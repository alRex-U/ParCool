package com.alrex.parcool.client.input;

public enum LogicalMovement {
    FORWARD, BACKWARD, RIGHT, LEFT;

    public LogicalMovement inverse() {
        return switch (this) {
            case FORWARD -> BACKWARD;
            case BACKWARD -> FORWARD;
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
        };
    }

    public LogicalMovement right() {
        return switch (this) {
            case FORWARD -> RIGHT;
            case RIGHT -> BACKWARD;
            case BACKWARD -> LEFT;
            case LEFT -> FORWARD;
        };
    }

    public LogicalMovement left() {
        return switch (this) {
            case FORWARD -> LEFT;
            case LEFT -> BACKWARD;
            case BACKWARD -> RIGHT;
            case RIGHT -> FORWARD;
        };
    }
}
