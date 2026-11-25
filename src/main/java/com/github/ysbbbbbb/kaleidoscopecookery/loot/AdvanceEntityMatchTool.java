package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootModifier;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;

public class AdvanceEntityMatchTool implements LootItemCondition {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "advance_entity_match_tool");
   public static final MapCodec<AdvanceEntityMatchTool> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(tool -> tool.slot), ItemPredicate.CODEC.fieldOf("predicate").forGetter(tool -> tool.predicate)
         )
         .apply(instance, AdvanceEntityMatchTool::new)
   );
   private final EquipmentSlot slot;
   private final ItemPredicate predicate;

   public AdvanceEntityMatchTool(EquipmentSlot slot, ItemPredicate predicate) {
      this.slot = slot;
      this.predicate = predicate;
   }

   @Override
   public MapCodec<? extends net.minecraft.world.level.storage.loot.predicates.LootItemCondition> codec() {
      return CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ATTACKING_ENTITY);
   }

   public boolean test(LootContext context) {
      if (context.hasParameter(LootContextParams.ATTACKING_ENTITY)) {
         Entity entity = (Entity)context.getOptional(LootContextParams.ATTACKING_ENTITY);
         if (entity instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getItemBySlot(this.slot);
            return this.predicate.test(stack);
         }
      }

      return false;
   }

   public static Builder toolMatches(EquipmentSlot slot, ItemPredicate builder) {
      return () -> new AdvanceEntityMatchTool(slot, builder);
   }
}
