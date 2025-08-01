package pl.pabilo8.immersiveintelligence.common.entity.hans.tasks.hand_weapon;

import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Array;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.items.ItemToolUpgrade.ToolUpgrades;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.api.IEProperties;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.blocks.stone.BlockIISandbags;
import pl.pabilo8.immersiveintelligence.common.entity.EntityMachinegun;
import pl.pabilo8.immersiveintelligence.common.entity.EntityMortar;
import pl.pabilo8.immersiveintelligence.common.entity.EntityVehicleSeat;
import pl.pabilo8.immersiveintelligence.common.entity.EntityFieldHowitzer;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;
import pl.pabilo8.immersiveintelligence.common.entity.hans.HansUtils;
import pl.pabilo8.immersiveintelligence.common.entity.hans.tasks.AIHansBase;
import pl.pabilo8.immersiveintelligence.common.items.ammunition.ItemIIBulletMagazine;
import pl.pabilo8.immersiveintelligence.common.items.armor.ItemIIArmorUpgrade.ArmorUpgrades;
import pl.pabilo8.immersiveintelligence.common.items.tools.ItemIIBinoculars;
import pl.pabilo8.immersiveintelligence.common.items.tools.ItemIIRadioConfigurator;
import pl.pabilo8.immersiveintelligence.common.items.weapons.ItemIIRailgunOverride;
import pl.pabilo8.immersiveintelligence.common.items.weapons.ItemIISubmachinegun;
import pl.pabilo8.immersiveintelligence.common.items.weapons.ItemIIWeaponUpgrade.WeaponUpgrades;
import pl.pabilo8.immersiveintelligence.common.util.commands.ii.CommandIIHans;
/**
 * @author MouJouNakki
 * @since 9.6.2022
 */
public class AIHansCommander extends AIHansHandWeapon
{
	private int summoningTime = 0;
	
	private final int defaultSummoningTime = 2000;
	private final int minSummoningTime = 50;
	
	private int sneakingTime = -1;
	
	private int enemiesPerHans;
	private int dangerDistance;
	
	private int maxSpreadForChemthrower;
	private int minEnemiesForChemthrower;
	
	private int maxSpreadForMG;
	
//	private int maxSpreadForFieldMG;
//	private int maxEnemiesForFieldMG;
	
	private int maxSpreadForMortar;
	private int minEnemiesForMortar;
	
	private int minEnemiesForFieldHow;
	
	private int maxSpreadForSmgDrum;
	private int minEnemiesForSmgDrum;
	
	private int minSpreadForRailgunSniper;
	private int maxEnemiesForRailgunSniper;
	
	private int maxSpreadForRailgun;
	private int maxEnemiesForRailgun;
	
	private int maxSpreadForGrenadier;
	private int minEnemiesForGrenadier;
	
	private int minSummoningTimeForObserver;
	
	private int minSpreadForSurroundingMGDefense;
	
	private int minEnemiesForMG;
	
	private int maxEnemiesForRevolver;
	
	private int getRandomInt(int min, int max)
	{
		return hans.getRNG().nextInt(max)+min;
	}
	
