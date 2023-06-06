package lizcraft.garbagebins.network;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import lizcraft.garbagebins.GarbageBins;
import lizcraft.garbagebins.network.packet.FilterStatePacket;
import lizcraft.garbagebins.network.packet.QuickUsePacket;
import lizcraft.garbagebins.network.packet.RedstoneStatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler
{
	private static final String PROTOCOL_VERSION = "1";
	
	public static final SimpleChannel MAIN = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(GarbageBins.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	
	private static int packetIndex = 0;
	
	public static void register()
	{
		MAIN.registerMessage(packetIndex++, RedstoneStatePacket.class, RedstoneStatePacket::write, RedstoneStatePacket::read, RedstoneStatePacket::handle);
		MAIN.registerMessage(packetIndex++, FilterStatePacket.class, FilterStatePacket::write, FilterStatePacket::read, FilterStatePacket::handle);
		MAIN.registerMessage(packetIndex++, QuickUsePacket.class, QuickUsePacket::write, QuickUsePacket::read, QuickUsePacket::handle);
	}
	
	@SuppressWarnings("deprecation")
	public static <T> void enqueueBlockEntityWork(BlockPos pos, Supplier<Context> ctx, BiConsumer<Level, BlockEntity> consumer)
	{
		if (ctx.get() == null)
			return;
		
		ctx.get().enqueueWork(() ->
		{
			Level level = ctx.get().getSender().level;
			if (level.hasChunkAt(pos))
				consumer.accept(level, level.getBlockEntity(pos));
		});
		
		ctx.get().setPacketHandled(true);
	}
}
