package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

/**
 * Verifies the chef villager actually receives datapack-defined trades.
 * Directly invokes {@code updateTrades} since command-based summoning bypasses it.
 */
public final class TradeGameTests {
   private TradeGameTests() {
   }

   public static void chefHasTrades(GameTestHelper helper) {
      ServerLevel level = helper.getLevel();
      @SuppressWarnings("unchecked")
      net.minecraft.world.entity.EntityType<Villager> villagerType =
         (net.minecraft.world.entity.EntityType<Villager>) BuiltInRegistries.ENTITY_TYPE.getValue(
            Identifier.withDefaultNamespace("villager"));
      Villager villager = villagerType.create(level, EntitySpawnReason.COMMAND);
      if (villager == null) {
         helper.fail("could not create villager");
         return;
      }
      ResourceKey<VillagerProfession> chefKey = ResourceKey.create(Registries.VILLAGER_PROFESSION,
         Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "chef"));
      var chefHolder = level.registryAccess().lookupOrThrow(Registries.VILLAGER_PROFESSION)
         .get(chefKey).orElse(null);
      if (chefHolder == null) {
         helper.fail("chef profession not registered");
         return;
      }
      villager.setVillagerData(villager.getVillagerData()
         .withProfession(chefHolder)
         .withLevel(3));
      villager.setPos(helper.absolutePos(new BlockPos(1, 1, 1)).getX() + 0.5D, helper.absolutePos(new BlockPos(1, 1, 1)).getY(), helper.absolutePos(new BlockPos(1, 1, 1)).getZ() + 0.5D);
      try {
         var m = Villager.class.getDeclaredMethod("updateTrades", ServerLevel.class);
         m.setAccessible(true);
         m.invoke(villager, level);
      } catch (Exception e) {
         helper.fail("updateTrades failed: " + e);
         return;
      }
      int offers = villager.getOffers().size();
      if (offers <= 0) {
         helper.fail("chef villager has no trades (offers=" + offers + ")");
         return;
      }
      helper.succeed();
   }
}
