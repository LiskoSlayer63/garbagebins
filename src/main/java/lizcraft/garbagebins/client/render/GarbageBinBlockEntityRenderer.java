package lizcraft.garbagebins.client.render;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import lizcraft.garbagebins.client.ClientContent;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.block.GarbageBinBlock;
import lizcraft.garbagebins.common.block.entity.GarbageBinBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;

public class GarbageBinBlockEntityRenderer implements BlockEntityRenderer<GarbageBinBlockEntity> 
{
	public final ModelPart BODY;
	public final ModelPart PADDLE;
	public final ModelPart LID;
	public final ModelPart HANDLE;
	
	public GarbageBinBlockEntityRenderer(BlockEntityRendererProvider.Context context) 
	{
		ModelPart model = context.bakeLayer(ClientContent.GARBAGEBIN_MODEL);
		BODY = model.getChild("body");
		PADDLE = model.getChild("paddle");
		LID = model.getChild("lid");
		HANDLE = model.getChild("handle");
	}

	@Override
	public void render(GarbageBinBlockEntity tileEntityIn, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) 
	{
		Level world = tileEntityIn.getLevel();
	    BlockState blockstate = world != null ? tileEntityIn.getBlockState() : CommonContent.GARBAGEBIN_BLOCK.get().defaultBlockState().setValue(GarbageBinBlock.FACING, Direction.SOUTH);
		
		poseStackIn.pushPose();
		
		float rotAngle = blockstate.getValue(ChestBlock.FACING).toYRot();
        poseStackIn.translate(0.5D, 0.5D, 0.5D);
        poseStackIn.mulPose(Vector3f.YP.rotationDegrees(-rotAngle));
        poseStackIn.translate(-0.5D, -0.5D, -0.5D);

        float lidAngle = 1.0F - tileEntityIn.getOpenNess(partialTicks);
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        
        Material renderMaterial = this.getMaterial();
        VertexConsumer renderBuffer = renderMaterial.buffer(bufferIn, RenderType::entityCutout);
        
	    render(poseStackIn, renderBuffer, combinedLightIn, combinedOverlayIn, lidAngle);
	    
	    poseStackIn.popPose();
	}
	
	private Material getMaterial()
	{
		return new Material(InventoryMenu.BLOCK_ATLAS, ClientContent.GARBAGEBIN_TEXTURE);
	}
	
	private void render(PoseStack poseStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float lidAngle)
	{
		LID.xRot = -(lidAngle * ((float)Math.PI / 3F));
		HANDLE.xRot = LID.xRot;
		BODY.render(poseStackIn, bufferIn, packedLightIn, packedOverlayIn);
		PADDLE.render(poseStackIn, bufferIn, packedLightIn, packedOverlayIn);
		LID.render(poseStackIn, bufferIn, packedLightIn, packedOverlayIn);
		HANDLE.render(poseStackIn, bufferIn, packedLightIn, packedOverlayIn);
	}
	
	
	/*
	 * GARBAGEBIN MODEL
	 */
	
	public static LayerDefinition getLayerDefinition()
	{
	      MeshDefinition meshdefinition = new MeshDefinition();
	      PartDefinition partdefinition = meshdefinition.getRoot();

	      partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
	    		  .texOffs(4, 16)
	    		  .addBox(2.0F, 0.0F, 2.0F, 12.0F, 14.0F, 12.0F), 
	    		  PartPose.ZERO);
	      partdefinition.addOrReplaceChild("paddle", CubeListBuilder.create()
	    		  .texOffs(0, 3)
	    		  .addBox(6.0F, 0.0F, 14.0F, 4.0F, 1.0F, 2.0F), 
	    		  PartPose.ZERO);
	      partdefinition.addOrReplaceChild("lid", CubeListBuilder.create()
	    		  .texOffs(0, 0)
	    		  .addBox(1.0F, 0.0F, -1.0F, 14.0F, 2.0F, 14.0F), 
	    		  PartPose.offset(0.0F, 14.0F, 2.0F));
	      partdefinition.addOrReplaceChild("handle", CubeListBuilder.create()
	    		  .texOffs(2, 0)
	    		  .addBox(6.5F, 2.0F, 5.5F, 3.0F, 1.0F, 1.0F), 
	    		  PartPose.offset(0.0F, 14.0F, 2.0F));
	      
	      return LayerDefinition.create(meshdefinition, 64, 64);
	}
}
