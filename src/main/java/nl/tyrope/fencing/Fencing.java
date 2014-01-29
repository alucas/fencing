package nl.tyrope.fencing;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import nl.tyrope.fencing.Refs.MetaValues;
import nl.tyrope.fencing.blocks.ElectricFenceBlock;
import nl.tyrope.fencing.blocks.FenceBlock;
import nl.tyrope.fencing.blocks.PaintedFenceBlock;
import nl.tyrope.fencing.creativetab.FencingTab;
import nl.tyrope.fencing.items.ElectricFenceBlockItem;
import nl.tyrope.fencing.items.FenceBlockItem;
import nl.tyrope.fencing.items.FencePoleItem;
import nl.tyrope.fencing.items.PaintedFenceBlockItem;
import nl.tyrope.fencing.proxy.Common;
import nl.tyrope.fencing.tileEntity.ElectricFenceEntity;
import nl.tyrope.fencing.util.BarbedDmg;
import nl.tyrope.fencing.util.ElecDmg;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "fencing", name = "Fencing", version = "0.4.dev", dependencies = "after:IC2")
@NetworkMod(clientSideRequired = true)
public class Fencing {

	@Instance(value = "fencing")
	public static Fencing instance;

	@SidedProxy(clientSide = "nl.tyrope.fencing.proxy.Client", serverSide = "nl.tyrope.fencing.proxy.Common")
	public static Common proxy;

	public FencePoleItem fencePole;
	public FenceBlock fenceBlock;
	public ElectricFenceBlock electricFenceBlock;
	public PaintedFenceBlock paintedFenceBlock;

