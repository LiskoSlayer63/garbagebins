package lizcraft.garbagebins.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import lizcraft.garbagebins.GarbageBins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SettingsTabButton extends Button
{
	protected static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(GarbageBins.MOD_ID, "textures/gui/widgets.png");
	
	protected int origLeft;
	protected int origTop;
	
	protected SettingsTabButton(int x, int y, OnPress onPress)
	{
		super(x, y, 20, 18, Component.empty(), onPress);
		this.origLeft = x;
		this.origTop = y;
	}
	
	public SettingsTabButton(OnPress onPress) 
	{
		super(0, 0, 20, 18, Component.empty(), onPress);
		this.origLeft = 0;
		this.origTop = 0;
	}
	
	public void setOffset(int left, int top)
	{
		this.x = this.origLeft + left;
		this.y = this.origTop + top;
	}
	
	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) 
	{
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		int texY = this.isHoveredOrFocused() ? 18 : 0;
		this.blit(poseStack, this.x, this.y, 30, texY, this.width, this.height);
		this.renderBg(poseStack, minecraft, mouseX, mouseY);
	}
}