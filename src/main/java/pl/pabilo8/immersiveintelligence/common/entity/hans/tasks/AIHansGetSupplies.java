package pl.pabilo8.immersiveintelligence.common.entity.hans.tasks;

import blusunrize.immersiveengineering.common.items.ItemRailgun;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;
import pl.pabilo8.immersiveintelligence.common.items.ammunition.ItemIIAmmoGrenade;
import pl.pabilo8.immersiveintelligence.common.items.armor.ItemIILightEngineerHelmet;
import pl.pabilo8.immersiveintelligence.common.items.weapons.ItemIIRailgunOverride;
import pl.pabilo8.immersiveintelligence.common.items.weapons.ItemIISubmachinegun;

public class AIHansGetSupplies extends EntityAIBase
{
    private final EntityHans hans;
    private BlockPos target = null;

    public AIHansGetSupplies(EntityHans hans)
    {
        this.hans = hans;
        setMutexBits(3);
    }
    @Override
    public void resetTask()
    {
        target = null;
    }

    @Override
    public boolean shouldExecute()
    {
        if(target!=null)
            return true;
        if(hans.isRiding())
            return false;
        if(hans.ticksExisted%25!=0)
            return false;
        ItemStack weapon = getHeldWeapon();
        boolean needsWeapon = weapon.isEmpty();
        boolean needsHelmet = needsHelmet();
        boolean needsAmmo = (!needsWeapon&&!hans.hasAmmo&&canGetAmmoFor(weapon));
        if(!needsWeapon&&!needsHelmet&&!needsAmmo)
            return false;
        BlockPos closestPos = null;
        double closestDist = Double.MAX_VALUE;
        BlockPos hansPos = hans.getPosition();
        for(int x = 1; x <= Math.min(closestDist, 50); x++)
        {
            for(int y = 1; y <= Math.min(closestDist, 10); y++)
            {
                for(int z = 1; z <= Math.min(closestDist, 50); z++)
                {
                    int xPos = hansPos.getX() + getBackforthOffset(x);
                    int yPos = hansPos.getY() + getBackforthOffset(y);
                    int zPos = hansPos.getZ() + getBackforthOffset(z);
                    BlockPos checkPos = new BlockPos(xPos, yPos, zPos);
                    if(getSuppliesSlot(checkPos, false) == -1)
                        continue;
                    if(closestPos == null || hans.getDistanceSq(checkPos) < closestDist)
                    {
                        closestPos = checkPos;
                        closestDist = hans.getDistanceSq(checkPos);
                    }
                }
            }
        }
        if(closestPos == null)
            return false;
        target = closestPos;
        return true;
    }
    private int getBackforthOffset(int num)
    {
        return Math.floorDiv(num,2)*(num%2==0?-1:1);
    }

    private int getSuppliesSlot(BlockPos pos, boolean extra)
    {
        ItemStack weapon = getHeldWeapon();
        boolean needsWeapon = weapon.isEmpty();
        boolean needsHelmet = needsHelmet();
        int ammoCount = 0;
        if(extra&&canGetAmmoFor(weapon))
        {
            final IItemHandler capability = hans.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if(capability!=null)
            {
                for (int i = 0; i < capability.getSlots(); i++) {
                    ItemStack itemstack = capability.getStackInSlot(i);
                    if(isAmmoFor(weapon,itemstack))
                    {
                        ammoCount += itemstack.getCount();
                        if(ammoCount >= getPreferredAmmoFor(weapon))
                            break;
                    }
                }
            }
        }
        boolean extraAmmo = extra && ammoCount < getPreferredAmmoFor(weapon);
        boolean needsAmmo = (!needsWeapon&&canGetAmmoFor(weapon)&&(!hans.hasAmmo||extraAmmo));
        TileEntity tileEntity = hans.world.getTileEntity(pos);
        if(tileEntity == null)
            return -1;
        IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if(handler == null)
            return -1;
        for(int i = 0; i < handler.getSlots(); i++)
        {
            ItemStack stack = handler.getStackInSlot(i);
            if((needsWeapon&&isWeapon(stack))
                    ||(needsHelmet&&stack.getItem() instanceof ItemIILightEngineerHelmet)
                    ||(needsAmmo&&isAmmoFor(weapon,stack))
            )
            {
                return i;
            }
        }
        return -1;
    }
    private boolean canGetAmmoFor(ItemStack weapon)
    {
        return weapon.getItem() instanceof ItemIISubmachinegun
                ||weapon.getItem() instanceof ItemRailgun
                ||weapon.getItem() instanceof ItemIIAmmoGrenade
                ;
    }
    private boolean isAmmoFor(ItemStack weapon, ItemStack stack)
    {
        if(!canGetAmmoFor(weapon))
            return false;
        if(weapon.getItem() instanceof ItemIISubmachinegun)
            return IIContent.itemSubmachinegun.isAmmo(stack, weapon);
        else if(weapon.getItem() instanceof ItemRailgun)
            return ItemIIRailgunOverride.isAmmo(stack);
        else if(weapon.getItem() instanceof ItemIIAmmoGrenade)
            return stack.getItem() instanceof ItemIIAmmoGrenade;
        return false;
    }
    private int getPreferredAmmoFor(ItemStack weapon)
    {
        if(!canGetAmmoFor(weapon))
            return 0;
        if(weapon.getItem() instanceof ItemIISubmachinegun)
            return 4;
        else if(weapon.getItem() instanceof ItemRailgun)
            return 16;
        else if(weapon.getItem() instanceof ItemIIAmmoGrenade)
            return 8;
        return 0;
    }

