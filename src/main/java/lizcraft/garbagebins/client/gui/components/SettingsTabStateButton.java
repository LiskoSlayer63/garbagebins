package lizcraft.garbagebins.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import lizcraft.garbagebins.common.SettingsState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class SettingsTabStateButton extends SettingsTabButton
{
	protected SettingsState state;
	protected int blitLeft;
	
	public SettingsTabStateButton(SettingsState state, OnPress onPress) 
	{
		super(onPress);
		this.state = state;
		this.blitLeft = 0;
	}
	
	public SettingsTabStateButton(SettingsState state, int blitLeft, OnPress onPress) 
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

	    if (this.state != SettingsState.DISABLED)
	    {
			int texY = this.state == SettingsState.NORMAL ? 0 : 16;
			this.blit(poseStack, this.x + 2, this.y + 1, this.blitLeft, 50 + texY, 16, 16);
	    }
	    else
	    	this.blit(poseStack, this.x + 2, this.y + 1, this.blitLeft, 82, 16, 16);
	}
	
	public SettingsState getState()
	{
		return this.state;
	}
	
	public void setState(SettingsState state)
	{
		this.state = state;
	}
}