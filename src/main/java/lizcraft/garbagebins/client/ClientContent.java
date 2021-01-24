package lizcraft.garbagebins.client;

import lizcraft.garbagebins.GarbageBins;
import lizcraft.garbagebins.client.gui.GarbageBinContainerScreen;
import lizcraft.garbagebins.client.render.GarbageBinRenderer;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.utils.Logger;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientContent 
{
	public static final ResourceLocation GARBAGEBIN_TEXTURE = new ResourceLocation(GarbageBins.MOD_ID, "entity/garbagebin/normal");
	
	public static void register(IEventBus eventBus)
	{
		eventBus.register(ClientContent.class);
	}
	
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) 
	{
		ClientRegistry.bindTileEntityRenderer(CommonContent.GARBAGEBIN_TILEENTITYTYPE, GarbageBinRenderer::new);
		ScreenManager.registerFactory(CommonContent.GARBAGEBIN_CONTAINER, GarbageBinContainerScreen::new);
	}
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onStitchEvent(TextureStitchEvent.Pre event)
    {
		ResourceLocation stitching = event.getMap().getTextureLocation();
		
        if(!stitching.equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE))
            return;
        
        boolean added = event.addSprite(GARBAGEBIN_TEXTURE);
     
        Logger.info("Added texture to atlas: " + added);
    }
}
