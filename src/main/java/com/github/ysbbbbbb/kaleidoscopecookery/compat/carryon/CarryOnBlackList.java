package com.github.ysbbbbbb.kaleidoscopecookery.compat.carryon;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.InterModComms;

/**
 * Stops Carry On from picking up this mod's blocks.
 * <p>
 * Verified against the Carry On 2.12.2 (NeoForge 26.3) jar: its
 * {@code tschipp.carryon.events.ModBusEvents#serverLoad(InterModProcessEvent)}
 * reads {@code InterModComms.getMessages("carryon")} and compares
 * {@code IMCMessage#method()} against {@code "blacklistBlock"}, so the
 * Forge-era inter-mod-communication channel still works unchanged on 26.x.
 */
public final class CarryOnBlackList {
   public static final String CARRY_ON_ID = "carryon";

   private CarryOnBlackList() {
   }

   public static void addBlackList() {
      int count = 0;
      for (Block block : BuiltInRegistries.BLOCK) {
         Identifier id = BuiltInRegistries.BLOCK.getKey(block);
         if (id != null && KaleidoscopeCookery.MOD_ID.equals(id.getNamespace())) {
            InterModComms.sendTo(CARRY_ON_ID, "blacklistBlock", id::toString);
            count++;
         }
      }
      KaleidoscopeCookery.LOGGER.info("Carry On detected: blacklisted {} blocks from pickup", count);
   }
}
