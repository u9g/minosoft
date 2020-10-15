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

package de.bixilon.minosoft.modding.loading;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.util.Util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ModInfo {
    final UUID uuid;
    final int versionId;
    final String versionName;
    final String name;
    final String[] authors;
    final String identifier;
    final String mainClass;
    LoadingInfo loadingInfo;

    public ModInfo(JsonObject json) {
        this.uuid = Util.getUUIDFromString(json.get("uuid").getAsString());
        this.versionId = json.get("versionId").getAsInt();
        this.versionName = json.get("versionName").getAsString();
        this.name = json.get("name").getAsString();
        JsonArray authors = json.get("authors").getAsJsonArray();
        this.authors = new String[authors.size()];
        AtomicInteger i = new AtomicInteger();
        authors.forEach((authorElement) -> this.authors[i.getAndIncrement()] = authorElement.getAsString());
        this.identifier = json.get("identifier").getAsString();
        this.mainClass = json.get("mainClass").getAsString();
        if (json.has("loading")) {
            JsonObject loading = json.getAsJsonObject("loading");
            this.loadingInfo = new LoadingInfo();
            if (loading.has("priority")) {
                this.loadingInfo.setLoadingPriority(Priorities.valueOf(loading.get("priority").getAsString()));
            }
        }
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMainClass() {
        return mainClass;
    }

    public LoadingInfo getLoadingInfo() {
        return loadingInfo;
    }

    public UUID getUUID() {
        return uuid;
    }


    public int getVersionId() {
        return versionId;
    }


    public String getVersionName() {
        return versionName;
    }


    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return String.format("name=\"%s\", uuid=%s, versionName=\"%s\", versionId=%d", getName(), getUUID(), getVersionName(), getVersionId());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode() * versionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (super.equals(obj)) {
            return true;
        }
        if (this.hashCode() != obj.hashCode()) {
            return false;
        }
        ModInfo their = (ModInfo) obj;
        return getUUID().equals(their.getUUID()) && getVersionId() == their.getVersionId();
    }
}
