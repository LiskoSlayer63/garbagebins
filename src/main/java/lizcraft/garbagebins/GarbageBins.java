package lizcraft.garbagebins;

import org.apache.logging.log4j.LogManager;

import lizcraft.garbagebins.client.ClientContent;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.config.GarbageBinsConfig;
import lizcraft.garbagebins.utils.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GarbageBins.MOD_ID)
public class GarbageBins 
{
	public static final String MOD_ID = "garbagebins";
	
	public GarbageBins()
	{
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		Logger.init(LogManager.getLogger(GarbageBins.class));
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GarbageBinsConfig.CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GarbageBinsConfig.COMMON_CONFIG);
		
		CommonContent.init();
		
		CommonContent.register(modEventBus);
		ClientContent.register(modEventBus);
	}
}
