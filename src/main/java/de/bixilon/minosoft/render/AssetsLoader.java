/*
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
 */

package de.bixilon.minosoft.render;

import de.bixilon.minosoft.render.blockModels.BlockModelLoader;
import de.bixilon.minosoft.render.entityModels.EntityModelLoader;
import de.bixilon.minosoft.render.texture.TextureLoader;

public class AssetsLoader {
    TextureLoader textureLoader;
    BlockModelLoader blockModelLoader;
    EntityModelLoader entityModelLoader;

    public AssetsLoader() {
        blockModelLoader = new BlockModelLoader();
        entityModelLoader = new EntityModelLoader();
        textureLoader = new TextureLoader(blockModelLoader.getTextures(), blockModelLoader.getTints());
        blockModelLoader.applyTextures(textureLoader);
    }


    public TextureLoader getTextureLoader() {
        return textureLoader;
    }

    public BlockModelLoader getBlockModelLoader() {
        return blockModelLoader;
    }

    public EntityModelLoader getEntityModelLoader() {
        return entityModelLoader;
    }
}
