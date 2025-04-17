package com.alrex.parcool.utilities;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.nio.ByteBuffer;

public class BufferUtil {
	ByteBuffer buffer;

	private BufferUtil(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public static BufferUtil wrap(ByteBuffer byteBuffer) {
		return new BufferUtil(byteBuffer);
	}

	public BufferUtil putBoolean(boolean bool) {
		buffer.put(bool ? (byte) 1 : 0);
		return this;
	}

	public static boolean getBoolean(ByteBuffer buffer) {
		return buffer.get() != 0;
	}

	public BufferUtil putBlockPos(BlockPos pos) {
		return putVector3i(pos);
	}

	public BufferUtil putVector3i(Vector3i vec) {
		buffer.putInt(vec.getX()).putInt(vec.getY()).putInt(vec.getZ());
		return this;
	}

	public BufferUtil putVector3d(Vector3d vec) {
		buffer.putDouble(vec.x()).putDouble(vec.y()).putDouble(vec.z());
		return this;
	}

	public static BlockPos getBlockPos(ByteBuffer buffer) {
		return new BlockPos(buffer.getInt(), buffer.getInt(), buffer.getInt());
	}

	public static Vector3i getVector3i(ByteBuffer buffer) {
		return new Vector3i(buffer.getInt(), buffer.getInt(), buffer.getInt());
	}

	public static Vector3d getVector3d(ByteBuffer buffer) {
		return new Vector3d(buffer.getDouble(), buffer.getDouble(), buffer.getDouble());
	}



	public ByteBuffer unwrap() {
		return buffer;
	}
}
