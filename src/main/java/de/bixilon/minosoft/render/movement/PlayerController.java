/*
 * Minosoft
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

package de.bixilon.minosoft.render.movement;

import de.bixilon.minosoft.data.GameModes;
import de.bixilon.minosoft.data.world.World;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.render.utility.Vec3;

import static org.lwjgl.opengl.GL11.glTranslated;

public class PlayerController {
    private static final float playerHeight = 1.8f;
    private static final float playerWidth = 0.25f;
    private static final float gravity = 13;

    private final Connection connection;
    public Vec3 oldPos;
    CameraMovement cameraMovement;
    PlayerMovement playerMovement;
    Vec3 playerPos = new Vec3(); // the feet position of the player
    Vec3 playerVelocity = new Vec3();
    private boolean onGround;
    private boolean enableGravity;
    private CollisionHandler collisionHandler;

    public PlayerController(Connection connection) {
        this.connection = connection;
        cameraMovement = new CameraMovement(connection);
        playerMovement = new PlayerMovement(connection);
    }

    public void loop(float deltaTime) {
        if (!connection.getPlayer().isSpawnConfirmed()) {
            return;
        }
        if (collisionHandler == null) {
            collisionHandler = new CollisionHandler(connection);
        }
        if (GameWindow.paused) {
            cameraMovement.loop();
            glTranslated(-playerPos.x, -(playerPos.y + playerHeight - 0.2f), -playerPos.z);
            return;
        }
        oldPos = playerPos.copy();

        GameModes gameMode = connection.getPlayer().getGameMode();
        enableGravity = gameMode != GameModes.CREATIVE && gameMode != GameModes.SPECTATOR;
        handleGravity(deltaTime);
        cameraMovement.loop();
        playerMovement.loop(deltaTime);
        applyVelocity(deltaTime);

        if (gameMode == GameModes.SPECTATOR) {
            return;
        }
        handleCollisions(connection.getPlayer().getWorld());

        glTranslated(-playerPos.x, -(playerPos.y + playerHeight - 0.2f), -playerPos.z);
    }

    private void handleCollisions(World world) {
        onGround = false;
        if (world == null) {
            playerVelocity.zero();
            return;
        }
        collisionHandler.handleCollisions();
    }

    public boolean isGravityEnabled() {
        return enableGravity;
    }

    public Vec3 getPlayerPos() {
        return playerPos;
    }

    public void setPlayerPos(Vec3 playerPos) {
        this.playerPos = playerPos;
    }

    private void applyVelocity(float deltaTime) {
        playerPos.add(Vec3.mul(playerVelocity, deltaTime));
    }

    private void handleGravity(float deltaTime) {
        if (!enableGravity) {
            return;
        }
        // a rather accurate model for the real world, but minecraft does it differently
        playerVelocity.y -= gravity * deltaTime;
    }

    public CameraMovement getCameraMovement() {
        return cameraMovement;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void jump() {
        playerVelocity.y = 10;
        onGround = false;
    }

    public float getPlayerWidth() {
        return playerWidth;
    }

    public float getPlayerHeight() {
        return playerHeight;
    }
}
