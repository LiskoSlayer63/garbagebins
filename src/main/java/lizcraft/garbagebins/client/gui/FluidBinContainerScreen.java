package lizcraft.garbagebins.client.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import lizcraft.garbagebins.client.gui.components.SettingsTabToggleButton;
import lizcraft.garbagebins.common.block.entity.FluidBinBlockEntity;
import lizcraft.garbagebins.common.gui.FluidBinMenu;
import lizcraft.garbagebins.network.NetworkHandler;
import lizcraft.garbagebins.network.packet.QuickUsePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FluidBinContainerScreen extends BaseContainerScreen<FluidBinMenu> 
{
	public static final int INPUT_SLOT_LEFT = 15;
	public static final int INPUT_SLOT_TOP = 24;
	
	public static final int BUFFER_SLOT_LEFT = 73;
	public static final int BUFFER_SLOT_TOP = 24;
	
	public static final int FILTER_SLOT_TOP = 67;
	
	protected SettingsTabToggleButton quickUseBtn;
	
	public FluidBinContainerScreen(FluidBinMenu container, Inventory inv, Component titleIn) 
	{
		super(container, inv, titleIn);
	}
	
	@Override
	public void init()
	{
		super.init();
	    
	    FluidBinBlockEntity parent = this.menu.getParent();
	    boolean quickUseState = parent != null ? parent.getQuickUseState() : true;
    	
	    this.quickUseBtn = new SettingsTabToggleButton(quickUseState, 16, btn -> onQuickUsePress((SettingsTabToggleButton)btn));
		
		this.settingsTab.addButton(this.quickUseBtn);

	    this.updateTopPos();
	}
	
	@Override
	public void containerTick()
	{
	    super.containerTick();
	    
	    FluidBinBlockEntity parent = this.menu.getParent();
	    if (parent != null)
	    {
	    	if (this.quickUseBtn.getState() != parent.getQuickUseState())
	    		this.quickUseBtn.setState(parent.getQuickUseState());
	    }
	}
	
	@Override
	public void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
	{
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		
		FluidStack water = new FluidStack(Fluids.WATER, 1000);
		FluidStack lava = new FluidStack(Fluids.LAVA, 1000);
		
		drawFluid(poseStack, this.leftPos + BUFFER_SLOT_LEFT, this.topPos + BUFFER_SLOT_TOP, water);
		drawFluid(poseStack, this.leftPos + BUFFER_SLOT_LEFT + 18, this.topPos + BUFFER_SLOT_TOP, lava);

		if (doesHover(mouseX, mouseY, this.leftPos + BUFFER_SLOT_LEFT, this.topPos + BUFFER_SLOT_TOP, 16, 16))
		{
			AbstractContainerScreen.renderSlotHighlight(poseStack, this.leftPos + BUFFER_SLOT_LEFT, this.topPos + BUFFER_SLOT_TOP, this.getBlitOffset(), 1090519039);
		}
		
		if (doesHover(mouseX, mouseY, this.leftPos + BUFFER_SLOT_LEFT + 18, this.topPos + BUFFER_SLOT_TOP, 16, 16))
		{
			AbstractContainerScreen.renderSlotHighlight(poseStack, this.leftPos + BUFFER_SLOT_LEFT + 18, this.topPos + BUFFER_SLOT_TOP, this.getBlitOffset(), 1090519039);
		}
	}
	
	@Override
	public void renderComponentTooltip(PoseStack poseStack, List<Component> tooltip, int mouseX, int mouseY)
	{
		if (this.quickUseBtn.isMouseOver(mouseX, mouseY))
			getButtonTooltip(this.quickUseBtn, "quickUse", tooltip);

		if (doesHover(mouseX, mouseY, this.leftPos + BUFFER_SLOT_LEFT, this.topPos + BUFFER_SLOT_TOP, 16, 16))
		{
			tooltip.add(Component.literal("ebin vesi"));
		}
		
		if (doesHover(mouseX, mouseY, this.leftPos + BUFFER_SLOT_LEFT + 18, this.topPos + BUFFER_SLOT_TOP, 16, 16))
		{
			tooltip.add(Component.literal("ebin laava"));
		}
		
		super.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
	}
	
	private boolean doesHover(int mouseX, int mouseY, int x, int y, int width, int height)
	{
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}
	
	private void onQuickUsePress(SettingsTabToggleButton btn)
	{
		if (this.menu.getParent() != null)
			NetworkHandler.MAIN.sendToServer(new QuickUsePacket(this.menu.getParent().getBlockPos(), !btn.getState()));
	}
	
	private void drawFluid(PoseStack poseStack, final int x, final int y, FluidStack fluidStack) 
	{
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);

        TextureAtlasSprite sprite = Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(fluidStill);

		int fluidColor = renderProperties.getTintColor();
        
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		Matrix4f matrix = poseStack.last().pose();
		setGLColorFromInt(fluidColor);
        
		drawTextureWithMasking(matrix, x, y, sprite, 0, 0, 100);
	}
	
	private static void setGLColorFromInt(int color) 
	{
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		float alpha = ((color >> 24) & 0xFF) / 255F;

		RenderSystem.setShaderColor(red, green, blue, alpha);
	}
	
	private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, long maskTop, long maskRight, float zLevel) 
	{
		float uMin = textureSprite.getU0();
		float uMax = textureSprite.getU1();
		float vMin = textureSprite.getV0();
		float vMax = textureSprite.getV1();
		uMax = uMax - (maskRight / 16F * (uMax - uMin));
		vMax = vMax - (maskTop / 16F * (vMax - vMin));

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
		bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
		bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
		bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
		tessellator.end();
	}
}