    private boolean needsHelmet()
    {
        return hans.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty();
    }

    private ItemStack getHeldWeapon()
    {
        if(isWeapon(hans.getHeldItem(EnumHand.MAIN_HAND)))
            return hans.getHeldItem(EnumHand.MAIN_HAND);
        if(isWeapon(hans.getHeldItem(EnumHand.OFF_HAND)))
            return hans.getHeldItem(EnumHand.OFF_HAND);
        return ItemStack.EMPTY;
    }

    @Override
    public void updateTask()
    {
        if(hans.getDistanceSq(target) < 1.5)
        {
            int slot = getSuppliesSlot(target, true);
            if(slot == -1)
            {
                target = null;
                return;
            }
            TileEntity tileEntity = hans.world.getTileEntity(target);
            if(tileEntity == null)
                return;
            IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if(handler == null)
                return;
            if(handler.getStackInSlot(slot).getItem() instanceof ItemIILightEngineerHelmet)
            {
                if(!hans.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty())
                    return;
                hans.setItemStackToSlot(EntityEquipmentSlot.HEAD,handler.extractItem(slot,1,false));
                return;
            }
            boolean w1 = isWeapon(hans.getHeldItem(EnumHand.OFF_HAND));
            boolean w2 = isWeapon(hans.getHeldItem(EnumHand.MAIN_HAND));
            if((w1||w2)&&isWeapon(handler.getStackInSlot(slot)))
                return;
            if(!isWeapon(handler.getStackInSlot(slot)))
                hans.hasAmmo = true;
            EnumHand hand;
            if(hans.getHeldItem(EnumHand.MAIN_HAND).isEmpty())
                hand = EnumHand.MAIN_HAND;
            else if(hans.getHeldItem(EnumHand.OFF_HAND).isEmpty())
                hand = EnumHand.OFF_HAND;
            else if(!isWeapon(handler.getStackInSlot(slot)))
            {
                for(int i = 0; i < hans.mainInventory.size(); i++)
                {
                    if(hans.mainInventory.get(i).isEmpty())
                    {
                        hans.mainInventory.set(i, handler.extractItem(slot, 1, false));
                        break;
                    }
                }
                return;
            }
            else
            {
                hand = (isWeapon(hans.getHeldItem(EnumHand.MAIN_HAND)) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                BlockPos hansPos = hans.getPosition();
                ItemStack heldItem = hans.getHeldItem(hand);
                EntityItem entity = new EntityItem(hans.world, hansPos.getX(), hansPos.getY(), hansPos.getZ(), heldItem);
                hans.world.spawnEntity(entity);
            }
            hans.setHeldItem(hand, handler.extractItem(slot, 1, false));
            hans.updateWeaponTasks();
        }
        else if(hans.ticksExisted%25==0)
        {
            hans.getNavigator().clearPath();
            hans.getNavigator().setPath(hans.getNavigator().getPathToPos(target), 1);
        }
    }

    public boolean isWeapon(ItemStack stack)
    {
        return hans.isWeapon(stack);
    }
}
