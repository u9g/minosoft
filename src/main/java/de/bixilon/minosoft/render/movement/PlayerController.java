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

package de.bixilon.minosoft.render.movement;

import de.bixilon.minosoft.game.datatypes.GameModes;
import de.bixilon.minosoft.game.datatypes.world.World;
import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.render.utility.Vec3;

import static de.bixilon.minosoft.render.utility.Vec3.mul;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class PlayerController {
    private final float playerHeight = 1.8f;
    private final float playerWidth = 0.25f;
    CameraMovement cameraMovement;
    PlayerMovement playerMovement;
    Vec3 playerPos = new Vec3(); // the feet position of the player
    Vec3 playerVelocity = new Vec3();
    float gravity = 9.81f;
    boolean onGround;
    private boolean enableGravity;
    private final CollisionHandler collisionHandler;
    public Vec3 oldPos;

    public PlayerController(long window) {
        cameraMovement = new CameraMovement();
        playerMovement = new PlayerMovement(window);
        collisionHandler = new CollisionHandler(this);
    }

    public void loop(float deltaTime) {
        if (!GameWindow.getConnection().getPlayer().isSpawnConfirmed()) {
            return;
        }
        if (GameWindow.paused) {
            cameraMovement.loop();
            glTranslatef(-playerPos.x, -(playerPos.y + playerHeight - 0.2f), -playerPos.z);
            return;
        }
        oldPos = playerPos.copy();

        GameModes gameMode = GameWindow.getConnection().getPlayer().getGameMode();
        enableGravity = gameMode != GameModes.CREATIVE && gameMode != GameModes.SPECTATOR;
        handleGravity(deltaTime);
        cameraMovement.loop();
        playerMovement.loop(deltaTime);
        applyVelocity(deltaTime);

        if (gameMode == GameModes.SPECTATOR) {
            return;
        }
        handleCollisions(GameWindow.getConnection().getPlayer().getWorld());

        glTranslatef(-playerPos.x, -(playerPos.y + playerHeight - 0.2f), -playerPos.z);
    }

    private void handleCollisions(World world) {
        onGround = false;
        if (world == null) {
            playerVelocity.zero();
            return;
        }
        collisionHandler.handleCollisions();
    }

    public boolean isEnableGravity() {
        return enableGravity;
    }

    public Vec3 getPlayerPos() {
        return playerPos;
    }

    public void setPlayerPos(Vec3 playerPos) {
        this.playerPos = playerPos;
    }

    private void applyVelocity(float deltaTime) {
        playerPos.add(mul(playerVelocity, deltaTime));
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
