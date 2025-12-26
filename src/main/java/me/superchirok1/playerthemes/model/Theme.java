package me.superchirok1.playerthemes.model;

public class Theme {

    private final String name;
    private final String value;
    private final String permission;
    private final String description;

    public String getDescription() {
        return description;
    }

    public Theme(String name, String value, String permission, String description) {
        this.name = name;
        this.value = value;
        this.permission = permission;
        this.description = description;
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
