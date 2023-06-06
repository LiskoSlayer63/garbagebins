package lizcraft.garbagebins.network.packet;

import java.util.function.Supplier;

import lizcraft.garbagebins.common.SettingsState;
import lizcraft.garbagebins.common.block.entity.interfaces.IFilterState;
import lizcraft.garbagebins.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent.Context;

public class FilterStatePacket extends SettingsStatePacket
{
	public FilterStatePacket(BlockPos pos, SettingsState state) 
	{
		super(pos, state);
	}

	public static FilterStatePacket read(FriendlyByteBuf buf)
	{
		SettingsStatePacket packet = readState(buf);
		return new FilterStatePacket(packet.pos, packet.state);
	}

	public static void handle(FilterStatePacket packet, Supplier<Context> ctx) 
	{
		NetworkHandler.enqueueBlockEntityWork(packet.pos, ctx, (level, blockEntity) -> 
		{
			if (blockEntity != null && blockEntity instanceof IFilterState state)
			{
				state.setFilterState(packet.state);
				blockEntity.setChanged();
				level.sendBlockUpdated(packet.pos, blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_CLIENTS);
			}
		});
	}
}