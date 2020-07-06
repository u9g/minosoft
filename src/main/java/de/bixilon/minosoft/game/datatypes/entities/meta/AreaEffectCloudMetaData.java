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
package de.bixilon.minosoft.game.datatypes.entities.meta;

import de.bixilon.minosoft.game.datatypes.particle.Particles;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

import java.util.HashMap;

public class AreaEffectCloudMetaData extends MobMetaData {

    public AreaEffectCloudMetaData(HashMap<Integer, MetaDataSet> sets, ProtocolVersion version) {
        super(sets, version);
    }


    public float getRadius() {
        switch (version) {
            case VERSION_1_9_4:
                return (float) sets.get(5).getData();
            case VERSION_1_10:
                return (float) sets.get(6).getData();
        }
        return 0;
    }

    public int getColor() {
        switch (version) {
            case VERSION_1_9_4:
                return (int) sets.get(6).getData();
            case VERSION_1_10:
                return (int) sets.get(7).getData();
        }
        return 0;
    }

    public boolean ignoreRadius() {
        switch (version) {
            case VERSION_1_9_4:
                return (boolean) sets.get(7).getData();
            case VERSION_1_10:
                return (boolean) sets.get(8).getData();
        }
        return false;
    }

    public Particles getParticle() {
        switch (version) {
            case VERSION_1_9_4:
                return Particles.byType((int) sets.get(8).getData());
            case VERSION_1_10:
                return Particles.byType((int) sets.get(9).getData());
        }
        return null;
    }

    public int getParticleParameter1() {
        switch (version) {
            case VERSION_1_10:
                return (int) sets.get(10).getData();
        }
        return 0;
    }

    public int getParticleParameter2() {
        switch (version) {
            case VERSION_1_10:
                return (int) sets.get(11).getData();
        }
        return 0;
    }


}
