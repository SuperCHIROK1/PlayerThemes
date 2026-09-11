package io.inf8ty.playerthemes.database;

import com.zaxxer.hikari.HikariConfig;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@UtilityClass
public class PoolProvider {
    public void applySettings(HikariConfig config, ConfigurationSection section) {
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value == null) continue;

            String methodName = formatedMethodName(key);
            if (methodName != null) {
                invoke(config, methodName, value);
            }
        }
    }

    private void invoke(HikariConfig config, String methodName, Object value) {
        for (Method method : config.getClass().getMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterCount() != 1)
                continue;

            try {
                Method m = config.getClass().getMethod(methodName, method.getParameterTypes()[0]);
                m.invoke(config, cast(value, method.getParameterTypes()[0]));
                return;
            } catch (Exception ignored) {}
        }
    }

    private Object cast(Object obj, Class<?> clazz) {
        if (obj instanceof Number number) {
            if (clazz == long.class) return number.longValue();
            if (clazz == int.class) return number.intValue();
        }
        if (clazz == boolean.class) return Boolean.valueOf(String.valueOf(obj));
        return obj;
    }

    private @Nullable String formatedMethodName(String input) {
        if (input == null || input.isEmpty()) return null;

        String clean = input.toLowerCase();

        StringBuilder result = new StringBuilder("set");
        for (String part : clean.split("[-_]")) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1));
            }
        }

        return result.toString();
    }
}
