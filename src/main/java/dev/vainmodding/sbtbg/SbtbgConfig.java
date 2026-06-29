package dev.vainmodding.sbtbg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SbtbgConfig {

    public boolean enabled = true;
    public boolean notify = true;
    public boolean fixMissingModels = true;
    public List<String> allowedServers = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path;

    public static SbtbgConfig load() {
        path = FabricLoader.getInstance().getConfigDir().resolve("sbtbg.json");
        SbtbgConfig cfg = null;
        try {
            if (Files.exists(path)) {
                cfg = GSON.fromJson(Files.readString(path), SbtbgConfig.class);
            }
        } catch (Exception e) {
            Sbtbg.LOGGER.warn("[SBTBG] Failed to read config, using defaults", e);
        }
        if (cfg == null) cfg = new SbtbgConfig();
        if (cfg.allowedServers == null) cfg.allowedServers = new ArrayList<>();
        cfg.save();
        return cfg;
    }

    public void save() {
        try {
            if (path == null) path = FabricLoader.getInstance().getConfigDir().resolve("sbtbg.json");
            Files.writeString(path, GSON.toJson(this));
        } catch (Exception e) {
            Sbtbg.LOGGER.warn("[SBTBG] Failed to write config", e);
        }
    }
}
