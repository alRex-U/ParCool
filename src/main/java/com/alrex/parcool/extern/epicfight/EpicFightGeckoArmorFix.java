package com.alrex.parcool.extern.epicfight;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

/**
 * Epic Fight bakes armour from GeckoLib geometry with {@code GeoModelTransformer}. That baker assumes every cube
 * carries six quads: {@code GeoModelTransformer.getCenterOfCube} iterates {@code cube.quads()} and dereferences each
 * element, and the chest/limb part transformers do the same. GeckoLib, however, stores {@code null} in the slot of a
 * face that a per-face UV model omits, so any armour whose model leaves a face out (Iron's Spellbooks wandering
 * magician chestplate omits three faces on {@code armorBody}) makes the bake throw
 * {@code NullPointerException: Cannot invoke "GeoQuad.vertices()" because "quad" is null}. Epic Fight swallows the
 * exception, logs {@code Can't transform the model of ...}, and from then on renders that item through the vanilla
 * armour model - which has no matching texture, so the chestplate disappears in combat mode.
 * <p>
 * Rather than reimplementing the baker, this listener replaces each omitted face with a degenerate quad: four copies
 * of a real vertex of the same cube, carrying the normal of the missing side. The geometry stays where it was (the
 * vertices already bound the cube, so the centre-of-cube test is unchanged) and the added triangles have zero area,
 * so neither GeckoLib nor Epic Fight draws anything for them - but the baker no longer meets a {@code null}.
 * <p>
 * GeckoLib has no compile-time dependency here on purpose: the mod is optional for ParCool, so its members are
 * resolved reflectively and every failure degrades to "no fix" instead of a crash.
 */
@OnlyIn(Dist.CLIENT)
public final class EpicFightGeckoArmorFix extends SimplePreparableReloadListener<Void> {
    private static final Logger LOGGER = LogManager.getLogger();

    /** Quad slot order of {@code BakedModelFactory.buildQuads}. */
    private static final Direction[] QUAD_DIRECTIONS = {
            Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN
    };

    private static boolean registered = false;
    private static boolean unavailable = false;
    /** Set when resources are (re)loaded; the cache is only guaranteed populated once the reload finished. */
    private static volatile boolean pending = false;
    private static Method getBakedModels;
    private static Method topLevelBones;
    private static Method getCubes;
    private static Method getChildBones;
    private static Method quadArray;
    private static Method quadVertices;
    private static Method cubePivot;
    private static Constructor<?> quadConstructor;
    private static Constructor<?> vertexConstructor;
    private static Class<?> vertexClass;

    private EpicFightGeckoArmorFix() {
    }

    /**
     * Must run during mod construction: {@code RegisterClientReloadListenersEvent} is a mod-bus event that fires long
     * before {@code FMLLoadCompleteEvent}, where the rest of the client integration is wired up.
     */
    public static void init(IEventBus modBus) {
        if (registered) return;
        registered = true;
        modBus.addListener(EpicFightGeckoArmorFix::onRegisterClientReloadListeners);
        // The baked-model cache is filled by GeckoLib's own reload listener, whose position relative to this one is
        // not part of any contract, so the actual patching waits for the next client tick.
        NeoForge.EVENT_BUS.addListener(EpicFightGeckoArmorFix::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        if (!pending) return;
        pending = false;
        try {
            int patched = sanitize();
            if (patched > 0) {
                LOGGER.info("Filled {} GeckoLib face(s) omitted by their models so Epic Fight can bake those armour meshes", patched);
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            LOGGER.warn("Could not sanitize GeckoLib geometry for Epic Fight armour baking", e);
        }
    }

    private static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        if (!ModList.get().isLoaded("geckolib")) return;
        // Registered after GeckoLib's own listener (mod construction precedes client setup), so the baked model
        // cache is already populated when this one applies.
        event.registerReloadListener(new EpicFightGeckoArmorFix());
        LOGGER.debug("Registered GeckoLib geometry sanitizer for Epic Fight armour baking");
    }

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profiler) {
        pending = true;
    }

