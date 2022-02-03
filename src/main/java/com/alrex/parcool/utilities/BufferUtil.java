package com.alrex.parcool.utilities;

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

	public ByteBuffer unwrap() {
		return buffer;
	}
}
