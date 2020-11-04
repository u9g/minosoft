"""
* Minosoft
* Copyright (C) 2020 Lukas Eisenhauer
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
*
*  This software is not affiliated with Mojang AB, the original developer of Minecraft.
"""
#  Minosoft
#  Copyright (C) 2020 Moritz Zwerger
#
#  This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
#
#   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
#   You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
#
#   This software is not affiliated with Mojang AB, the original developer of Minecraft.

import json
import os

blockStates = {}
blockModels = {}

modName = "minecraft"

blockStatesDir = modName + "/blockstates/"
blockModelsDir = modName + "/models/block/"

print("loading blockstates...")


def readRotations(apply, current):
    if "x" in current:
        apply["x"] = current["x"]
    if "y" in current:
        apply["y"] = current["y"]
    if "z" in current:
        apply["z"] = current["z"]


def readPart(part):
    properties = []
    if "when" in part:
        when = part["when"]
        if "OR" in when:
            for item in when["OR"]:
                properties.append(item)
        else:
            properties.append(part["when"])
    apply = {}
    current = part["apply"]
    if type(current) == type([]):
        current = current[0]
    apply["model"] = current["model"].split("/")[1]
    readRotations(apply, current)
    result = []
    for item in properties:
        state = {}
        state["properties"] = item
        for i in apply:
            state[i] = apply[i]
        result.append(state)
    if len(result) == 0:
        result.append(apply)
    return result


for blockStateFile in os.listdir(blockStatesDir):
    with open(blockStatesDir + blockStateFile, "r") as file:
        data = json.load(file)
        block = {}
        if "variants" in data:
            variants = data["variants"]
            states = []
            for variant in variants:
                state = {}
                properties = {}
                if variant != "":
                    for part in variant.split(","):
                        properties[part.split("=")[0]] = part.split("=")[1]
                state["properties"] = properties
                current = variants[variant]
                if type(current) == type([]):
                    current = current[0]
                state["model"] = current["model"].split("/")[1]
                readRotations(state, current)
                states.append(state)
            block = {
                "states": states
            }
        elif "multipart" in data:
            parts = data["multipart"]
            conditional = []
            for part in parts:
                conditional.extend(readPart(part))
            block = {
                "conditional": conditional
            }
    blockStates[blockStateFile.split(".")[0]] = block

print("loading models...")
for blockModelFile in os.listdir(blockModelsDir):
    with open(blockModelsDir + blockModelFile, "r") as file:
        data = json.load(file)

    blockModels[blockModelFile.split(".")[0]] = data

print("combining files...")
finalJson = {
    "mod": modName,
    "blockStates": blockStates,
    "blockModels": blockModels,
    "tinted_textures": {
        "block/grass_block_top": [0, 1, 0],
        "block/grass": [0, 1, 0],
        "block/water_still": [0, 0, 1]
    }
}

print("saving...")
with open("blockModels.json", "w+") as file:
    json.dump(finalJson, file)

print("finished succesfully")
