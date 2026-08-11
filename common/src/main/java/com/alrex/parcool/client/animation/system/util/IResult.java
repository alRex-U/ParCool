package com.alrex.parcool.client.animation.system.util;

public interface IResult<Some, Err> {
    record Success<T, E>(T result) implements IResult<T, E> {
    }

    record Error<T, E>(E error) implements IResult<T, E> {
    }
}
