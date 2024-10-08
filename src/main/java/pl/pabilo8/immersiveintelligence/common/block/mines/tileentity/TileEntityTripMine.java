package pl.pabilo8.immersiveintelligence.common.block.mines.tileentity;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedCollisionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.common.IIConfigHandler.IIConfig.Weapons.Mines;
import pl.pabilo8.immersiveintelligence.common.item.ItemIITripWireCoil;
import pl.pabilo8.immersiveintelligence.common.item.tools.ItemIITrenchShovel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pabilo8
 * @since 02.02.2021
 */
public class TileEntityTripMine extends TileEntityMineBase implements IBlockBounds, IAdvancedCollisionBounds
{
	public static final Material[] MATCHING_MATERIALS = new Material[]{Material.GROUND, Material.GRASS, Material.SAND, Material.GOURD};

	private static final Vec3d CONN_OFFSET = new Vec3d(0.5, 0.25, 0.5);
	private static final AxisAlignedBB[] AABB = new AxisAlignedBB[16];

	static
	{
		for(int i = 0; i < 16; i++)
		{
			AABB[i] = new AxisAlignedBB(0.25f, -0.625f*(i/15f), 0.25f,
					0.75f, 0.625f*(1f-(i/15f)), 0.75f);
		}
	}

	public boolean grass = false;
	public int digLevel = 0;

	@Override
	public void readCustomNBT(NBTTagCompound nbtTagCompound, boolean b)
	{
		grass = nbtTagCompound.getBoolean("grass");
		digLevel = nbtTagCompound.getInteger("digLevel");
		super.readCustomNBT(nbtTagCompound, b);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbtTagCompound, boolean b)
	{
		nbtTagCompound.setBoolean("grass", grass);
		nbtTagCompound.setInteger("digLevel", digLevel);
		super.writeCustomNBT(nbtTagCompound, b);
	}

	@Override
	public Vec3d getConnectionOffset(Connection con)
	{
		return CONN_OFFSET;
	}

	@Override
	protected boolean isRelay()
	{
		return true;
	}

	@Override
	public boolean canConnectCable(WireType cableType, TargetingInfo target, Vec3i offset)
	{
		return ItemIITripWireCoil.TRIPWIRE_CATEGORY.equals(cableType.getCategory());
	}

	@Override
	public float getDamageAmount(Entity e, Connection c)
	{
		return 1f;
	}

	@Override
	public void processDamage(Entity e, float amount, Connection c)
	{
		if(e.doesEntityNotTriggerPressurePlate())
			return;
		ResourceLocation key = EntityList.getKey(e instanceof MultiPartEntityPart?(Entity)((MultiPartEntityPart)e).parent: e);
		if(key!=null&&Arrays.asList(Mines.tripmineBlacklist).contains(key.toString()))
			return;

		TileEntity tileStart = world.getTileEntity(c.start);
		TileEntity tileEnd = world.getTileEntity(c.end);
		if(tileStart instanceof TileEntityTripMine)
			((TileEntityTripMine)tileStart).explode();
		if(tileEnd instanceof TileEntityTripMine)
			((TileEntityTripMine)tileEnd).explode();
	}

	@Override
	public boolean interact(EnumFacing side, EntityPlayer player, EnumHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ)
	{
		if(super.interact(side, player, hand, heldItem, hitX, hitY, hitZ))
			return true;
		//Digging the mine in
		if(this.digLevel < 15&&heldItem.getItem().getToolClasses(heldItem).contains("shovel"))
		{
			heldItem.damageItem(1, player);
			Material material = world.getBlockState(pos.down()).getMaterial();
			if(Arrays.stream(MATCHING_MATERIALS).noneMatch(material1 -> material1==material))
				return true;
			digLevel += heldItem.getItem() instanceof ItemIITrenchShovel?5: 1;
			world.playSound(pos.getX(), pos.getY()+1, pos.getZ(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1f, 1f, false);
			return true;
		}
		//Placing grass on top
		else if(digLevel==15&&heldItem.getItem() instanceof ItemBlock&&((ItemBlock)heldItem.getItem()).getBlock()==Blocks.TALLGRASS)
		{
			grass = true;
			if(!player.isCreative())
				heldItem.shrink(1);
			world.playSound(pos.getX(), pos.getY()+1, pos.getZ(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1f, 1f, false);
			return true;
		}
		return false;
	}

	@Override
	public List<AxisAlignedBB> getAdvancedColisionBounds()
	{
		ArrayList<AxisAlignedBB> list = new ArrayList<>();
		list.add(AABB[digLevel].offset(getPos()));
		return list;
	}

	@Override
	public float[] getBlockBounds()
	{
		return new float[]{0.25f, -0.625f*(digLevel/15f), 0.25f,
				0.75f, 0.625f*(1f-(digLevel/15f)), 0.75f};
	}

	@Override
	public void onEntityCollision(World world, Entity entity)
	{
		super.onEntityCollision(world, entity);
		if(digLevel > 6||entity.posY > this.getPos().getY())
			this.explode();
	}
}
