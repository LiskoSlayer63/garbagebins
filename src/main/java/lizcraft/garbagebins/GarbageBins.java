package lizcraft.garbagebins;

import org.apache.logging.log4j.LogManager;

import lizcraft.garbagebins.client.ClientContent;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.utils.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GarbageBins.MOD_ID)
public class GarbageBins 
{
	public static final String MOD_ID = "garbagebins";
	
	public GarbageBins()
	{
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		Logger.init(LogManager.getLogger(GarbageBins.class));
		
		CommonContent.register(modEventBus);
		ClientContent.register(modEventBus);
	}
}
