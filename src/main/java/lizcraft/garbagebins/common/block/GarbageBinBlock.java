package lizcraft.garbagebins.common.block;

import javax.annotation.Nullable;

import lizcraft.garbagebins.common.CommonContent;
import lizcraft.garbagebins.common.block.entity.GarbageBinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class GarbageBinBlock extends BaseBinBlock
{
	
	public GarbageBinBlock(Properties properties) 
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new GarbageBinBlockEntity(pos, state);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_49232_) 
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) 
	{
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		
		if (level.getBlockEntity(pos) instanceof GarbageBinBlockEntity garbageBin && !isBlockedByBlock(level, pos))
			NetworkHooks.openScreen((ServerPlayer)player, garbageBin, pos);
		
		return InteractionResult.CONSUME;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) 
	{
		return level.isClientSide ? createTickerHelper(blockEntityType, CommonContent.GARBAGEBIN_BLOCKENTITY_TYPE.get(), GarbageBinBlockEntity::lidAnimateTick) : null;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) 
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof GarbageBinBlockEntity garbageBin) 
		{
			garbageBin.recheckOpen();
		}
	}
	
	private static boolean isBlockedByBlock(BlockGetter getter, BlockPos pos) 
	{
		BlockPos blockpos = pos.above();
		return getter.getBlockState(blockpos).isRedstoneConductor(getter, blockpos);
	}
}
