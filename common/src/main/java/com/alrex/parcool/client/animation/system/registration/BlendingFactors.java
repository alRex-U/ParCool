package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.BlendMethod;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.SimpleBlendFactor;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.client.animation.system.resource.Argument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class BlendingFactors extends BasicRegistry<IBlendingFactor, BlendingFactors.RegistrationEntry> {
    public interface BlendingFactorFactory {
        IBlendingFactor newInstance(Argument args, BlendMethod method);
    }

    @Nullable
    private static BlendingFactors INSTANCE = null;

    public static BlendingFactors getInstance() {
        if (INSTANCE == null) INSTANCE = new BlendingFactors();
        return INSTANCE;
    }

    BlendingFactors() {
    }

    public record RegistrationEntry(ResourceLocation name, BlendingFactorFactory factorFactory) {
    }

    private ID<IBlendingFactor> register(String subName, BlendingFactorFactory factor) {
        return register(ParCool.resourceLocation(subName), factor);
    }

    public ID<IBlendingFactor> register(ResourceLocation name, BlendingFactorFactory factor) {
        return registerItem(name, new RegistrationEntry(name, factor));
    }

    @Nullable
    public IBlendingFactor newInstance(ID<IBlendingFactor> id, Argument argument, BlendMethod method) {
        var entry = getRegistry().get(id);
        if (entry == null) return null;
        return entry.factorFactory.newInstance(argument, method);
    }

    @Nullable
    public IBlendingFactor newInstance(ResourceLocation name, Argument argument, BlendMethod method) {
        var id = getID(name);
        if (id == null) return null;
        return newInstance(id, argument, method);
    }

    public final ID<IBlendingFactor> ONE = register(
            "one",
            (args, method) -> new SimpleBlendFactor((player) -> 1, method)
    );
    public final ID<IBlendingFactor> TIME = register(
            "time",
            (args, method) -> {
                var max = Mth.clamp(args.request("max", 20f), 0, 100f);
                return new IBlendingFactor() {
                    private int tick;

                    @Override
                    public float getFactor(AbstractClientPlayer player, float partial) {
                        return EasingFunctions.QUAD.easeInOut(Mth.clamp((tick + partial) / max, 0f, 1f));
                    }

                    @Override
                    public void tick(AbstractClientPlayer player) {
                        tick++;
                    }

                    @Override
                    public BlendMethod getBlendMethod() {
                        return method;
                    }
                };
            }
    );
    public final ID<IBlendingFactor> VELOCITY = register(
            "velocity",
            (args, method) -> {
                var min = Mth.clamp(args.request("min", 0f), 0f, 10f);
                var max = Mth.clamp(args.request("max", 0.25f), min, 100f);
                return new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut((float) Mth.clamp((player.position().subtract(player.xo, player.yo, player.zo).length() - min) / (max - min), 0d, 1d)), method);
            }
    );
    public final ID<IBlendingFactor> VELOCITY_VERTICAL = register(
            "velocity_v",
            (args, method) -> {
                var min = Mth.clamp(args.request("min", 0f), 0f, 10f);
                var max = Mth.clamp(args.request("max", 0.25f), min, 100f);
                return new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut((float) Mth.clamp((player.position().y() - player.yo - min) / (max - min), 0d, 1d)), method);
            }
    );
    public final ID<IBlendingFactor> VELOCITY_HORIZONTAL = register(
            "velocity_h",
            (args, method) -> {
                var min = Mth.clamp(args.request("min", 0f), 0f, 10f);
                var max = Mth.clamp(args.request("max", 0.25f), min, 100f);
                return new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut((float) Mth.clamp(((new Vec3(player.position().x - player.xo, 0., player.position().z - player.zo)).length() - min) / (max - min), 0d, 1d)), method);
            }
    );
    public final ID<IBlendingFactor> VELOCITY_FORWARD = register(
            "velocity_forward",
            (args, method) -> {
                var min = Mth.clamp(args.request("min", 0f), 0f, 10f);
                var max = Mth.clamp(args.request("max", 0.25f), min, 100f);
                return new SimpleBlendFactor((player) -> EasingFunctions.QUAD.easeInOut((float) Mth.clamp(((new Vec3(player.position().x - player.xo, 0., player.position().z - player.zo)).dot(player.getLookAngle()) - min) / (max - min), 0d, 1d)), method);
            }
    );
}
