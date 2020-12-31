/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */
package de.bixilon.minosoft.data.commands.parser

import de.bixilon.minosoft.data.commands.CommandStringReader
import de.bixilon.minosoft.data.commands.parser.exceptions.CommandParseException
import de.bixilon.minosoft.data.commands.parser.exceptions.identifier.ParticleNotFoundCommandParseException
import de.bixilon.minosoft.data.commands.parser.properties.ParserProperties
import de.bixilon.minosoft.data.mappings.particle.Particle
import de.bixilon.minosoft.data.mappings.particle.data.BlockParticleData
import de.bixilon.minosoft.data.mappings.particle.data.DustParticleData
import de.bixilon.minosoft.data.mappings.particle.data.ItemParticleData
import de.bixilon.minosoft.data.mappings.particle.data.ParticleData
import de.bixilon.minosoft.protocol.network.Connection

class ParticleParser : CommandParser() {

    @Throws(CommandParseException::class)
    override fun parse(connection: Connection, properties: ParserProperties?, stringReader: CommandStringReader): ParticleData {
        val identifier = stringReader.readModIdentifier()

        if (!connection.mapping.doesParticleExist(identifier.value)) {
            throw ParticleNotFoundCommandParseException(stringReader, identifier.key)
        }
        val particle = Particle(identifier.value.fullIdentifier)

        stringReader.skipWhitespaces()

        return when (identifier.value.fullIdentifier) {
            "minecraft:block", "minecraft:falling_dust" -> BlockParticleData(BlockStateParser.BLOCK_STACK_PARSER.parse(connection, properties, stringReader), particle)
            "minecraft:dust" -> {
                val red = stringReader.readFloat()
                stringReader.skipWhitespaces()
                val green = stringReader.readFloat()
                stringReader.skipWhitespaces()
                val blue = stringReader.readFloat()
                stringReader.skipWhitespaces()
                val scale = stringReader.readFloat()

                DustParticleData(red, green, blue, scale, particle)
            }
            "minecraft:item" -> {
                ItemParticleData(ItemStackParser.ITEM_STACK_PARSER.parse(connection, properties, stringReader), particle)
            }
            else -> ParticleData(particle)
        }
    }

    companion object {
        val PARTICLE_PARSER = ParticleParser()
    }
}