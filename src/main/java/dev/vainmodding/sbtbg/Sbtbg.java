package dev.vainmodding.sbtbg;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Locale;

public class Sbtbg implements ClientModInitializer {

    public static final String MOD_ID = "sbtbg";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final SystemToast.SystemToastId TOAST_ID = new SystemToast.SystemToastId();

    private static SbtbgConfig config;
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        config = SbtbgConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.sbtbg.toggle",
                GLFW.GLFW_KEY_UNKNOWN,
                KeyMapping.Category.MULTIPLAYER
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                config.enabled = !config.enabled;
                config.save();
                toast(
                        Component.literal("SkyblockTexturesBegone " + (config.enabled ? "enabled" : "disabled")),
                        Component.literal(config.enabled
                                ? "Server resource packs will be blocked."
                                : "Server resource packs are allowed.")
                );
            }
        });
    }

    public static boolean shouldBlock() {
        if (config == null || !config.enabled) return false;
        return !isCurrentServerAllowed();
    }

    public static boolean fixMissingModels() {
        return config != null && config.fixMissingModels;
    }

    public static void onBlocked(ClientboundResourcePackPushPacket packet) {
        LOGGER.info("[SBTBG] Blocked server resource pack {} (required={})", packet.id(), packet.required());
        if (config != null && config.notify) {
            toast(
                    Component.literal("Blocked server resource pack"),
                    Component.literal("Kept your own textures."
                            + (packet.required() ? " (server marked it required)" : ""))
            );
        }
    }

    private static boolean isCurrentServerAllowed() {
        if (config.allowedServers == null || config.allowedServers.isEmpty()) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return false;
        ServerData server = mc.getCurrentServer();
        if (server == null || server.ip == null) return false;
        String ip = server.ip.toLowerCase(Locale.ROOT).trim();
        for (String allowed : config.allowedServers) {
            if (allowed == null) continue;
            String entry = allowed.toLowerCase(Locale.ROOT).trim();
            if (entry.isEmpty()) continue;
            if (ip.equals(entry) || ip.endsWith("." + entry)) return true;
        }
        return false;
    }

    private static void toast(Component title, Component message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        mc.execute(() -> SystemToast.addOrUpdate(mc.getToastManager(), TOAST_ID, title, message));
    }
}
