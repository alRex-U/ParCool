package com.alrex.parcool.api.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class SynchronizedProperty<T> {
    private boolean dirty;
    @Nullable
    private T value;
    @Nullable
    private final IUpdateListener<T> updateListener;

    private SynchronizedProperty(@Nullable IUpdateListener<T> updateListener) {
        this.updateListener = updateListener;
    }

    @Nullable
    public T get() {
        return value;
    }


    public T getOrDefaultIfNull(T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public void set(@Nullable T value) {
        if (!Objects.equals(this.value, value)) {
            if (updateListener != null) updateListener.onUpdate(value, this.value);
            this.dirty = true;
            this.value = value;
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void sync(T value) {
        if (updateListener != null) updateListener.onUpdate(value, this.value);
        this.value = value;
    }

    public interface IUpdateListener<T> {
        void onUpdate(T newValue, T oldValue);
    }

    abstract IHandler<T> getHandler();

    interface IHandler<T> {
        byte dataLengthInBytes();

        @Nullable
        T read(ByteBuffer buffer);

        void write(ByteBuffer buffer, @Nullable T value);
    }

    public record Handler<T>(byte dataLengthInBytes, Function<ByteBuffer, T> reader,
                             BiConsumer<ByteBuffer, T> writer) implements IHandler<T> {
        @Override
        public T read(ByteBuffer buffer) {
            return reader.apply(buffer);
        }

        @Override
        public void write(ByteBuffer buffer, T value) {
            writer.accept(buffer, value);
        }
    }

    private static final Handler<Boolean> BOOLEAN_HANDLER = new Handler<>(
            (byte) 1,
            (b) -> switch (b.get()) {
                case 0 -> Boolean.FALSE;
                case 1 -> Boolean.TRUE;
                default -> null;
            },
            (b, v) -> b.put(v == null ? (byte) 255 : (v ? (byte) 1 : (byte) 0)));

    private static final Handler<java.lang.Byte> BYTE_HANDLER = new Handler<>(
            (byte) 2,
            (b) -> {
                var notNull = b.get();
                var v = b.get();
                return notNull != 0 ? v : null;
            },
            (b, v) -> {
                b.put(v != null ? (byte) 1 : (byte) 0);
                b.put(v != null ? v : 0);
            }
    );
    private static final Handler<java.lang.Float> FLOAT_HANDLER = new Handler<>(
            (byte) (Float.BYTES + 1),
            (b) -> {
                var notNull = b.get();
                return notNull != 0 ? b.getFloat() : null;
            },
            (b, v) -> {
                b.put(v != null ? (byte) 1 : (byte) 0);
                b.putFloat(v != null ? v : Float.NaN);
            }
    );
    private static final Handler<Vec3> VEC_3_HORIZONTAL_HANDLER = new Handler<>(
            (byte) (Float.BYTES * 2),
            (b) -> {
                var x = b.getFloat();
                var z = b.getFloat();
                if (Float.isNaN(x) && Float.isNaN(z)) {
                    return null;
                }
                return new Vec3(x, 0, z);
            },
            (b, v) -> {
                if (v == null) {
                    b.putFloat(Float.NaN).putFloat(Float.NaN);
                } else {
                    b.putFloat((float) v.x).putFloat((float) v.z);
                }
            }
    );
    private static final Handler<Vec3> VEC_3_HANDLER = new Handler<>(
            (byte) (Float.BYTES * 3),
            (b) -> {
                var x = b.getFloat();
                var y = b.getFloat();
                var z = b.getFloat();
                if (Float.isNaN(x) && Float.isNaN(y) && Float.isNaN(z)) {
                    return null;
                }
                return new Vec3(x, y, z);
            },
            (b, v) -> {
                if (v == null) {
                    b.putFloat(Float.NaN).putFloat(Float.NaN).putFloat(Float.NaN);
                } else {
                    b.putFloat((float) v.x).putFloat((float) v.y).putFloat((float) v.z);
                }
            }
    );
    private static final Handler<Vec3i> VEC_3_I_HANDLER = new Handler<>(
            (byte) (Integer.BYTES * 3 + 1),
            (b) -> {
                var isNotNull = b.get();
                var x = b.getInt();
                var y = b.getInt();
                var z = b.getInt();
                return isNotNull != 0 ? new Vec3i(x, y, z) : null;
            },
            (b, v) -> {
                if (v == null) {
                    b.put((byte) 0).putInt(0).putInt(0).putInt(0);
                } else {
                    b.put((byte) 1).putInt(v.getX()).putInt(v.getY()).putInt(v.getZ());
                }
            }
    );
    private static final Handler<BlockPos> BLOCK_POS_HANDLER = new Handler<>(
            VEC_3_I_HANDLER.dataLengthInBytes,
            (b) -> {
                var vec3i = VEC_3_I_HANDLER.read(b);
                return vec3i != null ? new BlockPos(vec3i) : null;
            },
            VEC_3_I_HANDLER::write
    );

    public static SynchronizedProperty<Boolean> newBoolean() {
        return newBoolean(null);
    }

    public static SynchronizedProperty<Boolean> newBoolean(@Nullable IUpdateListener<Boolean> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<Boolean> getHandler() {
                return BOOLEAN_HANDLER;
            }
        };
    }

    public static SynchronizedProperty<java.lang.Byte> newByte() {
        return newByte(null);
    }

    public static SynchronizedProperty<java.lang.Byte> newByte(@Nullable IUpdateListener<java.lang.Byte> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<java.lang.Byte> getHandler() {
                return BYTE_HANDLER;
            }
        };
    }

    public static SynchronizedProperty<java.lang.Float> newFloat() {
        return newFloat(null);
    }

    public static SynchronizedProperty<java.lang.Float> newFloat(@Nullable IUpdateListener<java.lang.Float> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<java.lang.Float> getHandler() {
                return FLOAT_HANDLER;
            }
        };
    }

    public static SynchronizedProperty<Vec3> newVec3Horizontal() {
        return newVec3Horizontal(null);
    }

    public static SynchronizedProperty<Vec3> newVec3Horizontal(@Nullable IUpdateListener<Vec3> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<Vec3> getHandler() {
                return VEC_3_HORIZONTAL_HANDLER;
            }
        };
    }

    public static SynchronizedProperty<Vec3> newVec3() {
        return newVec3(null);
    }

    public static SynchronizedProperty<Vec3> newVec3(@Nullable IUpdateListener<Vec3> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<Vec3> getHandler() {
                return VEC_3_HANDLER;
            }
        };
    }

    public static SynchronizedProperty<Vec3i> newVec3i() {
        return newVec3i(null);
    }

    public static SynchronizedProperty<Vec3i> newVec3i(@Nullable IUpdateListener<Vec3i> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<Vec3i> getHandler() {
                return VEC_3_I_HANDLER;
            }
        };
    }

    public static SynchronizedProperty<BlockPos> newBlockPos() {
        return newBlockPos(null);
    }

    public static SynchronizedProperty<BlockPos> newBlockPos(@Nullable IUpdateListener<BlockPos> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<BlockPos> getHandler() {
                return BLOCK_POS_HANDLER;
            }
        };
    }

    private static class EnumHandler<T extends Enum<T>> implements IHandler<T> {
        private final Class<T> enumClass;

        public EnumHandler(Class<T> enumClass) {
            this.enumClass = enumClass;
            assert (this.enumClass.getEnumConstants().length < 255);
        }

        @Override
        public byte dataLengthInBytes() {
            return (byte) 1;
        }

        @Override
        public T read(ByteBuffer buffer) {
            var value = buffer.get();
            if (value == (byte) 255) return null;
            var constants = enumClass.getEnumConstants();
            return constants[value % constants.length];
        }

        @Override
        public void write(ByteBuffer buffer, T value) {
            if (value == null) buffer.put((byte) 255);
            else buffer.put((byte) value.ordinal());
        }
    }

    private static class EnumSynchronizationProperty<T extends Enum<T>> extends SynchronizedProperty<T> {
        private final EnumHandler<T> enumHandler;

        public EnumSynchronizationProperty(Class<T> enumClass, @Nullable IUpdateListener<T> updateListener) {
            super(updateListener);
            enumHandler = new EnumHandler<>(enumClass);
        }

        @Override
        IHandler<T> getHandler() {
            return enumHandler;
        }
    }

    public static <T extends Enum<T>> SynchronizedProperty<T> newEnum(Class<T> enumClass) {
        return newEnum(enumClass, null);
    }

    public static <T extends Enum<T>> SynchronizedProperty<T> newEnum(Class<T> enumClass, @Nullable IUpdateListener<T> updateListener) {
        return new EnumSynchronizationProperty<>(enumClass, updateListener);
    }
}
