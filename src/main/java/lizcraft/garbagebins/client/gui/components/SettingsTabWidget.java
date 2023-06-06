package lizcraft.garbagebins.client.gui.components;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import lizcraft.garbagebins.GarbageBins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class SettingsTabWidget extends GuiComponent implements Widget, GuiEventListener, NarratableEntry
{
	protected static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation(GarbageBins.MOD_ID, "textures/gui/widgets.png");
	
	protected final List<SettingsTabButton> btns = Lists.newArrayList();
	protected final SettingsTabButton masterBtn;
	
	protected boolean isOpen;
	protected int origLeft;
	protected int origTop;
	public int x;
	public int y;
	
	public SettingsTabWidget(int posLeft, int posTop) 
	{
		this.x = posLeft;
		this.y = posTop;
		this.origLeft = this.x;
		this.origTop = this.y;
		this.isOpen = false;
		this.masterBtn = new SettingsTabButton(this.x + 5, this.y + 4, btn -> this.toggleOpen())
		{
			@Override
			protected void renderBg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY)
			{
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, STATS_ICON_LOCATION);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				
				GuiComponent.blit(poseStack, this.x + 3, this.y + 2, this.getBlitOffset(), 20, 20, 14, 14, 128, 128);
			}
		};
	}
	
	public Button getMasterButton()
	{
		return this.masterBtn;
	}
	
	public void addButton(SettingsTabButton btn)
	{
		btn.x = this.origLeft + 5;
		btn.y = this.origTop + 27 + btns.size() * 20;
		btn.origLeft = btn.x;
		btn.origTop = btn.y;
		
		btn.visible = this.isOpen;
		
		btns.add(btn);
	}
	
	public boolean toggleOpen()
	{
		this.isOpen = !this.isOpen;
		
		for (SettingsTabButton btn : this.btns)
			btn.visible = this.isOpen;
		
		return this.isOpen;
	}

	public void setOffset(int left, int top)
	{
		this.x = this.origLeft + left;
		this.y = this.origTop + top;
		
		this.masterBtn.setOffset(left, top);
		
		for (SettingsTabButton btn : this.btns)
			btn.setOffset(left, top);
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) 
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		this.blit(poseStack, this.x, this.y, 0, 0, 30, 26);
		
		if (this.isOpen)
		{
			for (int i = 0; i < btns.size(); i++)
				this.blit(poseStack, this.x, this.y + 25 + i * 20, 0, 26, 27, 22);
			
			this.blit(poseStack, this.x, this.y + 27 + btns.size() * 20, 0, 48, 27, 2);
		}
		
		this.renderButtons(poseStack, mouseX, mouseY, partialTicks);
	}
	
	private void renderButtons(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
	{
		masterBtn.render(poseStack, mouseX, mouseY, partialTicks);
		
		if (this.isOpen)
			for (Button btn : btns)
				btn.render(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) 
	{
		
	}

	@Override
	public NarrationPriority narrationPriority() 
	{
		return NarratableEntry.NarrationPriority.NONE;
	}

	@Override
	public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) 
	{
		if (masterBtn.mouseClicked(p_93641_, p_93642_, p_93643_))
			return true;
		
		for (Button btn : btns)
			if (btn.mouseClicked(p_93641_, p_93642_, p_93643_))
				return true;
		
		return false;
	}

	@Override
	public boolean mouseReleased(double p_93684_, double p_93685_, int p_93686_)
	{
		if (masterBtn.mouseReleased(p_93684_, p_93685_, p_93686_))
			return true;
		
		for (Button btn : btns)
			if (btn.mouseReleased(p_93684_, p_93685_, p_93686_))
				return true;
		
		return false;
	}

	@Override
	public boolean mouseDragged(double p_93645_, double p_93646_, int p_93647_, double p_93648_, double p_93649_) 
	{
		if (masterBtn.mouseDragged(p_93645_, p_93646_, p_93647_, p_93648_, p_93649_))
			return true;
		
		for (Button btn : btns)
			if (btn.mouseDragged(p_93645_, p_93646_, p_93647_, p_93648_, p_93649_))
				return true;
		
		return false;
	}

	@Override
	public boolean changeFocus(boolean p_93691_) 
	{
		if (masterBtn.changeFocus(p_93691_))
			return true;
		
		for (Button btn : btns)
			if (btn.changeFocus(p_93691_))
				return true;
		
		return false;
	}

	@Override
	public boolean isMouseOver(double p_93672_, double p_93673_) 
	{
		if (masterBtn.isMouseOver(p_93672_, p_93673_))
			return true;
		
		for (Button btn : btns)
			if (btn.isMouseOver(p_93672_, p_93673_))
				return true;
		
		return false;
	}
}
