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
package com.extendedclip.papi.expansion.javascript.cloud;

import org.jetbrains.annotations.NotNull;

public final class GitScript {
    private final String name;
    private final String version;
    private final String author;
    private final String description;
    private final String url;

    public GitScript(@NotNull final String name, @NotNull final String version, @NotNull final String author, @NotNull final String description, @NotNull final String url) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.description = description;
        this.url = url;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getVersion() {
        return version;
    }

    @NotNull
    public String getAuthor() {
        return author;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public String getUrl() {
        return url;
    }
}
