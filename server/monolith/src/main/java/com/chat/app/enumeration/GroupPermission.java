package com.chat.app.enumeration;

public enum GroupPermission {
    PRIVATE,
    SELF_PRIVATE,
    PUBLIC;


    public static GroupPermission getPermission(String permission) {
        return switch (permission) {
            case "PRIVATE" -> PRIVATE;
            case "SELF_PRIVATE" -> SELF_PRIVATE;
            case "PUBLIC" -> PUBLIC;
            default -> null;
        };
    }
}
