package pl.pabilo8.immersiveintelligence.common.entity.hans.tasks;

import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;
import pl.pabilo8.immersiveintelligence.common.entity.hans.HansPathNavigate;

import java.util.ArrayList;

public class AIHansBreakAndBuild extends AIHansBase
{

    public AIHansBreakAndBuild(EntityHans hans)
    {
        super(hans);
    }
    private ArrayList<BlockPos> toChange;

    @Override
    public void setRequiredAnimation()
    {

    }

    @Override
    public boolean shouldExecute()
    {
        HansPathNavigate navigator = this.hans.getNavigator();
        Path path = navigator.getPath();

        if(path!=null&&!path.isFinished()) {
            for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
                PathPoint pathpoint = path.getPathPointFromIndex(i);
                BlockPos pos = new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z);

                if (this.hans.getDistanceSq(pos) <= 2.25D && this.hans.world.isAirBlock(pos)) {
                    toChange.add(pos);
                }
                pos = pos.up();
                if (this.hans.getDistanceSq(pos) <= 2.25D && !this.hans.world.isAirBlock(pos)) {
                    toChange.add(pos);
                }
                pos = pos.up()
                if (this.hans.getDistanceSq(pos) <= 2.25D && !this.hans.world.isAirBlock(pos)) {
                    toChange.add(pos);
                }
            }
        }
        return !toChange.isEmpty();
    }
    public void startExecuting()
    {
        for (BlockPos pos : toChange)
        {
            if (this.hans.world.isAirBlock(pos))
                this.hans.world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            else
                this.hans.world.destroyBlock(pos,true);
        }
        toChange.clear();
    }
}

