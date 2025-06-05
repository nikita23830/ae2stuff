/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff

import com.gtnewhorizon.gtnhlib.keybind.SyncedKeybind

import java.io.File
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event._
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.relauncher.Side
import net.bdew.ae2stuff.compat.WrenchRegistry
import net.bdew.ae2stuff.items.visualiser.{
  VisualiserOverlayRender,
  VisualiserPlayerTracker
}
import net.bdew.ae2stuff.machines.wireless.WirelessOverlayRender
import net.bdew.ae2stuff.misc.{Icons, MouseEventHandler, OverlayRenderHandler}
import net.bdew.ae2stuff.network.NetHandler
import net.bdew.lib.Event
import net.bdew.lib.gui.GuiHandler
import org.apache.logging.log4j.Logger
import org.lwjgl.input.Keyboard

@Mod(
  modid = AE2Stuff.modId,
  version = "GRADLETOKEN_VERSION",
  name = "AE2 Stuff",
  dependencies =
    "required-after:appliedenergistics2;required-after:bdlib@[1.9.4.109,);required-after:gtnhlib@[0.6.5,)",
  modLanguage = "scala"
)
object AE2Stuff {
  var log: Logger = null
  var instance = this

  final val modId = "ae2stuff"
  final val channel = "bdew.ae2stuff"

  var configDir: File = null

  val guiHandler = new GuiHandler
  val keybindModeId = "ae2stuff.key.mode"
  val keybindModeSwitch = SyncedKeybind.createConfigurable(
    keybindModeId,
    "itemGroup.bdew.ae2stuff",
    Keyboard.CHAR_NONE
  )

  def logDebug(msg: String, args: Any*) = log.debug(msg.format(args: _*))
  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*) = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*) =
    log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*) =
    log.error(msg.format(args: _*), t)

  @EventHandler
  def preInit(event: FMLPreInitializationEvent): Unit = {
    log = event.getModLog
    configDir = new File(event.getModConfigurationDirectory, "AE2Stuff")
    TuningLoader.loadConfigFiles()
    Machines.load()
    Items.load()
  }

  @EventHandler
  def init(event: FMLInitializationEvent): Unit = {
    NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler)
    NetHandler.init()
    if (event.getSide == Side.CLIENT) {
      Icons.init()
      OverlayRenderHandler.register(WirelessOverlayRender)
      OverlayRenderHandler.register(VisualiserOverlayRender)
      MouseEventHandler.init()
    }
    VisualiserPlayerTracker.init()
    WrenchRegistry.init()
    FMLInterModComms.sendMessage(
      "Waila",
      "register",
      "net.bdew.ae2stuff.waila.WailaHandler.loadCallback"
    )
  }

  val onPostInit = Event[FMLPostInitializationEvent]

  @EventHandler
  def postInit(event: FMLPostInitializationEvent): Unit = {
    TuningLoader.loadDelayed()
    onPostInit.trigger(event)
    Recipes.load()
  }

  @EventHandler
  def onServerStarting(event: FMLServerStartingEvent): Unit = {
    VisualiserPlayerTracker.clear()
  }
}
