package lizcraft.garbagebins.common;

import lizcraft.garbagebins.common.block.FluidBinBlock;
import lizcraft.garbagebins.common.block.GarbageBinBlock;
import lizcraft.garbagebins.common.block.entity.FluidBinBlockEntity;
import lizcraft.garbagebins.common.block.entity.GarbageBinBlockEntity;
import lizcraft.garbagebins.common.gui.FluidBinMenu;
import lizcraft.garbagebins.common.gui.GarbageBinMenu;
import lizcraft.garbagebins.common.item.GarbageBinItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static lizcraft.garbagebins.GarbageBins.MOD_ID;

import lizcraft.garbagebins.GarbageBins;

public class CommonContent
{
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GarbageBins.MOD_ID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GarbageBins.MOD_ID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCKENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GarbageBins.MOD_ID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, GarbageBins.MOD_ID);
	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GarbageBins.MOD_ID);
	
	
	// BLOCKS
	public static final RegistryObject<Block> GARBAGEBIN_BLOCK = BLOCKS.register("garbagebin", () -> 
		new GarbageBinBlock(BlockBehaviour.Properties.of(Material.METAL).color(MaterialColor.COLOR_GRAY).sound(SoundType.METAL).strength(2.5F)));
	public static final RegistryObject<Block> FLUIDBIN_BLOCK = BLOCKS.register("fluidbin", () -> 
		new FluidBinBlock(BlockBehaviour.Properties.of(Material.METAL).color(MaterialColor.COLOR_LIGHT_BLUE).sound(SoundType.METAL).strength(2.5F)));
	
	
	// ITEMS
	public static final RegistryObject<Item> GARBAGEBIN_ITEM = ITEMS.register("garbagebin", () -> 
		new GarbageBinItem(new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
	public static final RegistryObject<Item> FLUIDBIN_ITEM = ITEMS.register("fluidbin", () -> 
		new BlockItem(FLUIDBIN_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

	
	// BLOCK ENTITY TYPES
	public static final RegistryObject<BlockEntityType<GarbageBinBlockEntity>> GARBAGEBIN_BLOCKENTITY_TYPE = BLOCKENTITY_TYPES.register("garbagebin", () ->
		BlockEntityType.Builder.of(GarbageBinBlockEntity::new, GARBAGEBIN_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<FluidBinBlockEntity>> FLUIDBIN_BLOCKENTITY_TYPE = BLOCKENTITY_TYPES.register("fluidbin", () ->
		BlockEntityType.Builder.of(FluidBinBlockEntity::new, FLUIDBIN_BLOCK.get()).build(null));
	
	
	// MENU TYPES
	public static final RegistryObject<MenuType<GarbageBinMenu>> GARBAGEBIN_MENU = MENU_TYPES.register("garbagebin", () -> IForgeMenuType.create(GarbageBinMenu::new));
	public static final RegistryObject<MenuType<FluidBinMenu>> FLUIDBIN_MENU = MENU_TYPES.register("fluidbin", () -> IForgeMenuType.create(FluidBinMenu::new));
	
	
	// SOUND EVENTS
	public static final RegistryObject<SoundEvent> GARBAGEBIN_OPEN = SOUND_EVENTS.register("garbagebin_open", () -> 
		new SoundEvent(new ResourceLocation(MOD_ID, "garbagebin_open")));
	public static final RegistryObject<SoundEvent> GARBAGEBIN_CLOSE = SOUND_EVENTS.register("garbagebin_close", () -> 
		new SoundEvent(new ResourceLocation(MOD_ID, "garbagebin_close")));

	
	public static void register(IEventBus eventBus)
	{
		BLOCKS.register(eventBus);
		ITEMS.register(eventBus);
		BLOCKENTITY_TYPES.register(eventBus);
		MENU_TYPES.register(eventBus);
		SOUND_EVENTS.register(eventBus);
	}
}