	/**
	 * This is code that is executed prior to your mod being initialized into of
	 * Minecraft.
	 * 
	 * @param event
	 *            The Forge ModLoader pre-initialization event
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		// Create creative tab.
		Refs.creativeTab = new FencingTab();

		// Load configuration file.
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		config.load();

		// Fetch values.
		Refs.PoleID = config.getItem("fencePole", 5000).getInt();
		Refs.FenceID = config.getBlock("fenceBlock", 500).getInt();
		Refs.ElecFenceID = config.getBlock("fenceBlockElectric", 501).getInt();
		Refs.PaintedFenceID = config.getBlock("fenceBlockPainted", 502)
				.getInt();
		Refs.dmgMulti = config.get(
				"misc",
				"damage_multiplier",
				1,
				"Multiplier for fence-inflicted damage."
						+ System.getProperty("line.separator")
						+ "Default: (1x)"
						+ System.getProperty("line.separator")
						+ "0.5hearts for barbed,"
						+ System.getProperty("line.separator")
						+ "max. 1 heart for tin &"
						+ System.getProperty("line.separator")
						+ "max. 2 hearts for copper.").getInt();
		Refs.dropCenter = config.get(
				"misc",
				"drop_on_cut",
				true,
				"Whether or not to drop the center"
						+ System.getProperty("line.separator")
						+ "item when cutting a fence.").getBoolean(true);

		// Save them in case they weren't set before.
		config.save();

		// Make items and blocks
		fencePole = new FencePoleItem(Refs.PoleID);
		fenceBlock = new FenceBlock(Refs.FenceID);
		paintedFenceBlock = new PaintedFenceBlock(Refs.PaintedFenceID);

		// Just in case it gets shifted.
		Refs.FenceID = fenceBlock.blockID;
		Refs.PaintedFenceID = paintedFenceBlock.blockID;

		// Make damage objects.
		Refs.DmgSrcs.barbed = new BarbedDmg();
	}

	/**
	 * This is code that is executed when your mod is being initialized in
	 * Minecraft.
	 * 
	 * @param event
	 *            The Forge ModLoader initialization event
	 */
	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new ItemStack(fencePole, 4), "xx", "xx", "xx",
				'x', new ItemStack(Item.stick));

		MinecraftForge.setBlockHarvestLevel(fenceBlock, "axe", 0);

		// register block
		GameRegistry.registerBlock(fenceBlock, FenceBlockItem.class,
				"FenceBlockItem");
		GameRegistry.registerBlock(paintedFenceBlock,
				PaintedFenceBlockItem.class, "PaintedFenceBlockItem");

		ItemStack pole = new ItemStack(fencePole);

		// Regular pole/X/pole crafting
		GameRegistry.addRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceString), "xyx", 'x', pole, 'y', new ItemStack(
				Item.silk));
		GameRegistry.addRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceIron), "xyx", 'x', pole, 'y', new ItemStack(
				Item.ingotIron));
		GameRegistry.addShapelessRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceSilly), new ItemStack(Item.slimeBall),
				new ItemStack(fenceBlock));
		GameRegistry.addRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceBarbed), "xyx", 'x', pole, 'y', new ItemStack(
				Block.fenceIron));
		GameRegistry.addRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceWood), "xyx", 'x', pole, 'y', new ItemStack(
				Item.stick));

		// Repairs
		GameRegistry.addShapelessRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceString), new ItemStack(fenceBlock, 1,
				MetaValues.FenceCut), new ItemStack(Item.silk));
		GameRegistry.addShapelessRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceIron), new ItemStack(fenceBlock, 1,
				MetaValues.FenceCut), new ItemStack(Item.ingotIron));
		GameRegistry.addShapelessRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceBarbed), new ItemStack(fenceBlock, 1,
				MetaValues.FenceCut), new ItemStack(Block.fenceIron));
		GameRegistry.addShapelessRecipe(new ItemStack(fenceBlock, 1,
				MetaValues.FenceWood), new ItemStack(fenceBlock, 1,
				MetaValues.FenceCut), new ItemStack(Item.stick));

		for (int i = 0; i < 16; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(paintedFenceBlock, 1,
					i), new ItemStack(fenceBlock, 1, MetaValues.FenceWood),
					new ItemStack(Item.dyePowder, 1, 15 - i));
		}

		// Intermod compatibility.
		if (Loader.isModLoaded("IC2")) {
			System.out.println("IndustrialCraft 2 detected: ");

			// Make block
			electricFenceBlock = new ElectricFenceBlock(Refs.ElecFenceID);
			// Just in case it gets shifted.
			Refs.ElecFenceID = electricFenceBlock.blockID;

			// Register damage source
			Refs.DmgSrcs.electric = new ElecDmg();

			// register block
			MinecraftForge.setBlockHarvestLevel(electricFenceBlock, "axe", 0);
			GameRegistry.registerBlock(electricFenceBlock,
					ElectricFenceBlockItem.class, "ElectricFenceBlockItem");
			System.out.println("  Electric Fence Block registered.");
			GameRegistry.registerTileEntity(ElectricFenceEntity.class,
					"ElectricFenceEntity");
			System.out.println("  Electric Fence Entity registered.");

			System.out.print("  Loading electric fence recipes... ");

			// Load items
			ItemStack cableTin = ic2.api.item.Items.getItem("tinCableItem");
			if (cableTin != null) {
				System.out.print("Tin loaded. ");
				GameRegistry.addRecipe(new ItemStack(electricFenceBlock, 1,
						MetaValues.FenceElectricTin), "xyx", 'x', pole, 'y',
						cableTin);
			}
			ItemStack cableCopper = ic2.api.item.Items
					.getItem("copperCableItem");
			if (cableCopper != null) {
				System.out.print("Copper loaded. ");
				GameRegistry.addRecipe(new ItemStack(electricFenceBlock, 1,
						MetaValues.FenceElectricCopper), "xyx", 'x', pole, 'y',
						cableCopper);
			}
			System.out.println("Complete.");

			proxy.registerRenderers(new FenceBlock[] { fenceBlock,
					paintedFenceBlock, electricFenceBlock });
		} else {
			proxy.registerRenderers(new FenceBlock[] { fenceBlock,
					paintedFenceBlock });
		}
	}

	/**
	 * This is code that is executed after all mods are initialized in Minecraft
	 * 
	 * @param event
	 *            The Forge ModLoader post-initialization event
	 */
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
}
