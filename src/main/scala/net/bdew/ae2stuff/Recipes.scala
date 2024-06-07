package net.bdew.ae2stuff

import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.ae2stuff.items.AdvWirelessKit
import net.minecraft.item.ItemStack

object Recipes {
  def load(): Unit = {
    //add recipe to clear NBT from adv wireless kit
    GameRegistry.addShapelessRecipe(
      new ItemStack(AdvWirelessKit),
      AdvWirelessKit
    )
  }
}
