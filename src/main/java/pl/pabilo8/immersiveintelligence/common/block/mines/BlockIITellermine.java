package pl.pabilo8.immersiveintelligence.common.block.mines;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.pabilo8.immersiveintelligence.client.model.builtin.AmmoModel;
import pl.pabilo8.immersiveintelligence.client.model.builtin.IAmmoModel;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.block.mines.tileentity.TileEntityTellermine;
import pl.pabilo8.immersiveintelligence.common.entity.ammo.types.EntityAmmoMine;
import pl.pabilo8.immersiveintelligence.common.item.ammo.ItemIIAmmoCasing.Casings;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author Pabilo8
 * @since 05.02.2021
 */
public class BlockIITellermine extends BlockIIMine
{
	public BlockIITellermine()
	{
		super("tellermine", TileEntityTellermine.class, ItemBlockTellermine::new);
	}

	@Override
	protected TileEntity getMineTileEntity()
	{
		return new TileEntityTellermine();
	}

	public static class ItemBlockTellermine extends ItemBlockMineBase
	{
		public ItemBlockTellermine(BlockIIMine b)
		{
			super(b);
		}

		@Override
		public String getName()
		{
			return "tellermine";
		}

		@Override
		public float getComponentMultiplier()
		{
			return 0.45f;
		}

		@Override
		public int getCoreMaterialNeeded()
		{
			return 1;
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
			return IIContent.itemAmmoCasing.getStack(Casings.TELLERMINE, amount);
		}
	}
}
