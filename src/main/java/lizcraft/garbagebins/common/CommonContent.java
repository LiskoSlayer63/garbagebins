package lizcraft.garbagebins.common;

import lizcraft.garbagebins.GarbageBins;
import lizcraft.garbagebins.client.render.ItemStackRenderer;
import lizcraft.garbagebins.common.block.FluidBinBlock;
import lizcraft.garbagebins.common.block.GarbageBinBlock;
import lizcraft.garbagebins.common.gui.GarbageBinContainer;
import lizcraft.garbagebins.common.tile.FluidBinTileEntity;
import lizcraft.garbagebins.common.tile.GarbageBinTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonContent
{
	public static Block GARBAGEBIN_BLOCK;
	public static Block FLUIDBIN_BLOCK;
	
	public static Item GARBAGEBIN_ITEM;
	public static Item FLUIDBIN_ITEM;

	public static TileEntityType<GarbageBinTileEntity> GARBAGEBIN_TILEENTITYTYPE;
	public static TileEntityType<FluidBinTileEntity> FLUIDBIN_TILEENTITYTYPE;
	
	public static ContainerType<GarbageBinContainer> GARBAGEBIN_CONTAINER;
	
	public static SoundEvent GARBAGEBIN_OPEN;
	public static SoundEvent GARBAGEBIN_CLOSE;

	
	public static void register(IEventBus eventBus)
	{
		eventBus.register(CommonContent.class);
	}

	@SubscribeEvent
	public static void onBlocksRegistration(final RegistryEvent.Register<Block> event) 
	{
		GARBAGEBIN_BLOCK = new GarbageBinBlock(
				Block.Properties.create(Material.IRON, MaterialColor.GRAY).
				sound(SoundType.METAL).
				harvestTool(ToolType.PICKAXE).
				hardnessAndResistance(2.5F)
				).setRegistryName("garbagebin");

		FLUIDBIN_BLOCK = new FluidBinBlock(
				Block.Properties.create(Material.IRON, MaterialColor.GRAY).
				sound(SoundType.METAL).
				harvestTool(ToolType.PICKAXE).
				hardnessAndResistance(2.5F)
				).setRegistryName("fluidbin");
		
		
    	event.getRegistry().register(GARBAGEBIN_BLOCK);	
    	event.getRegistry().register(FLUIDBIN_BLOCK);
	}

	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> event) 
	{
		GARBAGEBIN_ITEM = new BlockItem(GARBAGEBIN_BLOCK, new Item.Properties().
				group(ItemGroup.DECORATIONS).
				setISTER(() -> () -> ItemStackRenderer.INSTANCE)
				).setRegistryName(GARBAGEBIN_BLOCK.getRegistryName());
		
		FLUIDBIN_ITEM = new BlockItem(FLUIDBIN_BLOCK, new Item.Properties().
				group(ItemGroup.DECORATIONS)
				).setRegistryName(FLUIDBIN_BLOCK.getRegistryName());
		
		
		event.getRegistry().register(GARBAGEBIN_ITEM);
		event.getRegistry().register(FLUIDBIN_ITEM);
	}

	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) 
	{
		GARBAGEBIN_TILEENTITYTYPE = TileEntityType.Builder.create(GarbageBinTileEntity::new, GARBAGEBIN_BLOCK).build(null);
		GARBAGEBIN_TILEENTITYTYPE.setRegistryName(GARBAGEBIN_BLOCK.getRegistryName());
		
		FLUIDBIN_TILEENTITYTYPE = TileEntityType.Builder.create(FluidBinTileEntity::new, FLUIDBIN_BLOCK).build(null);
		FLUIDBIN_TILEENTITYTYPE.setRegistryName(FLUIDBIN_BLOCK.getRegistryName());
		
		
		event.getRegistry().register(GARBAGEBIN_TILEENTITYTYPE);
		event.getRegistry().register(FLUIDBIN_TILEENTITYTYPE);
	}

	@SubscribeEvent
	public static void onContainersRegistration(final RegistryEvent.Register<ContainerType<?>> event)
	{
		GARBAGEBIN_CONTAINER = IForgeContainerType.create(GarbageBinContainer::new);
		GARBAGEBIN_CONTAINER.setRegistryName(GARBAGEBIN_BLOCK.getRegistryName());
		
		
		event.getRegistry().register(GARBAGEBIN_CONTAINER);
	}
	
	@SubscribeEvent
	public static void onSoundEventsRegistration(final RegistryEvent.Register<SoundEvent> event) 
	{
		GARBAGEBIN_OPEN = new SoundEvent(new ResourceLocation(GarbageBins.MOD_ID, "garbagebin_open")).setRegistryName("garbagebin_open");
		GARBAGEBIN_CLOSE = new SoundEvent(new ResourceLocation(GarbageBins.MOD_ID, "garbagebin_close")).setRegistryName("garbagebin_close");
		
		
    	event.getRegistry().register(GARBAGEBIN_OPEN);	
    	event.getRegistry().register(GARBAGEBIN_CLOSE);
	}
}