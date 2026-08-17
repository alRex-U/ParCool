package com.alrex.parcool.client.renderer;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.action.impl.Grapple;
import com.alrex.parcool.common.grapple.GrapplePhase;
import com.alrex.parcool.common.grapple.GrapplePhysics;
import com.alrex.parcool.client.renderer.entity.GrappleTipModel;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GrappleRopeRenderer {
    public static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/misc/grapple_rope.png");

    public static final ResourceLocation TIP_TEXTURE_LOCATION = ParCool.resourceLocation("textures/item/grappling_hook_model.png");

    private static GrappleTipModel tipModel = null;

    private static final float ROPE_RADIUS = 0.04f;

    private static final double SEGMENT_LENGTH = 0.6;

    private static final double TEXTURE_REPEAT_LENGTH = 1.0;
    private static final int MAX_SEGMENTS = 80;
    private static final double MAX_SAG = 0.45;

    private static final double CORNER_RADIUS = 0.4;
    private static final int CORNER_STEPS = 7;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) return;
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        float partialTick = event.getPartialTick();
        Camera camera = event.getCamera();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        List<RopePath> paths = null;
        for (Player player : level.players()) {
            RopePath path = buildPath(minecraft, player, camera, partialTick);
            if (path == null) continue;
            if (paths == null) paths = new ArrayList<>(2);
            paths.add(path);
        }
        if (paths == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPosition = camera.getPosition();
        VertexConsumer consumer = bufferSource.getBuffer(RenderTypes.GRAPPLE_ROPE);

        poseStack.pushPose();
        poseStack.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        Matrix4f matrix = poseStack.last().pose();
        for (RopePath path : paths) {
            renderRope(matrix, consumer, level, path);
        }
        bufferSource.endBatch(RenderTypes.GRAPPLE_ROPE);

        VertexConsumer tipConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TIP_TEXTURE_LOCATION));
        for (RopePath path : paths) {
            renderTip(poseStack, tipConsumer, level, path);
        }
        poseStack.popPose();
        bufferSource.endBatch(RenderType.entityCutoutNoCull(TIP_TEXTURE_LOCATION));
    }

    private static void renderTip(PoseStack poseStack, VertexConsumer consumer, Level level, RopePath path) {
        List<Vec3> points = path.points();
        if (points.size() < 2) return;
        if (tipModel == null) {
            tipModel = new GrappleTipModel(Minecraft.getInstance().getEntityModels().bakeLayer(ParCoolModelLayers.GRAPPLE_TIP));
        }

        Vec3 end = points.get(points.size() - 1);
        Vec3 along = end.subtract(points.get(points.size() - 2));
        double length = along.length();
        if (length < 1.0e-6) return;

        Vec3 up = along.scale(-1 / length);
        double horizontal = Math.sqrt(up.x * up.x + up.z * up.z);

        poseStack.pushPose();
        poseStack.translate(end.x, end.y, end.z);
        poseStack.mulPose(Vector3f.YP.rotation((float) Math.atan2(up.x, up.z)));
        poseStack.mulPose(Vector3f.XP.rotation((float) Math.atan2(horizontal, up.y)));

        poseStack.scale(-1f, -1f, 1f);
        tipModel.render(poseStack, consumer, lightAt(level, end), OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private record RopePath(List<Vec3> points, Grapple grapple) {
    }

    private static RopePath buildPath(Minecraft minecraft, Player player, Camera camera, float partialTick) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return null;
        Grapple grapple = parkourability.get(ParCoolActions.GRAPPLE);
        if (!grapple.isDoing()) return null;
        Vec3 anchor = grapple.getAnchor();
        if (anchor == null) return null;

        Vec3 origin = ropeOrigin(minecraft, player, camera, partialTick);
        GrapplePhase phase = grapple.getPhase();

        if (phase != GrapplePhase.ATTACHED) {
            Vec3 hook = origin.add(anchor.subtract(origin).scale(grapple.getFlightProgress(partialTick)));
            List<Vec3> flying = new ArrayList<>();
            appendStraight(flying, origin, hook, 0, Vec3.ZERO);
            return new RopePath(flying, grapple);
        }

        List<Vec3> corners = new ArrayList<>(4);
        List<Vec3> bends = grapple.getBends();
        if (!bends.isEmpty()) {
            for (int i = bends.size() - 1; i >= 0; i--) {
                corners.add(bends.get(i));
            }
        } else {
            Vec3 pivot = grapple.getPivot();
            if (pivot != null && pivot.distanceToSqr(anchor) > 1.0e-4) corners.add(pivot);
        }

        List<Vec3> nodes = new ArrayList<>(corners.size() + 2);
        nodes.add(origin);
        nodes.addAll(corners);
        nodes.add(anchor);

        double slack = grapple.getRopeLength() - origin.distanceTo(nodes.get(1));
        return new RopePath(buildPolyline(nodes, slack, grapple.getRopeWobble()), grapple);
    }

    private static List<Vec3> buildPolyline(List<Vec3> nodes, double slack, Vec3 wobble) {
        int count = nodes.size();
        List<Vec3> points = new ArrayList<>();
        Vec3 from = nodes.get(0);

        for (int i = 1; i < count; i++) {
            Vec3 corner = nodes.get(i);
            boolean isCorner = i < count - 1;

            Vec3 enter = corner;
            Vec3 exit = corner;
            if (isCorner) {
                Vec3 incoming = corner.subtract(from);
                Vec3 outgoing = nodes.get(i + 1).subtract(corner);
                double incomingLength = incoming.length();
                double outgoingLength = outgoing.length();
                if (incomingLength > 1.0e-4 && outgoingLength > 1.0e-4) {
                    double radius = Math.min(CORNER_RADIUS, Math.min(incomingLength, outgoingLength) * 0.45);
                    enter = corner.subtract(incoming.scale(radius / incomingLength));
                    exit = corner.add(outgoing.scale(radius / outgoingLength));
                }
            }

            appendStraight(points, from, enter, i == 1 ? slack : 0, i == 1 ? wobble : Vec3.ZERO);
            if (isCorner) {
                appendCorner(points, enter, corner, exit);
                from = exit;
            }
        }
        return points;
    }

    private static void appendStraight(List<Vec3> points, Vec3 from, Vec3 to, double slack, Vec3 wobble) {
        double length = from.distanceTo(to);
        int steps = Mth.clamp(Mth.ceil(length / SEGMENT_LENGTH), 1, MAX_SEGMENTS);
        double sag = 0;
        if (slack > 0 && length > 1.0e-6) {
            double horizontal = Math.sqrt(Mth.square(to.x - from.x) + Mth.square(to.z - from.z)) / length;
            sag = Math.min(slack * 0.2, MAX_SAG)
                    * horizontal
                    * ParCool.getConfig().client().grapplingHook.ropeSag().get();
        }
        boolean bowed = wobble.lengthSqr() > 1.0e-8;

        for (int i = points.isEmpty() ? 0 : 1; i <= steps; i++) {
            double t = (double) i / steps;
            Vec3 point = from.add(to.subtract(from).scale(t));

            double bulge = 4 * t * (1 - t);
            if (sag > 0) {
                point = point.subtract(0, sag * bulge, 0);
            }
            if (bowed) {
                point = point.add(wobble.scale(bulge));
            }
            points.add(point);
        }
    }

    private static void appendCorner(List<Vec3> points, Vec3 enter, Vec3 corner, Vec3 exit) {
        for (int i = 1; i <= CORNER_STEPS; i++) {
            double t = (double) i / CORNER_STEPS;
            double inverse = 1 - t;
            points.add(new Vec3(
                    inverse * inverse * enter.x + 2 * inverse * t * corner.x + t * t * exit.x,
                    inverse * inverse * enter.y + 2 * inverse * t * corner.y + t * t * exit.y,
                    inverse * inverse * enter.z + 2 * inverse * t * corner.z + t * t * exit.z
            ));
        }
    }

    private static Vec3 ropeOrigin(Minecraft minecraft, Player player, Camera camera, float partialTick) {
        boolean firstPerson = player == minecraft.player
                && minecraft.options.getCameraType().isFirstPerson()
                && camera.getEntity() == player;
        if (firstPerson) {
            Vec3 position = camera.getPosition();
            Vec3 look = toVec3(camera.getLookVector());
            Vec3 up = toVec3(camera.getUpVector());
            Vec3 right = toVec3(camera.getLeftVector()).reverse();
            return position.add(look.scale(0.45)).add(right.scale(0.3)).subtract(up.scale(0.28));
        }

        double x = Mth.lerp(partialTick, player.xo, player.getX());
        double y = Mth.lerp(partialTick, player.yo, player.getY());
        double z = Mth.lerp(partialTick, player.zo, player.getZ());
        float bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        Vec3 forward = Vec3.directionFromRotation(0, bodyYaw);
        Vec3 right = new Vec3(-forward.z, 0, forward.x);

        double side = player.getMainArm() == HumanoidArm.RIGHT ? 0.22 : -0.22;
        return new Vec3(x, y + GrapplePhysics.ATTACH_HEIGHT, z)
                .add(right.scale(side))
                .add(forward.scale(0.10));
    }

    private static Vec3 toVec3(com.mojang.math.Vector3f vector) {
        return new Vec3(vector.x(), vector.y(), vector.z());
    }

    private static void renderRope(Matrix4f matrix, VertexConsumer consumer, Level level, RopePath path) {
        List<Vec3> points = path.points();
        int count = points.size();
        if (count < 2) return;

        Vec3[] tangents = new Vec3[count];
        for (int i = 0; i < count; i++) {
            Vec3 tangent = i == 0
                    ? points.get(1).subtract(points.get(0))
                    : i == count - 1
                    ? points.get(count - 1).subtract(points.get(count - 2))
                    : points.get(i + 1).subtract(points.get(i - 1));
            double length = tangent.length();
            tangents[i] = length < 1.0e-8 ? new Vec3(0, -1, 0) : tangent.scale(1 / length);
        }

        Vec3[] sides = new Vec3[count];
        Vec3[] others = new Vec3[count];

        Vec3 side = advanceFrame(path.grapple(), tangents[0]);
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                Vec3 previous = tangents[i - 1];
                Vec3 axis = previous.cross(tangents[i]);
                double sin = axis.length();
                if (sin > 1.0e-9) {
                    side = rotateAround(side, axis.scale(1 / sin), Math.atan2(sin, previous.dot(tangents[i])));
                }
            }

            side = side.subtract(tangents[i].scale(side.dot(tangents[i])));
            side = side.lengthSqr() < 1.0e-10 ? anyPerpendicular(tangents[i]) : side.normalize();
            sides[i] = side;
            others[i] = tangents[i].cross(side);
        }

        int startLight = lightAt(level, points.get(0));
        int endLight = lightAt(level, points.get(count - 1));
        int lastIndex = count - 1;
        double travelled = 0;

        for (int i = 0; i < lastIndex; i++) {
            Vec3 from = points.get(i);
            Vec3 to = points.get(i + 1);
            int lightFrom = lightAlong(startLight, endLight, (float) i / lastIndex);
            int lightTo = lightAlong(startLight, endLight, (float) (i + 1) / lastIndex);

            float vFrom = (float) (travelled / TEXTURE_REPEAT_LENGTH);
            travelled += from.distanceTo(to);
            float vTo = (float) (travelled / TEXTURE_REPEAT_LENGTH);

            for (int face = 0; face < 4; face++) {
                Vec3 a0 = corner(sides[i], others[i], face);
                Vec3 b0 = corner(sides[i], others[i], face + 1);
                Vec3 a1 = corner(sides[i + 1], others[i + 1], face);
                Vec3 b1 = corner(sides[i + 1], others[i + 1], face + 1);

                float shade = faceShade(a0.add(b0));
                float u0 = face / 4f;
                float u1 = (face + 1) / 4f;

                vertex(matrix, consumer, from.add(a0.scale(ROPE_RADIUS)), u0, vFrom, lightFrom, shade);
                vertex(matrix, consumer, from.add(b0.scale(ROPE_RADIUS)), u1, vFrom, lightFrom, shade);
                vertex(matrix, consumer, to.add(b1.scale(ROPE_RADIUS)), u1, vTo, lightTo, shade);
                vertex(matrix, consumer, to.add(a1.scale(ROPE_RADIUS)), u0, vTo, lightTo, shade);
            }
        }
    }

    private static int lightAlong(int startLight, int endLight, float phase) {
        return LightTexture.pack(
                (int) Mth.lerp(phase, LightTexture.block(startLight), LightTexture.block(endLight)),
                (int) Mth.lerp(phase, LightTexture.sky(startLight), LightTexture.sky(endLight))
        );
    }

    private static Vec3 advanceFrame(Grapple grapple, Vec3 tangent) {
        Vec3 previousSide = grapple.getRenderFrameSide();
        Vec3 previousTangent = grapple.getRenderFrameTangent();

        Vec3 side;
        if (previousSide == null || previousTangent == null) {
            side = anyPerpendicular(tangent);
        } else {
            side = previousSide;
            Vec3 axis = previousTangent.cross(tangent);
            double sin = axis.length();
            if (sin > 1.0e-9) {
                side = rotateAround(side, axis.scale(1 / sin), Math.atan2(sin, previousTangent.dot(tangent)));
            }
        }
        side = side.subtract(tangent.scale(side.dot(tangent)));
        side = side.lengthSqr() < 1.0e-10 ? anyPerpendicular(tangent) : side.normalize();
        grapple.setRenderFrame(side, tangent);
        return side;
    }

    private static Vec3 anyPerpendicular(Vec3 tangent) {
        Vec3 reference = Math.abs(tangent.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        return tangent.cross(reference).normalize();
    }

    private static Vec3 rotateAround(Vec3 vector, Vec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return vector.scale(cos)
                .add(axis.cross(vector).scale(sin))
                .add(axis.scale(axis.dot(vector) * (1 - cos)));
    }

    private static float faceShade(Vec3 outward) {
        double length = outward.length();
        if (length < 1.0e-8) return 1f;
        Vec3 normal = outward.scale(1 / length);
        float shade = (float) (0.62 + 0.38 * (0.5 + 0.5 * normal.y));
        return shade * (float) (1 - 0.12 * Math.abs(normal.x));
    }

    private static Vec3 corner(Vec3 side, Vec3 other, int index) {
        return switch (index % 4) {
            case 0 -> side.add(other);
            case 1 -> side.subtract(other);
            case 2 -> side.reverse().subtract(other);
            default -> side.reverse().add(other);
        };
    }

    private static void vertex(Matrix4f matrix, VertexConsumer consumer, Vec3 position, float u, float v, int light, float shade) {
        consumer.vertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .color(shade, shade, shade, 1f)
                .uv(u, v)
                .uv2(light)
                .endVertex();
    }

    private static int lightAt(Level level, Vec3 position) {
        BlockPos pos = new BlockPos(position);
        return LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
    }
}
