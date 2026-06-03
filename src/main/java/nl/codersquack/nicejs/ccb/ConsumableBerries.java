package nl.codersquack.nicejs.ccb;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.registries.*;
import nl.codersquack.nicejs.ccb.effects.FreezingMobEffect;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ConsumableBerries.MODID)
public class ConsumableBerries {
    public static class BerryEffectContext {
        private final LivingEntityUseItemEvent.Finish event;
        private final BerryEffect effect;
        public BerryEffectContext(LivingEntityUseItemEvent.Finish event, BerryEffect effect) {
            this.event = event;
            this.effect = effect;
        }

        public LivingEntityUseItemEvent.Finish getEvent() {
            return event;
        }

        public BerryEffect getEffect() {
            return effect;
        }
    }
    // Define mod id in a common place for everything to reference
    public static final String MODID = "consumableberries";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "consumableberries" namespace
    //public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "consumableberries" namespace
    //public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "consumableberries" namespace
    //public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "consumableberries:example_block", combining the namespace and path
    //public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "consumableberries:example_block", combining the namespace and path
    //public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "consumableberries:example_id", nutrition 1 and saturation 2
    //public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
    //        .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "consumableberries:example_tab" for the example item, that is placed after the combat tab
    /*public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.consumableberries")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());*/
    public static final Gson gson = new Gson();
    public static final HashMap<ResourceLocation, Consumer<BerryEffectContext>> specialEffects = new HashMap<>();
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
    public static final Object FREEZING_EFFECT = MOB_EFFECTS.register("freezing",
            () -> new FreezingMobEffect(MobEffectCategory.HARMFUL, 0x000080));
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ConsumableBerries(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        //BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        //ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        //CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ConsumableBerries) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        //NeoForge.EVENT_BUS.unregister(sol_valheim);
        if (ModList.get().isLoaded("sol_valheim_reforged")) {
            LOGGER.info("Sol Valheim Reforged is present, applying CanEatCheckProcedure override!");
            NeoForge.EVENT_BUS.unregister(sol_valheim_reforged.procedures.CanEatCheckProcedure.class);
            NeoForge.EVENT_BUS.register(CanEatCheckProcedureOverride.class);
        }
        specialEffects.put(ResourceLocation.fromNamespaceAndPath(MODID, "set_fire"), (ctx) -> {
            //LOGGER.info("it is i, the special effect");
            LivingEntity entity = ctx.getEvent().getEntity();
            entity.setSharedFlagOnFire(true);
            entity.setRemainingFireTicks(ctx.getEffect().duration);
        });
        /*specialEffects.put(ResourceLocation.fromNamespaceAndPath(MODID, "set_freeze"), ctx -> {
            LivingEntity entity = ctx.getEvent().getEntity();
            //entity.setTicksFrozen(ctx.getEffect().duration);
            entity.setIsInPowderSnow(true);
        });*/

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);
        //modEventBus.register(ModBusEvents.class);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        // cobblemon:aspear_berry
        //Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "aspear_berry"));
        MOB_EFFECTS.register(modEventBus);

    }
    @EventBusSubscriber(modid = ConsumableBerries.MODID)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void modifyComponents(ModifyDefaultComponentsEvent event) {
            //Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "aspear_berry"));
            LOGGER.info("ModifyComponents fired!");
            /*try {
                Resource res = Minecraft.getInstance().getResourceManager().getResourceOrThrow(
                        ResourceLocation.fromNamespaceAndPath(MODID, "cb_data/cm_berries.json")
                );
                LOGGER.info("cm_berries.json loaded!");
                InputStream sires = res.open();
                String strRes = new String(sires.readAllBytes(), StandardCharsets.UTF_8);
                TypeToken<Map<String, BerryData>> mapType = new TypeToken<Map<String, BerryData>>(){};
                //LOGGER.info("unc still got it: {}", berries[0]);
                BerryDataRegistry.data = gson.fromJson(strRes, mapType);
                sires.close();
            } catch(Throwable err) {
                LOGGER.error("Failed to load cm_berries.json! The mod will not work.", err);
            }*/
            BerryDataRegistry.data.forEach((key, val) -> {
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(key));
                if (item == null) {
                    LOGGER.warn("Can't find item {}, skipping", item);
                    return;
                }
                event.modify(item, builder -> {
                    FoodProperties.Builder props = new FoodProperties.Builder();
                    if (val.alwaysEdible) props.alwaysEdible();
                    props.nutrition(val.nutrition);
                    props.saturationModifier(val.saturationModifier);
                    builder.set(DataComponents.FOOD, props.build());
                });
            });
            /*event.modify(item, builder -> {
                builder.set(DataComponents.FOOD, new FoodProperties.Builder()
                        .alwaysEdible()
                        .nutrition(1)
                        .saturationModifier(2f)
                        .build());
            });
            Item item2 = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "leppa_berry"));
            event.modify(item2, builder -> {
                builder.set(DataComponents.FOOD, new FoodProperties.Builder()
                        .alwaysEdible()
                        .nutrition(1)
                        .saturationModifier(2f)
                        .build());
            });*/

        }
    }
    @SubscribeEvent
    public void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new BerryJsonReloadListener());
    }
    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        //LOGGER.info("item used/ate {} {}", event.getDuration(), event.getEntity().level().isClientSide());
        if (event.getEntity().level().isClientSide()) return;
        if (BerryDataRegistry.effects.effects == null) return;
        BerryEffect[] ef = BerryDataRegistry.effects.effects.get(BuiltInRegistries.ITEM.getKey(event.getItem().getItem()).toString());
        if (ef == null) return;
        LivingEntity ent = event.getEntity();
        for (BerryEffect eff : ef) {
            if (eff.chance != 100 && Minecraft.getInstance().level != null) {
                int roll = Minecraft.getInstance().level.getRandom().nextInt(100);
                //LOGGER.info("rolled {}", roll);
                if (roll >= eff.chance) {
                    continue;
                }
            }
            ResourceLocation rl = ResourceLocation.parse(eff.id);
            Optional<Holder.Reference<MobEffect>> omef = BuiltInRegistries.MOB_EFFECT.getHolder(rl);
            if (eff.removeRandomEffect) {
                /*for (Map.Entry<ResourceKey<MobEffect>, MobEffect> a : BuiltInRegistries.MOB_EFFECT.entrySet()) {
                    BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(a.getKey());
                }*/
                Collection<MobEffectInstance> entityEffects = ent.getActiveEffects();
                if (entityEffects.isEmpty()) continue;
                int pickedEffect = Minecraft.getInstance().level.getRandom().nextInt(entityEffects.size());
                MobEffectInstance inst = (MobEffectInstance) entityEffects.toArray()[pickedEffect];
                ent.removeEffect(inst.getEffect());
                continue;
            }
            if (omef.isEmpty()) {
                if (!specialEffects.containsKey(rl)) continue;
                //LOGGER.info("calling special effect");
                try {
                    specialEffects.get(rl).accept(new BerryEffectContext(event, eff));
                } catch(Throwable e) {
                    LOGGER.error("Caught exception while executing special effect {}: ", rl, e);
                }
                continue;
            }

            Holder<MobEffect> mef = omef.get();
            if (eff.duration == 0) {
                ent.removeEffect(mef);
                continue;
            }
            MobEffectInstance inst = new MobEffectInstance(mef, eff.duration, eff.amplifier, false, eff.showParticles, eff.showIcon);
            ent.addEffect(inst);
            //ent.addEffect()
        }
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        /*LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));*/
        CMBerryConfig.init(FMLPaths.CONFIGDIR.get().toFile());
    }

    // Add the example block item to the building blocks tab
    /*private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }*/

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        //LOGGER.info("HELLO from server starting");
    }
}
