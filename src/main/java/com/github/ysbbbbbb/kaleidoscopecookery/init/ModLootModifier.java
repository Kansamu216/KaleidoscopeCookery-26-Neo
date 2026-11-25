package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceEntityMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.RecipeRandomlyFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registers the mod's custom loot condition and function codecs. Minecraft 26
 * replaced the old {@code LootItemConditionType}/{@code LootItemFunctionType}
 * wrappers with plain {@link MapCodec} registry entries.
 */
@EventBusSubscriber()
public final class ModLootModifier {
   @SubscribeEvent
   public static void register(RegisterEvent evt) {
      if (evt.getRegistryKey().equals(net.minecraft.core.registries.Registries.LOOT_CONDITION_TYPE)) {
         Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, AdvanceEntityMatchTool.ID,
            (MapCodec<? extends LootItemCondition>) AdvanceEntityMatchTool.CODEC);
         Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, AdvanceBlockMatchTool.ID,
            (MapCodec<? extends LootItemCondition>) AdvanceBlockMatchTool.CODEC);
      }
      if (evt.getRegistryKey().equals(net.minecraft.core.registries.Registries.LOOT_FUNCTION_TYPE)) {
         Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, RecipeRandomlyFunction.ID,
            (MapCodec<? extends LootItemFunction>) RecipeRandomlyFunction.CODEC);
      }
   }

}
