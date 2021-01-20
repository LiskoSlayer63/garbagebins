package fi.lizcraft.garbagebins.client;

import fi.lizcraft.garbagebins.client.gui.GarbageBinContainerScreen;
import fi.lizcraft.garbagebins.client.render.GarbageBinRenderer;
import fi.lizcraft.garbagebins.common.CommonContent;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientContent 
{
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) 
	{
		ClientRegistry.bindTileEntityRenderer(CommonContent.GARBAGEBIN_TILEENTITYTYPE, GarbageBinRenderer::new);
		ScreenManager.registerFactory(CommonContent.GARBAGEBIN_CONTAINER, GarbageBinContainerScreen::new);
	}
	
	public static void register(IEventBus eventBus)
	{
		eventBus.register(ClientContent.class);
	}
}
