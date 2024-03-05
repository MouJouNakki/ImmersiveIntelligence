package pl.pabilo8.immersiveintelligence.common.block.mines;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.pabilo8.immersiveintelligence.client.model.builtin.AmmoModel;
import pl.pabilo8.immersiveintelligence.client.model.builtin.IAmmoModel;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.block.mines.tileentity.TileEntityTripMine;
import pl.pabilo8.immersiveintelligence.common.entity.ammo.types.EntityAmmoMine;
import pl.pabilo8.immersiveintelligence.common.item.ammo.ItemIIAmmoCasing.Casings;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author Pabilo8
 * @since 05.02.2021
 */
public class BlockIITripmine extends BlockIIMine
{
	public BlockIITripmine()
	{
		super("tripmine", TileEntityTripMine.class,
				ItemBlockTripmine::new, IOBJModelCallback.PROPERTY, IEProperties.BOOLEANS[0], IEProperties.BOOLEANS[1], IEProperties.CONNECTIONS);
	}

	@Override
	protected TileEntity getMineTileEntity()
	{
		return new TileEntityTripMine();
	}

	public static class ItemBlockTripmine extends ItemBlockMineBase
	{
		public ItemBlockTripmine(BlockIIMine b)
		{
			super(b);
		}


		@Override
		public String getName()
		{
			return "tripmine";
		}

		@Override
		public float getComponentMultiplier()
		{
			return 0.45f;
		}

		@Override
		public int getGunpowderNeeded()
		{
			return 200;
		}

		@Override
		public int getCoreMaterialNeeded()
		{
			return 2;
		}

		@Override
		public float getInitialMass()
		{
			return 0.75f;
		}

		@Override
		public int getCaliber()
		{
			return 10;
		}

		@Nonnull
		@SideOnly(Side.CLIENT)
		public Function<ItemBlockMineBase, IAmmoModel<ItemBlockMineBase, EntityAmmoMine>> get3DModel()
		{
			return AmmoModel::createExplosivesModel;
		}

		@Override
		public float getDamage()
		{
			return 0;
		}

		@Override
		public ItemStack getCasingStack(int amount)
		{
			return IIContent.itemAmmoCasing.getStack(Casings.TRIPMINE, amount);
		}
	}
}
