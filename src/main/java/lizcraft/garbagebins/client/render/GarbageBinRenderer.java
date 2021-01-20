package lizcraft.garbagebins.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lizcraft.garbagebins.GarbageBins;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.block.GarbageBinBlock;
import lizcraft.garbagebins.common.tile.GarbageBinTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class GarbageBinRenderer extends TileEntityRenderer<GarbageBinTileEntity> 
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(GarbageBins.MOD_ID, "textures/entity/garbagebin/normal.png");
	private static final GarbageBinModel MODEL = new GarbageBinModel();
	
	public GarbageBinRenderer(TileEntityRendererDispatcher rendererDispatcherIn) 
	{
		super(rendererDispatcherIn);
	}

	@Override
	public void render(GarbageBinTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) 
	{
		World world = tileEntityIn.getWorld();
	    boolean flag = world != null;
	    
	    BlockState blockstate = flag ? tileEntityIn.getBlockState() : CommonContent.GARBAGEBIN_BLOCK.getDefaultState().with(GarbageBinBlock.FACING, Direction.SOUTH);
		
		matrixStackIn.push();
		
		float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
        matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

        float lidAngle = 1.0F - tileEntityIn.getLidAngle(partialTicks);
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        
		IVertexBuilder renderBuffer = bufferIn.getBuffer(MODEL.getRenderType(TEXTURE));
	    MODEL.render(matrixStackIn, renderBuffer, combinedLightIn, combinedOverlayIn, lidAngle);
	    
	    matrixStackIn.pop();
	}
	
	
	/*
	 * GARBAGEBIN MODEL
	 */
	
	public static class GarbageBinModel extends Model 
	{
		private final ModelRenderer BODY_RENDERER;
		private final ModelRenderer PADDLE_RENDERER;
		private final ModelRenderer LID_RENDERER;
		private final ModelRenderer HANDLE_RENDERER;
	    
	    private float lidAngle = 0F;

		public GarbageBinModel() 
		{
			super(RenderType::getEntityTranslucent);
			
			textureWidth = 64;
			textureHeight = 64;
			
			BODY_RENDERER = new ModelRenderer(this);
			BODY_RENDERER.setTextureOffset(4, 16);
			BODY_RENDERER.addBox(2, 0, 2, 12, 14, 12);
			
			PADDLE_RENDERER = new ModelRenderer(this);
			PADDLE_RENDERER.setTextureOffset(0, 3);
			PADDLE_RENDERER.addBox(6, 0, 14, 4, 1, 2);
			
			LID_RENDERER = new ModelRenderer(this);
			LID_RENDERER.setTextureOffset(0, 0);
			LID_RENDERER.addBox(1, 0, -1, 14, 2, 14);
			LID_RENDERER.setRotationPoint(0, 14, 2);
			
			HANDLE_RENDERER = new ModelRenderer(this);
			HANDLE_RENDERER.setTextureOffset(2, 0);
			HANDLE_RENDERER.addBox(6.5F, 2, 5.5F, 3, 1, 1);
			LID_RENDERER.addChild(HANDLE_RENDERER);
		}
		
		public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float lidAngle)
		{
			this.lidAngle = lidAngle;
			render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		@Override
		public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) 
		{
			LID_RENDERER.rotateAngleX = -(lidAngle * ((float)Math.PI / 3F));
			BODY_RENDERER.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			PADDLE_RENDERER.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			LID_RENDERER.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
}
