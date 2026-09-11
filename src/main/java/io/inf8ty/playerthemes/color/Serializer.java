package io.inf8ty.playerthemes.color;

public interface Serializer {
    String colorize(String text);

    enum Type {
        LEGACY, MINI_MESSAGE
    }
}
