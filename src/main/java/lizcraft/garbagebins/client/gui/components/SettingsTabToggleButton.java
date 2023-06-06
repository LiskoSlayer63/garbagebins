package lizcraft.garbagebins.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class SettingsTabToggleButton extends SettingsTabButton
{
	protected boolean state;
	protected int blitLeft;
	
	public SettingsTabToggleButton(boolean state, OnPress onPress) 
	{
		super(onPress);
		this.state = state;
		this.blitLeft = 0;
	}
	
	public SettingsTabToggleButton(boolean state, int blitLeft, OnPress onPress) 
	{
		super(onPress);
		this.state = state;
		this.blitLeft = blitLeft;
	}
	
	@Override
	protected void renderBg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
	    
	    int texY = this.state ? 0 : 16;
		this.blit(poseStack, this.x + 2, this.y + 1, this.blitLeft, 50 + texY, 16, 16);
	}

	public boolean getState()
	{
		return this.state;
	}
	
	public void setState(boolean state)
	{
		this.state = state;
	}
}
