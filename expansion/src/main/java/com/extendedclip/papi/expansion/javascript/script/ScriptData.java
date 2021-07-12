/*
 *
 * Javascript-Expansion
 * Copyright (C) 2020 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.javascript.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ScriptData {

    private final Map<String, Object> map;

    public ScriptData(final Map<String, Object> data) {
        this.map = data;
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(map);
    }

    public void clear() {
        map.clear();
    }

    public boolean exists(String key) {
        return map.get(key) != null;
    }

    public Object get(String key) {
        return map.get(key);
    }

    public void remove(String key) {
        map.put(key, null);
    }

    public void set(String key, Object value) {
        map.put(key, value);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
