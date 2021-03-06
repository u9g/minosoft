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

package de.bixilon.minosoft.nbt.tag;

import de.bixilon.minosoft.protocol.protocol.InByteBuffer;

public class LongTag implements Tag {
    final long value;

    public LongTag(long value) {
        this.value = value;
    }

    public LongTag(InByteBuffer buffer) {
        this.value = buffer.readLong();
    }

    @Override
    public TagTypes getType() {
        return TagTypes.LONG;
    }

    public long getValue() {
        return value;
    }
}
