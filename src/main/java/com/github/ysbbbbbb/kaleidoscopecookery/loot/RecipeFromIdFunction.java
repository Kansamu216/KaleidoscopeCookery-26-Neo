package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * Attaches a {@link RecipeItem.RecipeRecord} to the produced stack.
 *
 * <p>The record is resolved at <em>runtime</em> from a recipe id, so the JSON never
 * contains an {@link ItemStack}. This avoids the early-load failure where
 * {@code villager_trade} data is parsed before item components are bound
 * ("Item ... does not have components yet").
 */
public class RecipeFromIdFunction extends LootItemConditionalFunction {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "recipe_from_id");
   public static final MapCodec<RecipeFromIdFunction> CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance)
         .and(Identifier.CODEC.fieldOf("recipe").forGetter(f -> f.recipeId))
         .and(Identifier.CODEC.fieldOf("recipe_type").forGetter(f -> f.recipeType))
         .apply(instance, RecipeFromIdFunction::new)
   );

   private final Identifier recipeId;
   private final Identifier recipeType;

   protected RecipeFromIdFunction(Optional<Holder<LootItemCondition>> condition, Identifier recipeId, Identifier recipeType) {
      super(condition);
      this.recipeId = recipeId;
      this.recipeType = recipeType;
   }

   @Override
   public MapCodec<? extends LootItemConditionalFunction> codec() {
      return CODEC;
   }

   @Override
   protected ItemStack run(ItemStack stack, LootContext context) {
      if (!(stack.getItem() instanceof RecipeItem)) {
         return stack;
      }
      RegistryAccess registries = context.getLevel().registryAccess();
      RecipeManager manager = context.getLevel().recipeAccess();
      RecipeHolder<?> holder = manager.byKey(net.minecraft.resources.ResourceKey.create(
         net.minecraft.core.registries.Registries.RECIPE, this.recipeId)).orElse(null);
      if (holder == null) {
         return stack;
      }
      List<ItemStack> inputs;
      ItemStack output;
      if (holder.value() instanceof PotRecipe pot) {
         inputs = pot.getIngredients().stream()
            .filter(i -> !i.isEmpty())
            .map(i -> new ItemStack(i.items().findFirst().orElseThrow().value()))
            .collect(ImmutableList.toImmutableList());
         output = pot.result();
      } else if (holder.value() instanceof StockpotRecipe stockpot) {
         inputs = stockpot.getIngredients().stream()
            .filter(i -> !i.isEmpty())
            .map(i -> new ItemStack(i.items().findFirst().orElseThrow().value()))
            .collect(ImmutableList.toImmutableList());
         output = stockpot.result();
      } else {
         return stack;
      }
      RecipeItem.setRecipe(stack, new RecipeItem.RecipeRecord(inputs, output, this.recipeType, false));
      return stack;
   }
}
