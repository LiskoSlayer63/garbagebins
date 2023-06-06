package lizcraft.garbagebins.network.packet;

import java.util.function.Supplier;

import lizcraft.garbagebins.common.block.entity.interfaces.IQuickUseState;
import lizcraft.garbagebins.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent.Context;

public class QuickUsePacket 
{
	protected BlockPos pos;
	protected boolean state;
	
	public QuickUsePacket(BlockPos pos, boolean state)
	{
		this.pos = pos;
		this.state = state;
	}

	public static void write(QuickUsePacket packet, FriendlyByteBuf buf) 
	{
		buf.writeBlockPos(packet.pos);
		buf.writeBoolean(packet.state);
	}

	public static QuickUsePacket read(FriendlyByteBuf buf)
	{
		return new QuickUsePacket(buf.readBlockPos(), buf.readBoolean());
	}

	public static void handle(QuickUsePacket packet, Supplier<Context> ctx) 
	{
		NetworkHandler.enqueueBlockEntityWork(packet.pos, ctx, (level, blockEntity) -> 
		{
			if (blockEntity != null && blockEntity instanceof IQuickUseState state)
			{
				state.setQuickUseState(packet.state);
				blockEntity.setChanged();
				level.sendBlockUpdated(packet.pos, blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_CLIENTS);
			}
		});
	}
}
