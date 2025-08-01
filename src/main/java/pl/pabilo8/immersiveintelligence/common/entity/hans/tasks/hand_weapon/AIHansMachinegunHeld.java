package pl.pabilo8.immersiveintelligence.common.entity.hans.tasks.hand_weapon;

/*import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;
import pl.pabilo8.immersiveintelligence.common.entity.EntityMachinegun;
import pl.pabilo8.immersiveintelligence.common.entity.hans.tasks.AIHansBase;
import pl.pabilo8.immersiveintelligence.common.items.tools.ItemIIRadioConfigurator;
import pl.pabilo8.immersiveintelligence.common.items.weapons.ItemIIMachinegun;

public class AIHansMachinegunHeld extends AIHansHandWeapon
{

	private int checkTimer = 0;
	
	public AIHansMachinegunHeld(EntityHans hans) {
		super(hans);
		this.entityMoveSpeed = movespeed;
		this.targetDistToEnemy = targetDistToEnemy;
	}
	@Override
	public void setRequiredAnimation()
	{
		
	}
	/**
	 * Keep ticking a continuous task that has already been started
	 */
	/*protected void executeTask()
	{
		if(checkTimer<=0)
		{
			checkTimer = 100;
			double nearestEnemyDist = Double.POSITIVE_INFINITY;
			Entity nearestEnemy = null;
			for(Entity entity : hans.world.loadedEntityList)
			{
				if(hans.isValidTarget(entity))
				{
					double dist = hans.getDistance(entity.posX, entity.posY, entity.posZ);
					if(dist < nearestEnemyDist)
					{
						nearestEnemyDist = dist;
						nearestEnemy = entity;
					}
				}
			}
			if(nearestEnemyDist < targetDistToEnemy&&summonMG())
				this.hans.getNavigator().clearPath();
			else if(this.hans.getNavigator().noPath())
				this.hans.getNavigator().tryMoveToEntityLiving(nearestEnemy, this.entityMoveSpeed);
		}
		else
			checkTimer--;
	}
	private boolean summonMG()
	{
		ItemStack itemstack = hans.getHeldItem(EnumHand.OFF_HAND);
		EntityMachinegun maschinengewehr = new EntityMachinegun(hans.world, new BlockPos(hans.posX, hans.posY, hans.posZ), hans.getRotationYawHead(), 0, itemstack);

		if(!hans.world.isRemote)
		{
			hans.world.spawnEntity(maschinengewehr);
		}
		hans.startRiding(maschinengewehr);
		hans.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Block.REGISTRY.getObject(new ResourceLocation("air"))));
		//itemstack.shrink(1);
		return true;
		/*ItemStack itemstack = hans.getHeldItem(EnumHand.OFF_HAND);
		float f = 1.0F;
		float f1 = hans.prevRotationPitch+(hans.rotationPitch-hans.prevRotationPitch)*1.0F;
		float f2 = hans.prevRotationYaw+(hans.rotationYaw-hans.prevRotationYaw)*1.0F;
		double d0 = hans.prevPosX+(hans.posX-hans.prevPosX)*1.0D;
		double d1 = hans.prevPosY+(hans.posY-hans.prevPosY)*1.0D+(double)hans.getEyeHeight();
		double d2 = hans.prevPosZ+(hans.posZ-hans.prevPosZ)*1.0D;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2*0.017453292F-(float)Math.PI);
		float f4 = MathHelper.sin(-f2*0.017453292F-(float)Math.PI);
		float f5 = -MathHelper.cos(-f1*0.017453292F);
		float f6 = MathHelper.sin(-f1*0.017453292F);
		float f7 = f4*f5;
		float f8 = f3*f5;
		double d3 = 5.0D;
		Vec3d vec3d1 = vec3d.addVector((double)f7*5.0D, (double)f6*5.0D, (double)f8*5.0D);
		RayTraceResult raytraceresult = hans.world.rayTraceBlocks(vec3d, vec3d1, false);

		if(raytraceresult==null)
		{
			return false;
		}
		else
		{
			Vec3d vec3d2 = hans.getLook(1.0F);
			boolean flag = false;
			List<Entity> list = hans.world.getEntitiesWithinAABBExcludingEntity(hans, hans.getEntityBoundingBox().expand(vec3d2.x*5.0D, vec3d2.y*5.0D, vec3d2.z*5.0D).grow(1.0D));

			for(int i = 0; i < list.size(); ++i)
			{
				Entity entity = list.get(i);

				if(entity.canBeCollidedWith())
				{
					AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());

					if(axisalignedbb.contains(vec3d))
					{
						flag = true;
					}
				}
			}

			if(flag)
			{
				return false;
			}
			else if(raytraceresult.typeOfHit!=RayTraceResult.Type.BLOCK)
			{
				return false;
			}
			else
			{
				AxisAlignedBB aabb = hans.world.getBlockState(raytraceresult.getBlockPos()).getCollisionBoundingBox(hans.world, raytraceresult.getBlockPos());
				EnumFacing facing = EnumFacing.fromAngle(hans.rotationYaw);
				float yaw = facing.getHorizontalAngle();
				float pitch = 25f;

				AxisAlignedBB fence;
				switch(facing)
				{
					case SOUTH:
						fence = new AxisAlignedBB(0, 0, 0.5, 1, 1, 1);
						break;
					case WEST:
						fence = new AxisAlignedBB(0, 0, 0, 0.5, 1, 1);
						break;
					case EAST:
						fence = new AxisAlignedBB(0.5, 0, 0, 1, 1, 1);
						break;
					default:
					case NORTH:
						fence = new AxisAlignedBB(0, 0, 0, 1, 1, 0.5);
						break;
				}

				if(aabb==null)
					return false;

				boolean intersects = Utils.isAABBContained(fence, aabb);

				if(!intersects)
				{
					return false;
				}

				EntityMachinegun maschinengewehr = new EntityMachinegun(hans.world, raytraceresult.getBlockPos(), hans.getRotationYawHead(), pitch, itemstack.copy());

				if(!hans.world.isRemote)
				{
					hans.world.spawnEntity(maschinengewehr);
				}
				hans.startRiding(maschinengewehr);

				itemstack.shrink(1);
				return true;
			}
		}*/
	/*}
	@Override
	protected float calculateBallisticAngle(ItemStack ammo, EntityLivingBase attackTarget) {
		return 0;
	}
	@Override
	protected boolean hasAnyAmmo() {
		return true;
	}
	@Override
	protected boolean isValidWeapon() {
		return getWeapon().getItem() instanceof ItemIIMachinegun;
	}
}*/