    private static int sanitize() throws ReflectiveOperationException {
        if (!resolve()) return 0;

        Map<?, ?> models = (Map<?, ?>) getBakedModels.invoke(null);
        if (models == null || models.isEmpty()) return 0;

        int patched = 0;
        for (Map.Entry<?, ?> entry : models.entrySet()) {
            Object model = entry.getValue();
            for (Object bone : (List<?>) topLevelBones.invoke(model)) {
                patched += sanitizeBone(bone);
            }
            if (patched > 0 && LOGGER.isDebugEnabled() && entry.getKey() instanceof ResourceLocation location) {
                LOGGER.debug("GeckoLib model {} contains omitted faces", location);
            }
        }
        return patched;
    }

    private static int sanitizeBone(Object bone) throws ReflectiveOperationException {
        int patched = 0;
        for (Object cube : (List<?>) getCubes.invoke(bone)) {
            patched += sanitizeCube(cube);
        }
        for (Object child : (List<?>) getChildBones.invoke(bone)) {
            patched += sanitizeBone(child);
        }
        return patched;
    }

    private static int sanitizeCube(Object cube) throws ReflectiveOperationException {
        Object[] quads = (Object[]) quadArray.invoke(cube);
        if (quads == null) return 0;

        Object sourceVertex = null;
        for (Object quad : quads) {
            if (quad == null) continue;
            Object[] vertices = (Object[]) quadVertices.invoke(quad);
            if (vertices != null && vertices.length > 0 && vertices[0] != null) {
                sourceVertex = vertices[0];
                break;
            }
        }
        if (sourceVertex == null) {
            // Fully omitted cube: fall back to its pivot so at least the baker gets a finite point.
            net.minecraft.world.phys.Vec3 point = (net.minecraft.world.phys.Vec3) cubePivot.invoke(cube);
            sourceVertex = vertexConstructor.newInstance(new Vector3f((float) point.x, (float) point.y, (float) point.z), 0.0F, 0.0F);
        }

        int patched = 0;
        for (int i = 0; i < quads.length && i < QUAD_DIRECTIONS.length; i++) {
            if (quads[i] != null) continue;
            Direction direction = QUAD_DIRECTIONS[i];
            // A typed array is required: Constructor.newInstance rejects an Object[] for a GeoVertex[] parameter.
            Object vertices = java.lang.reflect.Array.newInstance(vertexClass, 4);
            for (int v = 0; v < 4; v++) {
                java.lang.reflect.Array.set(vertices, v, sourceVertex);
            }
            quads[i] = quadConstructor.newInstance(vertices, new Vector3f(direction.getStepX(), direction.getStepY(), direction.getStepZ()), direction);
            patched++;
        }
        return patched;
    }

    /**
     * Resolves the GeckoLib members once. A missing/renamed member disables the fix permanently instead of
     * throwing on every reload.
     */
    private static boolean resolve() {
        if (unavailable) return false;
        if (getBakedModels != null) return true;

        try {
            Class<?> cacheClass = Class.forName("software.bernie.geckolib.cache.GeckoLibCache");
            Class<?> modelClass = Class.forName("software.bernie.geckolib.cache.object.BakedGeoModel");
            Class<?> boneClass = Class.forName("software.bernie.geckolib.cache.object.GeoBone");
            Class<?> cubeClass = Class.forName("software.bernie.geckolib.cache.object.GeoCube");
            Class<?> quadClass = Class.forName("software.bernie.geckolib.cache.object.GeoQuad");
            vertexClass = Class.forName("software.bernie.geckolib.cache.object.GeoVertex");

            Method models = cacheClass.getMethod("getBakedModels");
            if (!Modifier.isStatic(models.getModifiers())) {
                throw new NoSuchMethodException("GeckoLibCache.getBakedModels is not static");
            }
            quadConstructor = quadClass.getConstructor(vertexClass.arrayType(), Vector3f.class, Direction.class);
            vertexConstructor = vertexClass.getConstructor(Vector3f.class, float.class, float.class);

            getBakedModels = models;
            topLevelBones = modelClass.getMethod("topLevelBones");
            getCubes = boneClass.getMethod("getCubes");
            getChildBones = boneClass.getMethod("getChildBones");
            quadArray = cubeClass.getMethod("quads");
            quadVertices = quadClass.getMethod("vertices");
            cubePivot = cubeClass.getMethod("pivot");
            return true;
        } catch (ReflectiveOperationException | LinkageError e) {
            unavailable = true;
            LOGGER.warn("GeckoLib armour fix disabled: expected members are missing in the installed GeckoLib", e);
            return false;
        }
    }
}
