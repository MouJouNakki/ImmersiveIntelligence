package pl.pabilo8.immersiveintelligence.client.manual;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.ManualPageMultiblock;
import blusunrize.lib.manual.ManualPages;
import net.minecraft.item.ItemStack;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.block.data_device.BlockIIDataDevice.IIBlockTypes_Connector;
import pl.pabilo8.immersiveintelligence.common.block.multiblock.metal_multiblock1.multiblock.MultiblockRadar;
import pl.pabilo8.immersiveintelligence.common.item.tools.ItemIIBinoculars.Binoculars;
import pl.pabilo8.immersiveintelligence.common.util.IILib;

/**
 * @author Pabilo8
 * @since 18-01-2020
 */
public class IIManualIntelligence extends IIManual
{
	public static IIManualIntelligence INSTANCE = new IIManualIntelligence();

	@Override
	public String getCategory()
	{
		return IILib.CAT_INTELLIGENCE;
	}

	@Override
	public void addPages()
	{
		ManualHelper.addEntry("intel_main", getCategory(),
				new ManualPages.Text(ManualHelper.getManual(), "intel_main0")
		);

		ManualHelper.addEntry("binoculars", getCategory(),
				new ManualPages.Crafting(ManualHelper.getManual(), "binoculars0", IIContent.itemBinoculars.getStack(Binoculars.BINOCULARS)),
				new ManualPages.Crafting(ManualHelper.getManual(), "binoculars1", IIContent.itemBinoculars.getStack(Binoculars.INFRARED_BINOCULARS))
		);

		ManualHelper.addEntry("alarm_siren", getCategory(),
				new ManualPages.Crafting(ManualHelper.getManual(), "alarm_siren0", new ItemStack(IIContent.blockDataConnector, 1, IIBlockTypes_Connector.ALARM_SIREN.getMeta()))
		);

		ManualHelper.addEntry("tripod_periscope", getCategory(),
				new ManualPages.Crafting(ManualHelper.getManual(), "tripod_periscope0", new ItemStack(IIContent.itemTripodPeriscope)),
				new ManualPages.Text(ManualHelper.getManual(), "tripod_periscope1")
		);

		ManualHelper.addEntry("radar", getCategory(),
				new ManualPageMultiblock(ManualHelper.getManual(), "radar0", MultiblockRadar.INSTANCE),
				new ManualPages.Text(ManualHelper.getManual(), "radar1")
		);
	}
}
