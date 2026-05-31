package nl.codersquack.nicejs.ccb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class BerryJsonReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public BerryJsonReloadListener() {
        super(GSON, "cb_data");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> omap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ConsumableBerries.LOGGER.info("JSON reload called!");
        for (Map.Entry<ResourceLocation, JsonElement> entry : omap.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            //JsonObject jsonObject = entry.getValue().getAsJsonObject();
            ConsumableBerries.LOGGER.info("Adding {} berries to BDM!", fileId.toString());
            //jsonObject.getAsJsonArray("berries").asList().forEach(el -> BerryDataRegistry.data.add(el.getAsString()));
        }
        JsonElement relevant = omap.get(ResourceLocation.fromNamespaceAndPath(ConsumableBerries.MODID, "effects"));
        BerryDataRegistry.effects = GSON.fromJson(relevant, BerryEffectsJson.class);
    }
}
