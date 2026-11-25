package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModVillager {
   public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSION = DeferredRegister.create(
      BuiltInRegistries.VILLAGER_PROFESSION, "kaleidoscope_cookery"
   );
   private static ResourceKey<TradeSet> chefTradeSet(int level) {
      return ResourceKey.create(Registries.TRADE_SET,
         Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "chef/level_" + level));
   }

   private static java.util.Map<Integer, ResourceKey<TradeSet>> chefTradeSets() {
      java.util.Map<Integer, ResourceKey<TradeSet>> m = new java.util.HashMap<>();
      for (int level = 1; level <= 5; level++) {
         m.put(level, chefTradeSet(level));
      }
      return m;
   }

   public static final DeferredHolder<VillagerProfession, VillagerProfession> CHEF = VILLAGER_PROFESSION.register(
      "chef",
      () -> new VillagerProfession(
         Component.translatable("entity.minecraft.villager.kaleidoscope_cookery.chef"),
         poi -> poi.value() == ModPoi.STOVE.get(),
         poi -> poi.value() == ModPoi.STOVE.get(),
         ImmutableSet.of(),
         ImmutableSet.of(),
         SoundEvents.VILLAGER_WORK_BUTCHER,
         new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>(chefTradeSets())
      )
   );
}
