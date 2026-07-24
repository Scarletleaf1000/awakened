package me.scarletleaf1000.awakened;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import me.scarletleaf1000.awakened.breath.BreathAttributes;
import me.scarletleaf1000.awakened.client.AwakenedKeyMappings;
import me.scarletleaf1000.awakened.client.screens.VillagerBreathTradeScreen;
import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import me.scarletleaf1000.awakened.trade.VillagerBreathTradeMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Awakened.MOD_ID)
public class Awakened {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "awakened";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "awakened" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    // Create a Deferred Register to hold Items which will all be registered under the "awakened" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "awakened" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    // Create a Deferred Register to hold MenuTypes
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final TagKey<Item> AWAKENABLE_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(MOD_ID, "awakenable"));
    public static final TagKey<Item> UNAWAKENABLE_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(MOD_ID, "unawakenable"));

    public static final RegistryObject<Item> WOOD_DOLL = ITEMS.register("wood_doll", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STRAW_DOLL = ITEMS.register("straw_doll", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ROPE_COIL = ITEMS.register("rope_coil", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> AWAKENED_TAB = CREATIVE_MODE_TABS.register("awakened", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.awakened"))
            .icon(() -> new ItemStack(ROPE_COIL.get()))
            .displayItems((params, output) -> {
                output.accept(WOOD_DOLL.get());
                output.accept(STRAW_DOLL.get());
                output.accept(ROPE_COIL.get());
            })
            .build());

    public static final RegistryObject<MenuType<VillagerBreathTradeMenu>> VILLAGER_BREATH_TRADE_MENU =
            MENU_TYPES.register("villager_breath_trade", () -> IForgeMenuType.create(VillagerBreathTradeMenu::new));

    public Awakened() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the deferred registers to the mod event bus
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        BreathAttributes.ATTRIBUTES.register(modEventBus);

        // Register the command subsystem registries
        CommandRegistries.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BreathNetwork.register();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Awakened Starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() ->
                MenuScreens.register(Awakened.VILLAGER_BREATH_TRADE_MENU.get(), VillagerBreathTradeScreen::new)
            );
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(net.minecraftforge.client.event.RegisterKeyMappingsEvent event) {
            event.register(AwakenedKeyMappings.OPEN_AWAKENING);
        }


    }
}
