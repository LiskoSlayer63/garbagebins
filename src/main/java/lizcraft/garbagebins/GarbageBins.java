package lizcraft.garbagebins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lizcraft.garbagebins.client.ClientContent;
import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GarbageBins.MOD_ID)
public class GarbageBins 
{
	public static final String MOD_ID = "garbagebins";
	public static final Logger LOGGER = LogManager.getLogger(GarbageBins.class);
	
	public GarbageBins()
	{
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		CommonContent.register(modEventBus);
		NetworkHandler.register();
		
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientContent.register(modEventBus));
	}
}
