package lizcraft.garbagebins.common;

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
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonContent
{
	public static Block GARBAGEBIN_BLOCK;
	public static Block FLUIDBIN_BLOCK;
	
	public static BlockItem GARBAGEBIN_ITEM;
	public static BlockItem FLUIDBIN_ITEM;

	public static TileEntityType<GarbageBinTileEntity> GARBAGEBIN_TILEENTITYTYPE;
	public static TileEntityType<FluidBinTileEntity> FLUIDBIN_TILEENTITYTYPE;
	
	public static ContainerType<GarbageBinContainer> GARBAGEBIN_CONTAINER;
	
	public static void init()
	{
		// Initialize blocks
		
		GARBAGEBIN_BLOCK = new GarbageBinBlock(
				Block.Properties.create(Material.ROCK, MaterialColor.GRAY).
				sound(SoundType.METAL).
				harvestTool(ToolType.PICKAXE)
				).setRegistryName("garbagebin");

		FLUIDBIN_BLOCK = new FluidBinBlock(
				Block.Properties.create(Material.ROCK, MaterialColor.GRAY).
				sound(SoundType.METAL).
				harvestTool(ToolType.PICKAXE)
				).setRegistryName("fluidbin");
		
		
		// Initialize items
		
		GARBAGEBIN_ITEM = (BlockItem) new BlockItem(GARBAGEBIN_BLOCK, new Item.Properties().
				group(ItemGroup.DECORATIONS).
				setISTER(() -> ItemStackRenderer::new)
				).setRegistryName(GARBAGEBIN_BLOCK.getRegistryName());
		
		FLUIDBIN_ITEM = (BlockItem) new BlockItem(FLUIDBIN_BLOCK, new Item.Properties().
				group(ItemGroup.DECORATIONS)
				).setRegistryName(FLUIDBIN_BLOCK.getRegistryName());
		
		
		// Initialize entity types
		
		GARBAGEBIN_TILEENTITYTYPE = TileEntityType.Builder.create(GarbageBinTileEntity::new, GARBAGEBIN_BLOCK).build(null);
		GARBAGEBIN_TILEENTITYTYPE.setRegistryName(GARBAGEBIN_BLOCK.getRegistryName());
		
		FLUIDBIN_TILEENTITYTYPE = TileEntityType.Builder.create(FluidBinTileEntity::new, FLUIDBIN_BLOCK).build(null);
		FLUIDBIN_TILEENTITYTYPE.setRegistryName(FLUIDBIN_BLOCK.getRegistryName());
		
		
		// Initialize containers
		
		GARBAGEBIN_CONTAINER = IForgeContainerType.create(GarbageBinContainer::new);
		GARBAGEBIN_CONTAINER.setRegistryName(GARBAGEBIN_BLOCK.getRegistryName());
	}
	
	public static void register(IEventBus eventBus)
	{
		eventBus.register(CommonContent.class);
	}

	@SubscribeEvent
	public static void onBlocksRegistration(final RegistryEvent.Register<Block> event) 
	{
    	event.getRegistry().register(GARBAGEBIN_BLOCK);	
    	event.getRegistry().register(FLUIDBIN_BLOCK);
	}

	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> event) 
	{
		event.getRegistry().register(GARBAGEBIN_ITEM);
		event.getRegistry().register(FLUIDBIN_ITEM);
	}

	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) 
	{
		event.getRegistry().register(GARBAGEBIN_TILEENTITYTYPE);
		event.getRegistry().register(FLUIDBIN_TILEENTITYTYPE);
	}

	@SubscribeEvent
	public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
	{
		event.getRegistry().register(GARBAGEBIN_CONTAINER);
	}
}