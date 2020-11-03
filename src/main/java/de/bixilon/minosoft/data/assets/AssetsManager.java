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

package de.bixilon.minosoft.data.assets;

import com.google.errorprone.annotations.DoNotCall;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import de.bixilon.minosoft.Minosoft;
import de.bixilon.minosoft.config.ConfigurationPaths;
import de.bixilon.minosoft.config.StaticConfiguration;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.logging.LogLevels;
import de.bixilon.minosoft.util.CountUpAndDownLatch;
import de.bixilon.minosoft.util.HTTP;
import de.bixilon.minosoft.util.Util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetsManager {
    public static final String ASSETS_INDEX_VERSION = "1.16"; // version.json -> assetIndex -> id
    public static final String ASSETS_INDEX_HASH = "8cf727ca683b8b133293a605571772306f0ee6b3"; // version.json -> assetIndex -> sha1
    public static final String ASSETS_CLIENT_JAR_VERSION = "1.16.4"; // version.json -> id
    public static final String ASSETS_CLIENT_JAR_HASH = "ea372b056349f6bf00d4e71b428274aa01183161"; // sha1 hash of file generated by minosoft (client jar file mappings: name -> hash)
    public static final String[] RELEVANT_ASSETS = {"minecraft/lang/", "minecraft/sounds/", "minecraft/textures/", "minecraft/font/"};

    private static final HashMap<String, String> assets = new HashMap<>();

    public static void downloadAssetsIndex() throws IOException {
        Util.downloadFileAsGz(String.format("https://launchermeta.mojang.com/v1/packages/%s/%s.json", ASSETS_INDEX_HASH, ASSETS_INDEX_VERSION), getAssetDiskPath(ASSETS_INDEX_HASH));
    }

    private static HashMap<String, String> parseAssetsIndex(String hash) throws IOException {
        InputStreamReader reader = readAssetByHash(hash);
        JsonObject json = JsonParser.parseReader(new JsonReader(reader)).getAsJsonObject();
        if (json.has("objects")) {
            json = json.getAsJsonObject("objects");
        }
        HashMap<String, String> ret = new HashMap<>();
        for (String key : json.keySet()) {
            JsonElement value = json.get(key);
            if (value.isJsonPrimitive()) {
                ret.put(key, value.getAsString());
                continue;
            }
            ret.put(key, value.getAsJsonObject().get("hash").getAsString());
        }
        return ret;
    }

    private static HashMap<String, String> parseAssetsIndex() throws IOException {
        HashMap<String, String> mappings = parseAssetsIndex(ASSETS_INDEX_HASH);
        mappings.putAll(parseAssetsIndex(ASSETS_CLIENT_JAR_HASH));
        return mappings;
    }

    public static void downloadAllAssets(CountUpAndDownLatch latch) throws IOException {
        if (assets.size() > 0) {
            return;
        }
        try {
            downloadAssetsIndex();
        } catch (IOException e) {
            Log.printException(e, LogLevels.DEBUG);
            Log.warn("Could not download assets index. Please check your internet connection");
        }
        assets.putAll(parseAssetsIndex(ASSETS_INDEX_HASH));
        latch.addCount(assets.size() + 1); // set size of mappings + 1 (for client jar assets)
        // download assets
        assets.keySet().parallelStream().forEach((filename) -> {
            try {
                String hash = assets.get(filename);
                if (!verifyAssetHash(hash)) {
                    AssetsManager.downloadAsset(hash);
                }
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // ToDo: This is strange. You will get a jvm crash without it on linux. Weired.
        /*
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */
        generateJarAssets();
        assets.putAll(parseAssetsIndex(ASSETS_CLIENT_JAR_HASH));
        latch.countDown();
    }

    public static boolean doesAssetExist(String name) {
        return assets.containsKey(name);
    }

    public static HashMap<String, String> getAssets() {
        return assets;
    }

    public static InputStreamReader readAsset(String name) throws IOException {
        return readAssetByHash(assets.get(name));
    }

    public static InputStream readAssetAsStream(String name) throws IOException {
        return readAssetAsStreamByHash(assets.get(name));
    }

    public static JsonElement readJsonAsset(String name) throws IOException {
        return readJsonAssetByHash(assets.get(name));
    }

    private static void downloadAsset(String hash) throws Exception {
        downloadAsset(String.format("https://resources.download.minecraft.net/%s/%s", hash.substring(0, 2), hash), hash);
    }

    private static InputStreamReader readAssetByHash(String hash) throws IOException {
        return new InputStreamReader(readAssetAsStreamByHash(hash));
    }

    private static InputStream readAssetAsStreamByHash(String hash) throws IOException {
        return new GZIPInputStream(new FileInputStream(getAssetDiskPath(hash)));
    }

    private static JsonElement readJsonAssetByHash(String hash) throws IOException {
        return JsonParser.parseReader(readAssetByHash(hash));
    }

    private static long getAssetSize(String hash) {
        File file = new File(getAssetDiskPath(hash));
        if (!file.exists()) {
            return -1;
        }
        return file.length();
    }

    private static boolean verifyAssetHash(String hash) {
        // file does not exist
        if (getAssetSize(hash) == -1) {
            return false;
        }
        if (!Minosoft.config.getBoolean(ConfigurationPaths.BooleanPaths.DEBUG_VERIFY_ASSETS)) {
            return true;
        }
        try {
            return hash.equals(Util.sha1Gzip(new File(getAssetDiskPath(hash))));
        } catch (IOException ignored) {
        }
        return false;
    }

    public static void generateJarAssets() throws IOException {
        long startTime = System.currentTimeMillis();
        Log.verbose("Generating client.jar assets...");
        if (verifyAssetHash(ASSETS_CLIENT_JAR_HASH)) {
            // ToDo: Verify all jar assets
            readAssetAsStreamByHash(ASSETS_CLIENT_JAR_HASH);
            Log.verbose("client.jar assets probably already generated, skipping");
            return;
        }
        JsonObject manifest = HTTP.getJson("https://launchermeta.mojang.com/mc/game/version_manifest.json").getAsJsonObject();
        String assetsVersionJsonUrl = null;
        for (JsonElement versionElement : manifest.getAsJsonArray("versions")) {
            JsonObject version = versionElement.getAsJsonObject();
            if (version.get("id").getAsString().equals(ASSETS_CLIENT_JAR_VERSION)) {
                assetsVersionJsonUrl = version.get("url").getAsString();
                break;
            }
        }
        if (assetsVersionJsonUrl == null) {
            throw new RuntimeException(String.format("Invalid version manifest or invalid ASSETS_CLIENT_JAR_VERSION (%s)", ASSETS_CLIENT_JAR_VERSION));
        }
        String versionJsonHash = assetsVersionJsonUrl.replace("https://launchermeta.mojang.com/v1/packages/", "").replace(String.format("/%s.json", ASSETS_CLIENT_JAR_VERSION), "");
        downloadAsset(assetsVersionJsonUrl, versionJsonHash);
        // download jar
        JsonObject clientJarJson = readJsonAssetByHash(versionJsonHash).getAsJsonObject().getAsJsonObject("downloads").getAsJsonObject("client");
        downloadAsset(clientJarJson.get("url").getAsString(), clientJarJson.get("sha1").getAsString());

        HashMap<String, String> clientJarAssetsHashMap = new HashMap<>();
        ZipInputStream versionJar = new ZipInputStream(readAssetAsStreamByHash(clientJarJson.get("sha1").getAsString()));
        ZipEntry currentFile;
        while ((currentFile = versionJar.getNextEntry()) != null) {
            if (!currentFile.getName().startsWith("assets") || currentFile.isDirectory()) {
                continue;
            }
            boolean relevant = false;
            for (String prefix : RELEVANT_ASSETS) {
                if (currentFile.getName().startsWith("assets/" + prefix)) {
                    relevant = true;
                    break;
                }
            }
            if (!relevant) {
                continue;
            }
            String hash = saveAsset(versionJar);

            clientJarAssetsHashMap.put(currentFile.getName().substring("assets/".length()), hash);
        }
        JsonObject clientJarAssetsMapping = new JsonObject();
        clientJarAssetsHashMap.forEach(clientJarAssetsMapping::addProperty);
        String json = new GsonBuilder().create().toJson(clientJarAssetsMapping);
        String assetHash = saveAsset(json.getBytes());
        Log.verbose(String.format("Generated jar assets in %dms (elements=%d, hash=%s)", (System.currentTimeMillis() - startTime), clientJarAssetsHashMap.size(), assetHash));
    }

    @DoNotCall
    private static String saveAsset(byte[] data) throws IOException {
        String hash = Util.sha1(data);
        String destination = getAssetDiskPath(hash);
        File outFile = new File(destination);
        if (outFile.exists() && outFile.length() > 0) {
            return hash;
        }
        Util.createParentFolderIfNotExist(destination);
        OutputStream out = new GZIPOutputStream(new FileOutputStream(destination));
        out.write(data);
        out.close();
        return hash;
    }

    private static String saveAsset(InputStream data) throws IOException {
        File tempDestinationFile = null;
        while (tempDestinationFile == null || tempDestinationFile.exists()) { // file exist? lol
            tempDestinationFile = new File(System.getProperty("java.io.tmpdir") + "/minosoft/" + Util.generateRandomString(32));
        }
        Util.createParentFolderIfNotExist(tempDestinationFile);

        OutputStream out = new GZIPOutputStream(new FileOutputStream(tempDestinationFile));
        MessageDigest crypt;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        byte[] buffer = new byte[4096];
        int length;
        while ((length = data.read(buffer, 0, 4096)) != -1) {
            crypt.update(buffer, 0, length);
            out.write(buffer);
        }
        out.close();
        String hash = Util.byteArrayToHexString(crypt.digest());

        // move file to desired destination
        File outputFile = new File(getAssetDiskPath(hash));
        Util.createParentFolderIfNotExist(outputFile);
        tempDestinationFile.renameTo(outputFile);
        return hash;
    }

    private static void downloadAsset(String url, String hash) throws IOException {
        if (verifyAssetHash(hash)) {
            return;
        }
        Log.verbose(String.format("Downloading %s -> %s", url, hash));
        Util.downloadFileAsGz(url, getAssetDiskPath(hash));
    }

    private static String getAssetDiskPath(String hash) {
        return StaticConfiguration.HOME_DIR + String.format("assets/objects/%s/%s.gz", hash.substring(0, 2), hash);
    }
}
