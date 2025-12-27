package me.superchirok1.playerthemes.model;

import java.util.Map;

public record Theme(
        String name,
        String value,
        String permission,
        String description,
        Map<String, String> values
) {
        public String getValueByKey(String key) {
                return values != null
                        ? values.getOrDefault(key, "")
                        : "";
        }

}