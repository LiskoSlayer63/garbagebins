package liznet.trashcansreborn.gui;

import liznet.trashcansreborn.TrashCansReborn;
import liznet.trashcansreborn.block.tile.TileTrashcan;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
@SideOnly(Side.CLIENT)
public class GuiTrashcan extends GuiContainer
{
	private static final ResourceLocation texture = new ResourceLocation(TrashCansReborn.modId, "textures/gui/trashcan.png");

	public GuiTrashcan(EntityPlayer player, TileTrashcan trash) 
	{
		super(new ContainerTrashcan(trash, player));
		this.xSize = 200;
		this.ySize = 200;
	}
	
	@Override
	public void initGui() 
	{
		super.initGui();
	}
	
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) 
	{
		String name = I18n.translateToLocal("tile.trashcan.name");
		this.fontRenderer.drawString(name, this.xSize / 2 - 10 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.translateToLocalFormatted("container.inventory", new Object[0]), 8, this.ySize - 115 + 2, 4210752);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) 
	{
		this.mc.getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}
