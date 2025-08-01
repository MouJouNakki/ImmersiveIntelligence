package pl.pabilo8.immersiveintelligence.common.entity.hans.tasks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
<<<<<<< Updated upstream
import pl.pabilo8.immersiveintelligence.api.ammo.enums.CoreType;
import pl.pabilo8.immersiveintelligence.api.ammo.enums.FuseType;
=======
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import pl.pabilo8.immersiveintelligence.api.Utils;
>>>>>>> Stashed changes
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;
import pl.pabilo8.immersiveintelligence.common.entity.EntityMachinegun;
import pl.pabilo8.immersiveintelligence.common.item.ammo.ItemIIBulletMagazine.Magazines;
import pl.pabilo8.immersiveintelligence.common.network.IIPacketHandler;
<<<<<<< Updated upstream
import pl.pabilo8.immersiveintelligence.common.network.messages.MessageEntityNBTSync;
import pl.pabilo8.immersiveintelligence.common.util.IIColor;
import pl.pabilo8.immersiveintelligence.common.util.easynbt.EasyNBT;
=======
import pl.pabilo8.immersiveintelligence.common.network.MessageEntityNBTSync;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
>>>>>>> Stashed changes

import javax.annotation.Nullable;

/**
 * @author Pabilo8
 * @since 05.04.2021
 */
public class AIHansMachinegun extends EntityAIBase
{
	private final EntityLiving hans;
	private EntityMachinegun mg;
	private int checkTimer = 0;
	private double maxDistForField = 40;

	@Nullable
	private Entity target = null;

	public AIHansMachinegun(EntityLiving hans)
	{
		this.hans = hans;
		this.setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if(hans.getRidingEntity() instanceof EntityMachinegun)
			mg = ((EntityMachinegun)hans.getRidingEntity());
		else
		{
			hans.tasks.removeTask(this);
			return false;
		}

		if(mg==null||mg.isDead)
		{
			hans.tasks.removeTask(this);
			return false;
		}

		return true;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void updateTask()
	{
		if(mg != null&&IIContent.itemMachinegun.getUpgrades(mg.gun).hasKey("hasty_bipod"))
		{
			if(checkTimer<=0)
			{
				checkTimer = 100;
				double nearestEnemyDist = Double.POSITIVE_INFINITY;
				for(Entity entity : hans.world.loadedEntityList)
				{
					if(((EntityHans)hans).isValidTarget(entity))
					{
						double dist = hans.getDistance(entity.posX, entity.posY, entity.posZ);
						if(dist < nearestEnemyDist)
							nearestEnemyDist = dist;
					}
				}
				if(nearestEnemyDist>maxDistForField)
				{
					hans.dismountRidingEntity();
					ItemNBTHelper.setTagCompound(mg.gun, "magazine1", mg.magazine1.serializeNBT());
					ItemNBTHelper.setTagCompound(mg.gun, "magazine2", mg.magazine2.serializeNBT());
					if(IIContent.itemMachinegun.getCapacity(mg.gun, 0) > 0)
					{
						IFluidHandlerItem cap = mg.gun.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
						if(cap!=null)
						{
							cap.drain(Integer.MAX_VALUE, true);
							if(mg.tank.getFluid()!=null)
								cap.fill(mg.tank.getFluid().copy(), true);
						}
					}
					hans.setHeldItem(EnumHand.OFF_HAND, mg.gun);
					mg.setDead();
					return;
				}
			}
			else
				checkTimer--;
		}
		target = hans.getAttackTarget();
		if(mg!=null)
		{
			mg.shoot = false;

			NBTTagCompound update = new NBTTagCompound();
			mg.writeEntityToNBT(update);
			IIPacketHandler.INSTANCE.sendToAllAround(new MessageEntityNBTSync(mg, update), IIPacketHandler.targetPointFromEntity(mg, 24));

			if(mg.magazine1.isEmpty())
			{
				if(hans.getHeldItemMainhand().isEmpty())
				{

					ItemStack ammoStack = IIContent.itemAmmoMachinegun.getAmmoStack(IIContent.ammoCoreBrass, CoreType.PIERCING, FuseType.CONTACT, IIContent.ammoComponentTracerPowder);
					IIContent.itemAmmoMachinegun.setComponentNBT(ammoStack, EasyNBT.parseNBT("{colour: %s}", IIColor.MC_DARK_RED));
					ItemStack magazine = IIContent.itemBulletMagazine.getMagazine(Magazines.MACHINEGUN, ammoStack);


					hans.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, magazine);
				}
			}
			else if(target!=null)
			{
				hans.getLookHelper().setLookPositionWithEntity(target, hans.getHorizontalFaceSpeed(), hans.getVerticalFaceSpeed());
				if(isAimedAt())
					mg.shoot = true;
			}

		}
	}

	public boolean isAimedAt()
	{
		//TODO: 15.02.2024 use new calculation method
		return MathHelper.wrapDegrees(hans.rotationPitch)-mg.gunPitch < 5&&MathHelper.wrapDegrees(hans.rotationYawHead)-MathHelper.wrapDegrees(mg.rotationYaw) < 5;
	}
}
