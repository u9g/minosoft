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

package de.bixilon.minosoft.data.commands;

import de.bixilon.minosoft.data.commands.parser.CommandParser;
import de.bixilon.minosoft.data.commands.parser.exception.CommandParseException;
import de.bixilon.minosoft.data.commands.parser.properties.ParserProperties;
import de.bixilon.minosoft.protocol.protocol.InByteBuffer;
import de.bixilon.minosoft.util.BitByte;
import de.bixilon.minosoft.util.buffers.ImprovedStringReader;

import javax.annotation.Nullable;

public class CommandArgumentNode extends CommandLiteralNode {
    private final CommandParser parser;
    private final ParserProperties properties;
    private final SuggestionTypes suggestionType;

    public CommandArgumentNode(byte flags, InByteBuffer buffer) {
        super(flags, buffer);
        parser = CommandParser.createInstance(buffer.readIdentifier());
        properties = parser.readParserProperties(buffer);
        if (BitByte.isBitMask(flags, 0x10)) {
            String fullIdentifier = buffer.readIdentifier().getFullIdentifier();
            suggestionType = switch (fullIdentifier) {
                case "minecraft:ask_server" -> CommandArgumentNode.SuggestionTypes.ASK_SERVER;
                case "minecraft:all_recipes" -> CommandArgumentNode.SuggestionTypes.ALL_RECIPES;
                case "minecraft:available_sounds" -> CommandArgumentNode.SuggestionTypes.AVAILABLE_SOUNDS;
                case "minecraft:summonable_entities" -> CommandArgumentNode.SuggestionTypes.SUMMONABLE_ENTITIES;
                case "minecraft:available_biomes" -> CommandArgumentNode.SuggestionTypes.AVAILABLE_BIOMES;
                default -> throw new IllegalStateException("Unexpected value: " + fullIdentifier);
            };
        } else {
            suggestionType = null;
        }
    }

    public CommandParser getParser() {
        return parser;
    }

    @Nullable
    public ParserProperties getProperties() {
        return properties;
    }

    public SuggestionTypes getSuggestionType() {
        return suggestionType;
    }

    @Override
    public void isSyntaxCorrect(ImprovedStringReader stringReader) throws CommandParseException {
        parser.isParsable(properties, stringReader);
        super.isSyntaxCorrect(stringReader);
    }

    public enum SuggestionTypes {
        ASK_SERVER,
        ALL_RECIPES,
        AVAILABLE_SOUNDS,
        SUMMONABLE_ENTITIES,
        AVAILABLE_BIOMES
    }
}