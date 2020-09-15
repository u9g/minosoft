/*
 * Codename Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.util.mojang.api;

public enum MojangURLs {
    STATUS("https://status.mojang.com/check"),
    BLOCKED_SERVERS("https://sessionserver.mojang.com/blockedservers"),
    LOGIN("https://authserver.mojang.com/authenticate"),
    JOIN("https://sessionserver.mojang.com/session/minecraft/join"),
    REFRESH("https://authserver.mojang.com/refresh");

    final String url;

    MojangURLs(String url) {
        this.url = url;
    }

    public static MojangURLs byUrl(String key) {
        for (MojangURLs url : values()) {
            if (url.getUrl().equals(key)) {
                return url;
            }
        }
        return null;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }
}
