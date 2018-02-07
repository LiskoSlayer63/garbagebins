package liznet.trashcansreborn;

import org.apache.logging.log4j.Logger;

import liznet.trashcansreborn.block.BlockTrashcan;
import liznet.trashcansreborn.block.BlockTrashcanFluid;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = TrashCansReborn.modId, name = TrashCansReborn.name,  version = TrashCansReborn.version, acceptedMinecraftVersions = "[1.12.2]")
public class TrashCansReborn 
{
	@Instance(TrashCansReborn.modId)
	public static TrashCansReborn instance;
	
	public static final String modId = "trashcansreborn";
	public static final String name = "Garbage Bins";
	public static final String version = "1.1.0";
	
	public static Logger Log;
	
	public static final Block trashcan = new BlockTrashcan();
	public static final Block trashcanFluid = new BlockTrashcanFluid();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Log = event.getModLog();

		MinecraftForge.EVENT_BUS.register(RegistryHandler.class);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}
}
