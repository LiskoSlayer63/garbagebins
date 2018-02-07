package liznet.trashcansreborn;

import com.google.common.base.Preconditions;

import liznet.trashcansreborn.block.tile.TileTrashcan;
import liznet.trashcansreborn.block.tile.TileTrashcanFluid;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

public class RegistryHandler {
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();
		final Block[] blocks = {
				TrashCansReborn.trashcan,
				TrashCansReborn.trashcanFluid
		};
		registry.registerAll(blocks);
	}
	
	@SubscribeEvent
	public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		final ItemBlock[] items = { 
				new ItemBlock(TrashCansReborn.trashcan),
				new ItemBlock(TrashCansReborn.trashcanFluid)
		};

		for (final ItemBlock item : items) {
			final Block block = item.getBlock();
			final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
			registry.register(item.setRegistryName(registryName));
		}

		GameRegistry.registerTileEntity(TileTrashcan.class, "tiletrashcan");	
		GameRegistry.registerTileEntity(TileTrashcanFluid.class, "tiletrashcanfluid");
		
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TrashCansReborn.trashcan), 0, new ModelResourceLocation(TrashCansReborn.trashcan.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TrashCansReborn.trashcanFluid), 0, new ModelResourceLocation(TrashCansReborn.trashcanFluid.getRegistryName(), "inventory"));
		}	
	}
}
