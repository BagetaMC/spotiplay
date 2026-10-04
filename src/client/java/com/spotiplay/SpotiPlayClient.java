package com.spotiplay;

import com.spotiplay.audio.LocalAudioPlayer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class SpotiPlayClient implements ClientModInitializer {

    private static KeyBinding playKey;
    private static KeyBinding stopKey;
    private static String currentTrackName = "Zadna";

    @Override
    public void onInitializeClient() {
        LocalAudioPlayer.getMusicFolder();

        playKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spotiplay.play",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_8,
                "category.spotiplay.title"
        ));

        stopKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spotiplay.stop",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2,
                "category.spotiplay.title"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (playKey.wasPressed()) {
                File folder = LocalAudioPlayer.getMusicFolder();
                File[] files = folder.listFiles((dir, name) -> name.endsWith(".wav") || name.endsWith(".mp3"));

                if (files != null && files.length > 0) {
                    File trackToPlay = files[0];
                    currentTrackName = trackToPlay.getName();
                    LocalAudioPlayer.playSound(currentTrackName);

                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("§1[SpotiPlay] §fPrehravam: " + currentTrackName), true);
                    }
                } else {
                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("§c[SpotiPlay] Slozka config/spotiplay/music je prazdna!"), false);
                    }
                }
            }

            while (stopKey.wasPressed()) {
                LocalAudioPlayer.stopSound();
                currentTrackName = "Zastaveno";
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§c[SpotiPlay] Prehravani zastaveno."), true);
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null && !client.options.debugEnabled) {
                TextRenderer renderer = client.textRenderer;
                String status = "SpotiPlay: " + (LocalAudioPlayer.isPlaying() ? "§a" + currentTrackName : "§cVypnuto");
                drawContext.drawText(renderer, status, 10, 10, 0xFFFFFF, true);
            }
        });
    }
}
