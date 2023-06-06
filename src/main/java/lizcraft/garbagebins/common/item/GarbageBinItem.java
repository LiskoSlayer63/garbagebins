package lizcraft.garbagebins.common.item;

import java.util.function.Consumer;

import lizcraft.garbagebins.client.render.GarbageBinItemRenderer;
import lizcraft.garbagebins.common.CommonContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class GarbageBinItem extends BlockItem
{
	public GarbageBinItem(Properties properties) 
	{
		super(CommonContent.GARBAGEBIN_BLOCK.get(), properties);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) 
	{
		consumer.accept(new IClientItemExtensions() 
		{
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() 
			{
				return new GarbageBinItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
			}
		});
	}
}
