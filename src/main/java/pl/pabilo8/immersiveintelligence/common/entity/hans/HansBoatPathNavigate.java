package pl.pabilo8.immersiveintelligence.common.entity.hans;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;

public class HansBoatPathNavigate extends HansPathNavigate {

	public EntityBoat theEntity;
	
	public HansBoatPathNavigate(EntityHans hans, World worldIn) {
		super(hans, worldIn);
	}

	@Override
	protected PathFinder getPathFinder() {
		this.nodeProcessor = new HansBoatNodeProcessor();
		return new PathFinder(this.nodeProcessor);
	}

	@Override
	protected Vec3d getEntityPosition() {
		return new Vec3d(this.theEntity.posX, this.theEntity.posY, this.theEntity.posZ);
	}

	@Override
	protected boolean canNavigate() {
		return this.theEntity != null && isWater(new Vec3d(this.theEntity.posX, this.theEntity.posY-1, this.theEntity.posZ));
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
		int i = MathHelper.floor(posVec31.x);
        int j = MathHelper.floor(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D)
        {
            return false;
        }
        else
        {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isWater(new Vec3d(i, posVec31.y, j)))
            {
                return false;
            }
            else
            {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)i - posVec31.x;
                double d7 = (double)j - posVec31.z;

                if (d0 >= 0.0D)
                {
                    ++d6;
                }

                if (d1 >= 0.0D)
                {
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor(posVec32.x);
                int j1 = MathHelper.floor(posVec32.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0)
                {
                    if (d6 < d7)
                    {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    }
                    else
                    {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.isWater(new Vec3d(i, posVec31.y, j)))
                    {
                        return false;
                    }
                }

                return true;
            }
        }
	}
	/**
     * Returns the path to the given EntityLiving. Args : entity
     */
    public Path getPathToEntityLiving(Entity entityIn)
    {
        return this.getPathToPos(new BlockPos(entityIn));
    }
    
    private boolean isWater(Vec3d pos)
    {
    	return this.world.getBlockState(new BlockPos(pos)).getMaterial() == Material.WATER;
    }
    
    @Override
    protected void pathFollow()
    {
    	Vec3d vec3d = this.getEntityPosition();
    	while(this.isDirectPathBetweenPoints(vec3d, this.currentPath.getCurrentPos(), 1, 1, 1))
    		this.currentPath.incrementPathIndex();
    	this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex()-1);
    	double targetYaw = Math.atan((vec3d.x-this.currentPath.getCurrentPos().x)/(vec3d.z-this.currentPath.getCurrentPos().z));
    	if(this.theEntity.rotationYaw==targetYaw)
    		this.controlBoat(BoatDir.FORWARD);
    	else if(this.theEntity.rotationYaw<targetYaw)
    		this.controlBoat(BoatDir.RIGHT);
    	else if(this.theEntity.rotationYaw>targetYaw)
    		this.controlBoat(BoatDir.LEFT);
    	this.checkForStuck(vec3d);
    }
    
    private enum BoatDir
    {
    	LEFT,
    	RIGHT,
    	FORWARD
    }
    
    private void controlBoat(BoatDir dir)
    {
    	float f = 0.0F;

        if (dir == BoatDir.LEFT)
        {
            theEntity.rotationYaw += -1.0F;
        }

        if (dir == BoatDir.RIGHT)
        {
            ++theEntity.rotationYaw;
        }

        if ((dir == BoatDir.RIGHT) != (dir == BoatDir.LEFT) && dir != BoatDir.FORWARD)
        {
            f += 0.005F;
        }


        if (dir == BoatDir.FORWARD)
        {
            f += 0.04F;
        }

        theEntity.motionX += (double)(MathHelper.sin(-theEntity.rotationYaw * 0.017453292F) * f);
        theEntity.motionZ += (double)(MathHelper.cos(theEntity.rotationYaw * 0.017453292F) * f);
        theEntity.setPaddleState(dir == BoatDir.RIGHT || dir == BoatDir.FORWARD, dir == BoatDir.LEFT || dir == BoatDir.FORWARD);
    }
}
