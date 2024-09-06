package pl.pabilo8.immersiveintelligence.common.entity.hans;


import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;

import javax.annotation.Nonnull;

/**
 * @author Pabilo8
 * @since 28.12.2021
 */
public class HansPathNavigate extends PathNavigate
{
	public HansPathNavigate(EntityHans hans, World worldIn)
	{
		super(hans, worldIn);
	}

	@Nonnull
	@Override
	protected PathFinder getPathFinder()
	{
		this.nodeProcessor = new HansNodeProcessor();
		this.nodeProcessor.setCanEnterDoors(true);
		this.nodeProcessor.setCanOpenDoors(true);
		this.nodeProcessor.setCanSwim(true);
		return new PathFinder(this.nodeProcessor);
	}

	@Override
	protected Vec3d getEntityPosition()
	{
		return new Vec3d(this.entity.posX, this.entity.posY, this.entity.posZ);
	}

	@Override
	protected boolean canNavigate()
	{
		return true;
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d vec3d, Vec3d vec3d1, int i, int i1, int i2)
	{
		return true;
	}

	@Nonnull
	@Override
	public HansWalkNodeProcessor getNodeProcessor()
	{
		return ((HansWalkNodeProcessor)nodeProcessor);
	}

	@Override
	public boolean canEntityStandOnPos(BlockPos pos)
	{
		IBlockState state = this.world.getBlockState(pos);
		IBlockState dState = this.world.getBlockState(pos.down());

		//do not walk into blocks marked as "damage" or "blocked"
		return getNodeProcessor().defaultNode(world, state, pos, state.getBlock()).getPriority() >= 0&&dState.isFullBlock();
	}
	private static class HansNodeProcessor extends NodeProcessor
	{

		@Override
		public PathPoint getStart()
		{
			return openPoint((int)Math.floor(this.entity.posX), (int)Math.floor(this.entity.posY), (int)Math.floor(this.entity.posZ));
		}

		@Override
		public PathPoint getPathPointToCoords(double v, double v1, double v2)
		{
			return openPoint((int)Math.floor(v), (int)Math.floor(v1), (int)Math.floor(v2));
		}

		@Override
		public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance)
		{
			int optionCount = 0;
			PathPoint south = this.openPoint(currentPoint.x, currentPoint.y, currentPoint.z+1);
			PathPoint west = this.openPoint(currentPoint.x-1, currentPoint.y, currentPoint.z);
			PathPoint east = this.openPoint(currentPoint.x+1, currentPoint.y, currentPoint.z);
			PathPoint north = this.openPoint(currentPoint.x, currentPoint.y, currentPoint.z-1);
			PathPoint up = this.openPoint(currentPoint.x, currentPoint.y+1, currentPoint.z);
			PathPoint down = this.openPoint(currentPoint.x, currentPoint.y-1, currentPoint.z);
			if(!south.visited&&south.distanceTo(targetPoint) < maxDistance)
				pathOptions[optionCount++] = south;
			if(!west.visited&&west.distanceTo(targetPoint) < maxDistance)
				pathOptions[optionCount++] = west;
			if(!east.visited&&east.distanceTo(targetPoint) < maxDistance)
				pathOptions[optionCount++] = east;
			if(!north.visited&&north.distanceTo(targetPoint) < maxDistance)
				pathOptions[optionCount++] = north;
			if(!up.visited&&up.distanceTo(targetPoint) < maxDistance)
				pathOptions[optionCount++] = up;
			if(!down.visited&&down.distanceTo(targetPoint) < maxDistance)
				pathOptions[optionCount++] = down;
			return optionCount;
		}

		@Override
		public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int i, int i1, int i2, EntityLiving entityLiving, int i3, int i4, int i5, boolean b, boolean b1) {
			return PathNodeType.WALKABLE;
		}

		@Override
		public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int i, int i1, int i2) {
			return PathNodeType.WALKABLE;
		}
	}
}
