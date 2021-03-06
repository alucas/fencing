package nl.tyrope.fencing.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import nl.tyrope.fencing.Refs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FenceBlockItem extends ItemBlock {

	public IIcon[] FenceItemIcons;

	public FenceBlockItem(Block block) {
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName("fenceBlock");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister icon) {
		FenceItemIcons = new IIcon[Refs.fenceSubNames.length];
		for (int i = 0; i < Refs.fenceSubNames.length; i++) {
			FenceItemIcons[i] = icon.registerIcon("fencing:iconFence"
					+ Refs.fenceSubNames[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack i, int j) {
		return FenceItemIcons[i.getItemDamage()];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg) {
		return FenceItemIcons[dmg];
	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName()
				+ Refs.fenceSubNames[itemstack.getItemDamage()];
	}
}