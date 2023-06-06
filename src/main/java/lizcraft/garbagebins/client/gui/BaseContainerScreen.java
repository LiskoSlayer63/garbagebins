package lizcraft.garbagebins.client.gui;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import lizcraft.garbagebins.GarbageBins;
import lizcraft.garbagebins.client.gui.components.SettingsTabWidget;
import lizcraft.garbagebins.client.gui.components.SettingsTabStateButton;
import lizcraft.garbagebins.common.SettingsState;
import lizcraft.garbagebins.common.block.entity.BaseBinBlockEntity;
import lizcraft.garbagebins.common.gui.BaseBinMenu;
import lizcraft.garbagebins.network.NetworkHandler;
import lizcraft.garbagebins.network.packet.FilterStatePacket;
import lizcraft.garbagebins.network.packet.RedstoneStatePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BaseContainerScreen<T extends BaseBinMenu<? extends BaseBinBlockEntity>> extends AbstractContainerScreen<T>
{
	protected static final ResourceLocation GUI_TEXTURE = new ResourceLocation(GarbageBins.MOD_ID, "textures/gui/container/base_container.png");
	protected static final int FILTER_DIFF = BaseBinMenu.FILTER_DIFF;
	
	protected SettingsTabWidget settingsTab;
	protected SettingsTabStateButton redstoneBtn;
	protected SettingsTabStateButton filterBtn;
	
	protected boolean hasFilter;

	protected int filterLabelX;
	protected int filterLabelY;
	
	public BaseContainerScreen(T menu, Inventory inventory, Component title) 
	{
		super(menu, inventory, title);
	    this.passEvents = false;
	    this.imageWidth = 176;
	    this.imageHeight = 186;
	    this.inventoryLabelY = this.imageHeight - 94;
	    this.filterLabelX = this.inventoryLabelX;
	    this.filterLabelY = 55;
	}

	@Override
	public void init()
	{
		super.init();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		
	    this.clearWidgets();
	    
	    BaseBinBlockEntity parent = this.menu.getParent();
	    SettingsState redstoneState = parent != null ? parent.getRedstoneState() : SettingsState.NORMAL;
	    SettingsState filterState = parent != null ? parent.getFilterState() : SettingsState.DISABLED;

	    this.hasFilter = parent.getFilterState() != SettingsState.DISABLED;
    	
	    this.settingsTab = new SettingsTabWidget(this.leftPos - 27, this.topPos + 5);
	    this.redstoneBtn = new SettingsTabStateButton(redstoneState, 16, btn -> onRedstonePress((SettingsTabStateButton)btn));
		this.filterBtn = new SettingsTabStateButton(filterState, 0, btn -> onFilterPress((SettingsTabStateButton)btn));
		
		this.settingsTab.addButton(this.redstoneBtn);
		this.settingsTab.addButton(this.filterBtn);

	    this.updateTopPos();
		
	    this.addRenderableWidget(this.settingsTab);
	}
	
	@Override
	public void containerTick()
	{
	    BaseBinBlockEntity parent = this.menu.getParent();
	    if (parent != null)
	    {
	    	if (this.redstoneBtn.getState() != parent.getRedstoneState())
	    		this.redstoneBtn.setState(parent.getRedstoneState());
	    	
	    	if (this.filterBtn.getState() != parent.getFilterState())
	    		this.filterBtn.setState(parent.getFilterState());
	    	
	    	this.hasFilter = parent.getFilterState() != SettingsState.DISABLED;
	    	
	    	this.updateTopPos();
	    }
	}
	
	@Override
	public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
	{
		super.renderTooltip(poseStack, mouseX, mouseY);
		
		List<Component> tooltip = Lists.newArrayList();
		
		if (this.settingsTab != null)
		{
			if (this.settingsTab.getMasterButton().isMouseOver(mouseX, mouseY))
				tooltip.add(Component.translatable("container.garbagebins.settings.title"));
			
			if (this.redstoneBtn.isMouseOver(mouseX, mouseY))
				getButtonTooltip(this.redstoneBtn, "redstone", tooltip);
			
			if (this.filterBtn.isMouseOver(mouseX, mouseY))
				getButtonTooltip(this.filterBtn, "filter", tooltip);
		}
		
		this.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) 
	{
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}
	
	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) 
	{
		this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
		
		if (this.hasFilter)
		{
			this.font.draw(poseStack, Component.translatable("container.garbagebins.filter.label"), (float)this.filterLabelX, (float)this.filterLabelY, 4210752);
			this.font.draw(poseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
		}
		else
			this.font.draw(poseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY - FILTER_DIFF, 4210752);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) 
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderSystem.setShaderTexture(0, GUI_TEXTURE);

	    if (this.hasFilter)
		    this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	    else
	    {
		    this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, 65);
		    this.blit(poseStack, this.leftPos, this.topPos + 65, 0, 102, this.imageWidth, this.imageHeight - 102);
	    }
	}
	
	protected void onRedstonePress(SettingsTabStateButton btn)
	{
		if (this.menu.getParent() != null)
			NetworkHandler.MAIN.sendToServer(new RedstoneStatePacket(this.menu.getParent().getBlockPos(), btn.getState().next()));
	}
	
	protected void onFilterPress(SettingsTabStateButton btn)
	{
		if (this.menu.getParent() != null)
			NetworkHandler.MAIN.sendToServer(new FilterStatePacket(this.menu.getParent().getBlockPos(), btn.getState().next()));
	}
	
	protected void updateTopPos()
	{
    	if (this.hasFilter)
    	{
    		this.topPos = (this.height - this.imageHeight) / 2;
        	this.settingsTab.setOffset(0, 0);
    	}
    	else
    	{
    		this.topPos = (this.height - this.imageHeight + FILTER_DIFF) / 2;
        	this.settingsTab.setOffset(0, FILTER_DIFF / 2);
    	}
	}
	
	protected static void getButtonTooltip(Button button, String name, List<Component> tooltip)
	{
		String langKey = "container.garbagebins." + name;
		
		tooltip.add(Component.translatable(langKey + ".title"));
		
		if (button instanceof SettingsTabStateButton stateButton)
		{
			Component state = Component.translatable(langKey + "." + stateButton.getState().toString().toLowerCase());
			tooltip.add(Component.translatable(langKey + ".state", state).withStyle(ChatFormatting.GRAY));
		}

		tooltip.add(Component.literal(" "));
		
		String descKey = hasShiftDown() ? (langKey + ".desc") : "container.garbagebins.tooltip.info";
		tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}
}
