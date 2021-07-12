package com.extendedclip.papi.expansion.javascript.script.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ConfigurationMap implements Map<String, Object> {
    private final FileConfiguration configuration;

    public ConfigurationMap(final FileConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public int size() {
        return this.configuration.getKeys(false).size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(final Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            return false;
        }
        return keySet().stream()
                .anyMatch(key -> value.equals(get(key)));
    }

    @Override
    public Object get(final Object key) {
        return this.configuration.get(key.toString());
    }

    @Nullable
    @Override
    public Object put(String key, Object value) {
        final Object old = this.configuration.get(key);
        this.configuration.set(key, value);
        return old;
    }

    @Override
    public Object remove(Object key) {
        return put(key.toString(), null);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        for (final Map.Entry<? extends String, ?> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        keySet().forEach(this::remove);
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return configuration.getKeys(false);
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return keySet().stream().map(this::get).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return keySet().stream().map(key -> new Entry(key, get(key))).collect(Collectors.toSet());
    }

    private static final class Entry implements Map.Entry<String, Object> {
        private final String key;
        private Object value;

        public Entry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(final Object value) {
            final Object old = this.value;
            this.value = value;
            return old;
        }
    }

}
