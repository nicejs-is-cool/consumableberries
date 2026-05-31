package nl.codersquack.nicejs.ccb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CMBerryConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static File configFile;

    public static void init(File configDir) {
        configFile = new File(configDir, "cm_berries.json");
        if (!configFile.exists()) save();
        load();
    }
    public static void load() {
        try (FileReader reader = new FileReader(configFile)) {
            TypeToken<Map<String, BerryData>> mapType = new TypeToken<Map<String, BerryData>>(){};
            //LOGGER.info("unc still got it: {}", berries[0]);
            Map<String, BerryData> t = GSON.fromJson(reader, mapType);
            if (t == null) {
                ConsumableBerries.LOGGER.warn("cm_berries.json is null/empty, ignoring!");
                return;
            }
            BerryDataRegistry.data = t;
        } catch (IOException e) {
            ConsumableBerries.LOGGER.error("Failed to load cm_berries.json! This mod will not function properly.", e);
        }
    }
    public static void save() {
        try (FileOutputStream writer = new FileOutputStream(configFile)) {
            Resource res = Minecraft.getInstance().getResourceManager().getResourceOrThrow(
                    ResourceLocation.fromNamespaceAndPath(ConsumableBerries.MODID, "cb_data/cm_berries.json")
            );
            try (InputStream sires = res.open()) {
                sires.transferTo(writer);
                writer.close();
            }
        } catch (IOException e) {
            ConsumableBerries.LOGGER.error("Failed to save default cm_berries.json!", e);
        }
    }
}
