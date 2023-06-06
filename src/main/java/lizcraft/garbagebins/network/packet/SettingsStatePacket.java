package lizcraft.garbagebins.network.packet;

import lizcraft.garbagebins.common.SettingsState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class SettingsStatePacket 
{
	protected final BlockPos pos;
	protected final SettingsState state;
	
	public SettingsStatePacket(BlockPos pos, SettingsState state)
	{
		this.pos = pos;
		this.state = state;
	}
	
	public static <T extends SettingsStatePacket> void write(T packet, FriendlyByteBuf buf) 
	{
		buf.writeBlockPos(packet.pos);
		buf.writeByte(packet.state.save());
	}
	
	protected static SettingsStatePacket readState(FriendlyByteBuf buf)
	{
		return new SettingsStatePacket(buf.readBlockPos(), SettingsState.load(buf.readByte()));
	}
}
