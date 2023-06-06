package lizcraft.garbagebins.common.block;

import java.util.concurrent.atomic.AtomicBoolean;

import lizcraft.garbagebins.common.block.entity.FluidBinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.NetworkHooks;

public class FluidBinBlock extends BaseBinBlock
{
	private static final VoxelShape DRAIN_SHAPE = Block.box(5.0D, 14.0D, 5.0D, 11.0D, 16.0D, 11.0D);
	protected static final VoxelShape BASE_SHAPE = Shapes.join(BaseBinBlock.BASE_SHAPE, DRAIN_SHAPE, BooleanOp.ONLY_FIRST);
	
	public FluidBinBlock(Properties properties) 
	{
		super(properties);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) 
	{
		return BASE_SHAPE;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) 
	{
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		
		if (level.getBlockEntity(pos) instanceof FluidBinBlockEntity fluidBin)
		{
			ServerPlayer sPlayer = (ServerPlayer)player;
			ItemStack stack = sPlayer.getItemInHand(hand);
			AtomicBoolean isDrained = new AtomicBoolean();
			
			if (fluidBin.getQuickUseState() && !stack.isEmpty() && stack.getItem() != Items.BUCKET)
			{
				//if (stack.getItem() == Items.MILK_BUCKET)
				//{
				//	sPlayer.setItemInHand(hand, new ItemStack(Items.BUCKET));
				//	isDrained.set(true);
				//}
				//else
				//{
					stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent((fluidHandler) -> 
					{
						for (int i = 0; i < fluidHandler.getTanks(); i++)
						{
							FluidStack fluid = fluidHandler.getFluidInTank(i);
							if (!fluid.isEmpty())
							{
								playSound(level, pos, fluid);
	
								fluidHandler.drain(fluid, FluidAction.EXECUTE);
								isDrained.set(true);
							}
						}
						
						if (isDrained.get())
							sPlayer.setItemInHand(hand, fluidHandler.getContainer());
					});
				//}
			}
			
			if (!isDrained.get())
				NetworkHooks.openScreen(sPlayer, fluidBin, pos);

			return InteractionResult.CONSUME;
		}
		
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new FluidBinBlockEntity(pos, state);
	}
	
	private static void playSound(Level level, BlockPos pos, FluidStack fluid)
	{
		SoundEvent sound = fluid.getFluid().getFluidType().getSound(fluid, SoundActions.BUCKET_EMPTY);
		
		if (sound != null)
			level.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, SoundSource.BLOCKS, 0.7F, level.random.nextFloat() * 0.1F + 0.9F);
	}
}
