package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.AnimatableProperty;
import com.alrex.parcool.client.animation.system.data.TimedValue;
import com.alrex.parcool.client.animation.system.data.Transition;
import com.alrex.parcool.client.animation.system.util.IResult;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.List;

public class JsonAnimationComponent {
    @Nullable
    @SerializedName("Head")
    private PartAnimationEntry head;
    @Nullable
    @SerializedName("Body")
    private PartAnimationEntry body;
    @Nullable
    @SerializedName("Right Arm")
    private PartAnimationEntry rightArm;
    @Nullable
    @SerializedName("Left Arm")
    private PartAnimationEntry leftArm;
    @Nullable
    @SerializedName("Right Leg")
    private PartAnimationEntry rightLeg;
    @Nullable
    @SerializedName("Left Leg")
    private PartAnimationEntry leftLeg;

    @Nullable
    public PartAnimationEntry get(AnimatableModelPart part) {
        return switch (part) {
            case BODY -> body;
            case HEAD -> head;
            case RIGHT_ARM -> rightArm;
            case LEFT_ARM -> leftArm;
            case RIGHT_LEG -> rightLeg;
            case LEFT_LEG -> leftLeg;
            default -> null;
        };
    }

    public static class PartAnimationEntry {
        @Nullable
        @SerializedName("loc.x")
        private List<TransitionEntry> locX;
        @Nullable
        @SerializedName("loc.y")
        private List<TransitionEntry> locY;
        @Nullable
        @SerializedName("loc.z")
        private List<TransitionEntry> locZ;
        @Nullable
        @SerializedName("rot_q.x")
        private List<TransitionEntry> rotQX;
        @Nullable
        @SerializedName("rot_q.y")
        private List<TransitionEntry> rotQY;
        @Nullable
        @SerializedName("rot_q.z")
        private List<TransitionEntry> rotQZ;
        @Nullable
        @SerializedName("rot_q.w")
        private List<TransitionEntry> rotQW;

        @Nullable
        public List<TransitionEntry> get(AnimatableProperty property) {
            return switch (property) {
                case TRANSLATE_X -> locX;
                case TRANSLATE_Y -> locY;
                case TRANSLATE_Z -> locZ;
                case ROTATION_W -> rotQW;
                case ROTATION_X -> rotQX;
                case ROTATION_Y -> rotQY;
                case ROTATION_Z -> rotQZ;
                default -> null;
            };
        }

        public static class TransitionEntry {
            private float t;
            private float v;
            @Nullable
            public InterpolationEntry i;

            public static class InterpolationEntry {
                private String typ;
                @Nullable
                private TimedValue cp1;
                @Nullable
                private TimedValue cp2;
                @Nullable
                private Transition.Easing.Type ease;
            }

            public IResult<Transition, String> parse(@Nullable TransitionEntry nextKeyframe) {
                var currentKeyFrame = new TimedValue(this.t, this.v);
                if (this.i == null) {
                    return new IResult.Success<>(new Transition.End(currentKeyFrame));
                }
                if (nextKeyframe == null) {
                    return new IResult.Error<>("A transition has no interpolation information, but next keyframe is not given");
                }
                switch (this.i.typ) {
                    case "CONST" -> {
                        return new IResult.Success<>(new Transition.Constant(currentKeyFrame));
                    }
                    case "LINEAR" -> {
                        return new IResult.Success<>(new Transition.Linear(currentKeyFrame));
                    }
                    case "BAZIER" -> {
                        if (this.i.cp1 == null || this.i.cp2 == null) {
                            return new IResult.Error<>("Bazier interpolation has not enough control points");
                        }
                        if (!(this.t <= this.i.cp1.time() && this.i.cp1.time() <= this.i.cp2.time())) {
                            return new IResult.Error<>("Bazier control points are not placed in time-increasing order");
                        }
                        return new IResult.Success<>(new Transition.BazierCubic(currentKeyFrame, this.i.cp1, this.i.cp2, new TimedValue(nextKeyframe.t, nextKeyframe.v)));
                    }
                    case "SINE", "QUAD", "CUBIC", "CIRCLE" -> {
                        if (this.i.ease == null) {
                            return new IResult.Error<>("Invalid easing interpolation type");
                        }
                        switch (this.i.typ) {
                            case "SINE" -> new IResult.Success<>(new Transition.Sine(currentKeyFrame, this.i.ease));
                            case "QUAD" -> new IResult.Success<>(new Transition.Quad(currentKeyFrame, this.i.ease));
                            case "CUBIC" -> new IResult.Success<>(new Transition.Cubic(currentKeyFrame, this.i.ease));
                            case "CIRCLE" -> new IResult.Success<>(new Transition.Circle(currentKeyFrame, this.i.ease));
                        }
                    }
                }
                return new IResult.Error<>("Unknown interpolation type");
            }
        }
    }
}
