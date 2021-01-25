package lizcraft.garbagebins.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.tile.GarbageBinTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ItemStackRenderer extends ItemStackTileEntityRenderer 
{
	public static final ItemStackTileEntityRenderer INSTANCE = new ItemStackRenderer();
	
	private final GarbageBinTileEntity garbageBin = new GarbageBinTileEntity();
	
	public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) 
	{
		Item item = stack.getItem();
		
		if (item instanceof BlockItem) 
		{
			Block block = ((BlockItem)item).getBlock();
			TileEntity tileEntity;
			
			if (block == CommonContent.GARBAGEBIN_BLOCK)
				tileEntity = this.garbageBin;
			else
				return;
			
			TileEntityRendererDispatcher.instance.renderItem(tileEntity, matrixStack, buffer, combinedLight, combinedOverlay);
		}
	}
}
