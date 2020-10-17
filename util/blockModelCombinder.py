"""
* Codename Minosoft
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
#  Codename Minosoft
#  Copyright (C) 2020 Moritz Zwerger
#
#  This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
#
#   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
#   You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
#
#   This software is not affiliated with Mojang AB, the original developer of Minecraft.

# The blockModels are contained in the minecraft.jar file. Extract the assets/minecraft folder and place this file in the same folder

import \
    json
import \
    os

blockStates = {}
blockModels = {}

modName = "minecraft"

blockStatesDir = modName + "/blockstates/"
blockModelsDir = modName + "/models/block/"

print(
    "loading blockstates...")
for blockStateFile in os.listdir(
        blockStatesDir):
    with open(
            blockStatesDir + blockStateFile,
            "r") as file:
        data = json.load(
            file)
    blockStates[
        blockStateFile.split(
            ".")[
            0]] = data

print(
    "counting models...")
blockModelList = []
for block in blockStates:
    if "variants" in \
            blockStates[
                block]:
        for variant in \
        blockStates[
            block][
            "variants"]:
            if type(
                    blockStates[
                        block][
                        "variants"][
                        variant]) == type(
                    {}):
                if not \
                blockStates[
                    block][
                    "variants"][
                    variant][
                    "model"] in blockModelList:
                    blockModelList.append(
                        blockStates[
                            block][
                            "variants"][
                            variant][
                            "model"])
            elif type(
                    blockStates[
                        block][
                        "variants"][
                        variant]) == type(
                    []):
                for subVariant in variant:
                    if not subVariant in blockModelList:
                        blockModelList.append(
                            subVariant)

    elif "multipart" in \
            blockStates[
                block]:
        for part in \
        blockStates[
            block][
            "multipart"]:
            if type(
                    part[
                        "apply"]) == type(
                    {}):
                if not \
                part[
                    "apply"][
                    "model"] in blockModelList:
                    blockModelList.append(
                        part[
                            "apply"][
                            "model"])
            else:
                for subPart in \
                part[
                    "apply"]:
                    if not \
                    subPart[
                        "model"] in blockModelList:
                        blockModelList.append(
                            subPart[
                                "model"])

    else:
        print(
            "FAILDED TO GET BLOCK MODELS FOR BLOCK " + block)

print(
    "loading models...")
for blockModelFile in os.listdir(
        blockModelsDir):
    with open(
            blockModelsDir + blockModelFile,
            "r") as file:
        data = json.load(
            file)
    blockModels[
        blockModelFile.split(
            ".")[
            0]] = data

print(
    "combining files...")
finalJson = {
    "blockStates": blockStates,
    "blockModels": blockModels,
    "tinted_textures": {
        "block/grass_block_top": [
            0,
            1,
            0],
        "block/grass": [
            0,
            1,
            0],
        "block/water_still": [
            0,
            0,
            1]
    }
}

print(
    "saving...")
with open(
        "blockModels.json",
        "w+") as file:
    json.dump(
        finalJson,
        file)

print(
    "finished succesfully")
