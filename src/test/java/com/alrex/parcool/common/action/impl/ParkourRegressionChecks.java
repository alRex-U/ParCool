package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.network.ListStreamCodec;
import com.alrex.parcool.utilities.MathUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public final class ParkourRegressionChecks {
    public static void main(String[] args) {
        check(Crawl.allowsSharedKey(false, true, false), "shared key must crawl while stationary");
        check(!Crawl.allowsSharedKey(false, true, true), "moving dodge takes priority");
        for (boolean direction : new boolean[] {false, true}) {
            check(Crawl.allowsSharedKey(true, true, direction), "crouching chooses crawl");
            check(Crawl.allowsSharedKey(false, false, direction), "independent crawl key works");
        }
        for (float angle : new float[] {-1080, -540, -180, -90, 0, 90, 180, 540, 1080}) {
            float normalized = MathUtil.normalizeDegree(angle);
            check(normalized >= -180 && normalized < 180, "degree normalization range");
            near(MathUtil.normalizeDegree(normalized), normalized);
            near(MathUtil.normalizeDegree(angle + 360), normalized);
            float radians = MathUtil.normalizeRadian((float) Math.toRadians(angle));
            near(Math.cos(radians), Math.cos(Math.toRadians(angle)));
            near(Math.sin(radians), Math.sin(Math.toRadians(angle)));
        }
        broadcastCodec();
        System.out.println(
                "Shared crawl/dodge key, angle normalization and broadcast codec checks passed");
    }

    private static void broadcastCodec() {
        StreamCodec<ByteBuf, Integer> integers =
                StreamCodec.of((buffer, value) -> buffer.writeInt(value), ByteBuf::readInt);
        var codec = new ListStreamCodec<>(integers);
        ByteBuf buffer = Unpooled.buffer();
        try {
            List<Integer> states = List.of(-1, 0, Integer.MAX_VALUE);
            codec.encode(buffer, states);
            codec.encode(buffer, List.of());
            check(codec.decode(buffer).equals(states), "all entries preserve broadcast order");
            check(codec.decode(buffer).isEmpty(), "empty broadcast round-trips");
            check(!buffer.isReadable(), "codec consumes exactly one encoded broadcast");
            buffer.writeInt(2).writeInt(42);
            try {
                codec.decode(buffer);
                throw new AssertionError("truncated broadcast must fail");
            } catch (IndexOutOfBoundsException expected) {
                // A partial state broadcast must never be accepted as a complete packet.
            }
        } finally {
            buffer.release();
        }
    }

    private static void near(double actual, double expected) {
        if (!Double.isFinite(actual) || Math.abs(actual - expected) > 1e-5)
            throw new AssertionError("expected " + expected + ", got " + actual);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
