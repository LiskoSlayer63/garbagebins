package lizcraft.garbagebins.client;

import lizcraft.garbagebins.GarbageBins;
import lizcraft.garbagebins.client.gui.FluidBinContainerScreen;
import lizcraft.garbagebins.client.gui.GarbageBinContainerScreen;
import lizcraft.garbagebins.client.render.GarbageBinBlockEntityRenderer;
import lizcraft.garbagebins.common.CommonContent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientContent 
{
	public static final ResourceLocation GARBAGEBIN_TEXTURE = new ResourceLocation(GarbageBins.MOD_ID, "entity/garbagebin/normal");
	public static final ModelLayerLocation GARBAGEBIN_MODEL = new ModelLayerLocation(new ResourceLocation(GarbageBins.MOD_ID, "garbagebin"), "main");
	
	public static void register(IEventBus eventBus)
	{
		eventBus.register(ClientContent.class);
	}
	
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) 
	{
		MenuScreens.register(CommonContent.GARBAGEBIN_MENU.get(), GarbageBinContainerScreen::new);
		MenuScreens.register(CommonContent.FLUIDBIN_MENU.get(), FluidBinContainerScreen::new);
	}
	
	@SubscribeEvent
	public static void onRegisterEntityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) 
	{
		event.registerBlockEntityRenderer(CommonContent.GARBAGEBIN_BLOCKENTITY_TYPE.get(), GarbageBinBlockEntityRenderer::new);
	}
	
	@SubscribeEvent
	public static void onRegisterLayerDefinitionsEvent(RegisterLayerDefinitions event) 
	{
		event.registerLayerDefinition(GARBAGEBIN_MODEL, GarbageBinBlockEntityRenderer::getLayerDefinition);
	}
	
	@SubscribeEvent
	public static void onStitchEvent(TextureStitchEvent.Pre event)
    {
		ResourceLocation stitching = event.getAtlas().location();
		
		if(!stitching.equals(InventoryMenu.BLOCK_ATLAS))
			return;
        
		boolean added = event.addSprite(GARBAGEBIN_TEXTURE);
     
		GarbageBins.LOGGER.info((added ? "Added texture to atlas: " : "Failed to add texture to atlas: ") + GARBAGEBIN_TEXTURE);
    }
}