	public AIHansCommander(EntityHans hans)
	{
		super(hans, 80, 30, 1f);
		
		this.enemiesPerHans = getRandomInt(1,5);
		this.dangerDistance = getRandomInt(10,50);
		
		this.maxSpreadForChemthrower = getRandomInt(10,20);
		this.minEnemiesForChemthrower = getRandomInt(1,10);
		
		this.maxSpreadForMG = getRandomInt(20,60);
		
		//this.maxSpreadForFieldMG = getRandomInt(30,70);
		//this.maxEnemiesForFieldMG = getRandomInt(25,45);
		
		this.maxSpreadForMortar = getRandomInt(10,20);
		this.minEnemiesForMortar = getRandomInt(10,20);
		
		this.minEnemiesForFieldHow = getRandomInt(15,35);
		
		this.maxSpreadForSmgDrum = getRandomInt(15,35);
		this.minEnemiesForSmgDrum = getRandomInt(5,15);
		
		this.minSpreadForRailgunSniper = getRandomInt(15,35);
		this.maxEnemiesForRailgunSniper = getRandomInt(10,30);
		
		this.maxSpreadForRailgun = getRandomInt(10,30);
		this.maxEnemiesForRailgun = getRandomInt(15,35);
		
		this.maxSpreadForGrenadier = getRandomInt(10,30);
		this.minEnemiesForGrenadier = getRandomInt(15,35);
		
		this.minSummoningTimeForObserver = getRandomInt(minSummoningTime,defaultSummoningTime*5);
		
		this.minSpreadForSurroundingMGDefense = getRandomInt(30, 50);
		
		this.minEnemiesForMG = getRandomInt(1,10);
		
		this.maxEnemiesForRevolver = getRandomInt(1,10);
	}
	@Override
	public void setRequiredAnimation()
	{
		
	}
	@Override
	protected void executeTask()
	{
		if(sneakingTime==0)
		{
			hans.setSneaking(false);
			sneakingTime = -1;
		}
		else if(sneakingTime>0)
			sneakingTime--;
		if(summoningTime<=0)
		{
			int enemyCount = 0;
			double enemyTotalX = 0;
			double enemyTotalY = 0;
			double enemyTotalZ = 0;
			int hansCount = 0;
			int observerCount = 0;
			double nearestEnemyDist = Double.POSITIVE_INFINITY;
			for(Entity entity : hans.world.loadedEntityList)
			{
				if(hans.isValidTarget(entity))
				{
					enemyCount++;
					double dist = hans.getDistance(entity.posX, entity.posY, entity.posZ);
					if(dist < nearestEnemyDist)
						nearestEnemyDist = dist;
					enemyTotalX += entity.posX;
					enemyTotalY += entity.posY;
					enemyTotalZ += entity.posZ;
				}
				else if(entity instanceof EntityHans&&entity.getTeam() == hans.getTeam()&&!(((EntityHans)entity).getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof ItemIIRadioConfigurator))
				{
					hansCount++;
					if(((EntityHans)entity).getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemIIBinoculars)
						observerCount++;
				}
			}
			summoningTime = Math.max(minSummoningTime, defaultSummoningTime/(observerCount+1));
			if(enemyCount<=0)
				return;
			double avgX = enemyTotalX/enemyCount;
			double avgY = enemyTotalY/enemyCount;
			double avgZ = enemyTotalZ/enemyCount;
			double mgX = Math.floor((avgX+hans.posX)/2);
			double mgZ = Math.floor((avgZ+hans.posZ)/2);
			double difX = avgX-mgX;
			double difZ = avgZ-mgZ;
			float yaw;
			if(Math.abs(difX)>Math.abs(difZ)&&difX>=0)
				yaw = -90;
			else if(Math.abs(difZ)>Math.abs(difZ)&&difZ>=0)
				yaw = 0;
			else if(Math.abs(difX)>Math.abs(difZ)&&difX<0)
				yaw = 90;
			else
				yaw = 180;
			double spread = Math.abs(hans.getDistance(avgX,avgY,avgZ)-nearestEnemyDist);
			if(nearestEnemyDist>dangerDistance&&spread<=maxSpreadForMG&&enemyCount>=minEnemiesForMG&&trySummoningMG(mgX, hans.posY, mgZ, yaw, (spread<=maxSpreadForMortar&&enemyCount>=minEnemiesForMortar), (enemyCount>=minEnemiesForFieldHow), (spread>=minSpreadForSurroundingMGDefense)))
				return;
			if((nearestEnemyDist<dangerDistance&&hansCount<enemyCount)||hansCount*enemiesPerHans<enemyCount)
			{
				hans.setSneaking(true);
				sneakingTime = 20;
				if(nearestEnemyDist>dangerDistance&&(summoningTime*(enemyCount-hansCount))>=minSummoningTimeForObserver)
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "observer"), hans);
				else if(enemyCount<=maxEnemiesForRevolver)
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "revolver"), hans);
				else if(spread<=maxSpreadForChemthrower&&enemyCount>=minEnemiesForChemthrower)
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "chemthrower"), hans);
				else if(spread<=maxSpreadForSmgDrum&&enemyCount>=minEnemiesForSmgDrum)
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "smg_drum"), hans);
				else if(spread>=minSpreadForRailgunSniper&&enemyCount<=maxEnemiesForRailgunSniper)
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "railgun_sniper"), hans);
				else if(spread<=maxSpreadForRailgun&&enemyCount<=maxEnemiesForRailgun)
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "railgun"), hans);
				else if(spread<=maxSpreadForGrenadier&&enemyCount<=minEnemiesForGrenadier)
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "grenadier"), hans);
//				else if(spread<=maxSpreadForFieldMG&&enemyCount<=maxEnemiesForFieldMG)
//					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "field_mg"), hans);
				else
					CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "smg"), hans);
			}
		}
		else
			summoningTime--;
	}
	private boolean trySummoningMG(double x, double y, double z, float yaw, boolean spawnMortar, boolean spawnFieldHow, boolean surroundDefense)
	{
		while(hans.world.isBlockFullCube(new BlockPos(x,y+1,z)))
		{
			y++;
			if(y+1 >= 256)
				return false;
		}
		while(!hans.world.isBlockFullCube(new BlockPos(x,y,z)) && hans.world.getBlockState(new BlockPos(x,y,z)).getMaterial() != Material.WATER)
		{
            y--;
            if(y < 0)
            	return false;
		}
		y++;
		if(hans.world.getEntitiesWithinAABB(EntityMachinegun.class, getAABB(x,y,z)).size()>0)
			return false;
		if(hans.world.getEntitiesWithinAABB(EntityMortar.class, getAABB(x,y,z)).size()>0)
			return false;
		if(hans.world.getEntitiesWithinAABB(EntityFieldHowitzer.class, getAABB(x,y,z)).size()>0)
			return false;
		hans.setSneaking(true);
		sneakingTime = 20;
		if(hans.world.getBlockState(new BlockPos(x,y-1,z)).getMaterial() == Material.WATER)
			CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "naval_mg"), hans, new Vec3d(x,y,z), false, yaw);
		else if(spawnMortar && spawnFieldHow)
			CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "field_howi"), hans, new Vec3d(x,y,z), false, yaw);
		else if(spawnMortar)
		{
			CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "mortar"), hans, new Vec3d(x,y,z), false, yaw);
			EnumFacing facing = EnumFacing.fromAngle(yaw);
			x += facing.getFrontOffsetX();
			z += facing.getFrontOffsetZ();
			if(spawnSandbag(x+facing.getFrontOffsetX(),y,z+facing.getFrontOffsetZ(),facing.getOpposite()))
			{
				EnumFacing facingLeft = facing.rotateYCCW();
				EnumFacing facingRight = facing.rotateY();
				spawnSandbag(x+facingLeft.getFrontOffsetX(),y,z+facingLeft.getFrontOffsetZ(),facingLeft.getOpposite());
				spawnSandbag(x+facingRight.getFrontOffsetX(),y,z+facingRight.getFrontOffsetZ(),facingRight.getOpposite());
			}
		}
		else
		{
			CommandIIHans.commanderSpawnSquad(new ResourceLocation(ImmersiveIntelligence.MODID, "heavy_mg"), hans, new Vec3d(x,y,z), false, yaw);
			EnumFacing facing = EnumFacing.fromAngle(yaw);
			if(spawnSandbag(x+facing.getFrontOffsetX(),y,z+facing.getFrontOffsetZ(),facing.getOpposite()))
			{
				if(surroundDefense)
				{
					EnumFacing facingLeft = facing.rotateYCCW();
					EnumFacing facingRight = facing.rotateY();
					spawnSandbag(x+facingLeft.getFrontOffsetX(),y,z+facingLeft.getFrontOffsetZ(),facingLeft.getOpposite());
					spawnSandbag(x+facingRight.getFrontOffsetX(),y,z+facingRight.getFrontOffsetZ(),facingRight.getOpposite());
				}
				else
				{
					if(facing.getAxis()==EnumFacing.Axis.X)
					{
						spawnSandbag(x+facing.getFrontOffsetX(),y,z+1,facing.getOpposite());
						spawnSandbag(x+facing.getFrontOffsetX(),y,z-1,facing.getOpposite());
					}
					else
					{
						spawnSandbag(x+1,y,z+facing.getFrontOffsetZ(),facing.getOpposite());
						spawnSandbag(x-1,y,z+facing.getFrontOffsetZ(),facing.getOpposite());
					}
				}
			}
		}
		return true;
	}
	private AxisAlignedBB getAABB(double x, double y, double z)
	{
		int dist = 5;
		return new AxisAlignedBB(x+dist, y+dist, z+dist, x-dist, y-dist, z-dist);
	}
	private boolean spawnSandbag(double x, double y, double z, EnumFacing facing)
	{
		if(!hans.world.getBlockState(new BlockPos(x,y,z)).getMaterial().isReplaceable())
			return false;
		if(!hans.world.isBlockFullCube(new BlockPos(x,y-1,z)))
			return false;
		hans.world.setBlockState(new BlockPos(x,y,z), IIContent.blockSandbags.getInitDefaultState().withProperty(IEProperties.FACING_HORIZONTAL, facing));
		return true;
	}
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
		return getWeapon().getItem() instanceof ItemIIRadioConfigurator;
	}
}
