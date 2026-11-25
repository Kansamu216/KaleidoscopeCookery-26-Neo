package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.StockpotMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

/**
 * Bridges Farmer's Delight cooking pot recipes into the stockpot.
 * <p>
 * On Minecraft 26.2 the Farmer's Delight 26.2 port still declares
 * {@code CookingPotRecipe implements Recipe<RecipeWrapper>}, so the NeoForge
 * {@link RecipeWrapper} (deprecated but present) is the correct input type.
 * The 26.3 port changed that to its own {@code InventoryRecipeInput}, which is
 * why the 26.3 branch of this file differs.
 */
public class CookingPotCompat {
   static void getTransformRecipeForJei(Level level, List<RecipeHolder<StockpotRecipe>> recipes) {
      if (level == null || !(level.recipeAccess() instanceof RecipeManager recipeManager)) {
         return;
      }
      // Minecraft 26.2 exposes RecipeManager#getRecipes() instead of getAllRecipesFor(type).
      recipeManager.getRecipes().stream()
         .filter(holder -> holder.value() instanceof CookingPotRecipe)
         .forEach(holder -> recipes.add(transformRecipe((RecipeHolder<CookingPotRecipe>) holder, level)));
   }

   static RecipeHolder<StockpotRecipe> transformRecipe(RecipeHolder<CookingPotRecipe> holder, Level level) {
      CookingPotRecipe cookingPotRecipe = holder.value();
      StockpotRecipe recipe = new StockpotRecipe(
         cookingPotRecipe.getIngredients(),
         cookingPotRecipe.getResultItem(),
         cookingPotRecipe.getCookTime(),
         cookingPotRecipe.getOutputContainer()
      );
      return new RecipeHolder<>(holder.id(), recipe);
   }

   @SubscribeEvent
   static void afterStockpotRecipeMatch(StockpotMatchRecipeEvent.Post event) {
      if (!(event.getLevel().recipeAccess() instanceof RecipeManager recipeManager)) {
         return;
      }
      if (event.getRawOutput().equals(StockpotRecipeSerializer.EMPTY_ID)) {
         List<ItemStack> items = event.getInput().getInputs();
         ItemStacksResourceHandler stacks = new ItemStacksResourceHandler(NonNullList.copyOf(items));
         RecipeWrapper wrapper = new RecipeWrapper(IItemHandler.of(stacks));
         recipeManager.getRecipeFor(ModRecipeTypes.COOKING.get(), wrapper, event.getLevel())
            .ifPresent(recipe -> event.setOutput(transformRecipe(recipe, event.getLevel())));
      }
   }
}
