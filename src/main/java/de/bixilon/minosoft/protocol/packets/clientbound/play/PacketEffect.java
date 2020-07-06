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

package de.bixilon.minosoft.protocol.packets.clientbound.play;

import de.bixilon.minosoft.game.datatypes.MapSet;
import de.bixilon.minosoft.game.datatypes.VersionValueMap;
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ClientboundPacket;
import de.bixilon.minosoft.protocol.protocol.InPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.PacketHandler;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class PacketEffect implements ClientboundPacket {
    // is this class used??? What about PacketParticle or PacketSoundEffect?
    EffectEffects effect;
    BlockPosition position;
    int data;
    boolean disableRelativeVolume; // normally only at MOB_ENDERDRAGON_END and MOB_WITHER_SPAWN, but we allow this everywhere

    @Override
    public boolean read(InPacketBuffer buffer) {
        switch (buffer.getVersion()) {
            case VERSION_1_7_10:
                this.effect = EffectEffects.byId(buffer.readInt(), buffer.getVersion());
                position = buffer.readBlockPosition();
                data = buffer.readInt();
                disableRelativeVolume = buffer.readBoolean();
                return true;
            case VERSION_1_8:
            case VERSION_1_9_4:
            case VERSION_1_10:
                this.effect = EffectEffects.byId(buffer.readInt(), buffer.getVersion());
                position = buffer.readPosition();
                data = buffer.readInt();
                disableRelativeVolume = buffer.readBoolean();
                return true;
        }

        return false;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Received effect packet at %s (effect=%s, data=%d, disableRelativeVolume=%s)", position.toString(), effect.name(), data, disableRelativeVolume));
    }

    @Override
    public void handle(PacketHandler h) {
        h.handle(this);
    }

    public BlockPosition getPosition() {
        return position;
    }

    public EffectEffects getEffect() {
        return effect;
    }

    public int getData() {
        return data;
    }

    public SmokeDirection getSmokeDirection() {
        if (effect == EffectEffects.PARTICLE_10_SMOKE) {
            return SmokeDirection.byId(data);
        }
        return null;
    }
    //ToDo all other dataTypes


    public boolean isDisableRelativeVolume() {
        return disableRelativeVolume;
    }

    public enum EffectEffects {

        RANDOM_CLICK(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1000), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        DISPENSER_DISPENSES(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1000)}),
        RANDOM_CLICK1(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1001), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        DISPENSER_FAILS(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1001)}),
        RANDOM_BOW(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1002), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        DISPENSER_SHOOTS(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1002)}),
        RANDOM_DOOR_OPEN_CLOSE(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1003), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        ENDER_EYE_LAUNCHED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1003)}),
        RANDOM_FIZZ(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1004), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        FIREWORK_SHOT(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1004)}),
        MUSIC_DISK(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1005), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1010)}), // data: recordId
        IRON_DOOR_OPENED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1005)}),
        WOODEN_DOOR_OPENED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1006)}),
        MOB_GHAST_CHARGE(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1007), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        WOODEN_TRAP_DOOR_OPENED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1007)}),
        MOB_GHAST_FIREBALL(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1008), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        FENCE_GATE_OPENED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1008)}),
        MOB_GHAST_FIREBALL_LOW(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1009), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        FIRE_EXTINGUISHED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1009)}),
        MOB_ZOMBIE_ATTACKS_WOOD_DOOR(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1010), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1019)}),
        MOB_ZOMBIE_ATTACKS_METAL_DOOR(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1011), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1020)}),
        IRON_DOOR_CLOSED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1011)}),
        MOB_ZOMBIE_WOODEN_DOOR_BREAK(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1012), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1021)}),
        WOODEN_DOOR_CLOSED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1012)}),
        MOB_WITHER_SPAWN(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1013), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1023)}),
        WOODEN_TRAP_DOOR_CLOSED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1013)}),
        MOB_WITHER_SHOOT(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1014), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1024)}),
        FENCE_GATE_CLOSED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1014)}),
        MOB_BAT_TAKEOFF(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1015), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1025)}),
        GHAST_WARNS(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1015)}),
        MOB_ZOMBIE_INFECT(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1016), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1026)}),
        GHAST_SHOOTS(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1016)}),
        MOB_ZOMBIE_UNFECT(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1017), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        ENDER_DRAGON_SHOOTS(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1017)}),
        MOB_ENDERDRAGON_DEATH(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1018), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1028)}),
        BLAZE_SHOOTS(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1018)}),
        ANVIL_BREAK(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1020), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1029)}),
        ANVIL_USE(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1021), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1030)}),
        ANVIL_LAND(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1022), new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1031)}),
        MOB_WITHER_BREAKS_BLOCKS(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1022)}),
        MOB_ZOMBIE_CONVERTED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1027)}),
        PORTAL_TRAVEL(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1032)}),
        CHORUS_FLOWER_GROWN(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1033)}),
        CHORUS_FLOWER_DIED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1034)}),
        BREWING_STAND_BREWED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1035)}),
        IRON_TRAP_DOOR_OPENED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1036)}),
        IRON_TRAP_DOOR_CLOSED(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 1037)}),

        PARTICLE_10_SMOKE(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2000)}), // data: smoke direction
        BLOCK_BREAK(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2001)}), // data: blockId
        SPLASH_POTION(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2002)}), //data: portionId
        EYE_OF_ENDER_BREAK_ANIMATION(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2003)}),
        MOB_SPAWN_SMOKE_FLAMES(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2004)}),
        SPAWN_HAPPY_VILLAGER(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2005), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}),
        BONE_MEAL_PARTICLES(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 2005)}),
        SPAWN_FALL_PARTICLES(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2006), new MapSet<>(ProtocolVersion.VERSION_1_9_4, -1)}), // data: fall damage (particle speed)
        DRAGON_BREATH(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 2006)}),

        END_GATEWAY_SPAWN(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 3000)}),
        MOB_ENDER_DRAGON_GROWL(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_9_4, 3001)});


        final VersionValueMap<Integer> valueMap;

        EffectEffects(MapSet<ProtocolVersion, Integer>[] values) {
            valueMap = new VersionValueMap<>(values, true);
        }

        public static EffectEffects byId(int id, ProtocolVersion version) {
            for (EffectEffects e : values()) {
                if (e.getId(version) == id) {
                    return e;
                }
            }
            return null;
        }

        public int getId(ProtocolVersion version) {
            Integer ret = valueMap.get(version);
            if (ret == null) {
                return -2;
            }
            return ret;
        }
    }

    public enum SmokeDirection {
        SOUTH_EAST(0),
        SOUTH(1),
        SOUTH_WEST(2),
        EAST(3),
        UP(4),
        WEST(5),
        NORTH_EAST(6),
        NORTH(7),
        NORTH_WEST(8);


        final int id;

        SmokeDirection(int id) {
            this.id = id;
        }

        public static SmokeDirection byId(int id) {
            for (SmokeDirection s : values()) {
                if (s.getId() == id) {
                    return s;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}
