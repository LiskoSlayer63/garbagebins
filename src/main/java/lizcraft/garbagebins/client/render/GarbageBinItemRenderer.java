package lizcraft.garbagebins.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.block.GarbageBinBlock;
import lizcraft.garbagebins.common.block.entity.GarbageBinBlockEntity;
import lizcraft.garbagebins.common.item.GarbageBinItem;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class GarbageBinItemRenderer extends BlockEntityWithoutLevelRenderer
{
	private static final GarbageBinBlockEntity blockEntity = new GarbageBinBlockEntity(BlockPos.ZERO, CommonContent.GARBAGEBIN_BLOCK.get().defaultBlockState());
	
	private final BlockEntityRenderDispatcher renderer;
	
	public GarbageBinItemRenderer(BlockEntityRenderDispatcher renderer, EntityModelSet modelSet) 
	{
		super(renderer, modelSet);
		this.renderer = renderer;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transform, PoseStack poseStack, MultiBufferSource bufferIn, int p_108834_, int p_108835_)
	{
		if (stack.getItem() instanceof GarbageBinItem item && item.getBlock() instanceof GarbageBinBlock)
		{
			renderer.renderItem(blockEntity, poseStack, bufferIn, p_108834_, p_108835_);
		}
	}
}
