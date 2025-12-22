package me.superchirok1.playerthemes.model;

public class Theme {

    private final String name;
    private final String value;
    private final String permission;

    public Theme(String name, String value, String permission) {
        this.name = name;
        this.value = value;
        this.permission = permission;
    }

    public String getValue() {
        return value;
    }

    public String getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

}
