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

package de.bixilon.minosoft.data.mappings.particle.data;

import de.bixilon.minosoft.data.mappings.particle.Particle;

public class DustParticleData extends ParticleData {
    final float red;
    final float green;
    final float blue;
    final float scale;

    public DustParticleData(float red, float green, float blue, float scale, Particle type) {
        super(type);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return String.format("{red=%s, green=%s, blue=%s, scale=%s)", red, green, blue, scale);
    }
}