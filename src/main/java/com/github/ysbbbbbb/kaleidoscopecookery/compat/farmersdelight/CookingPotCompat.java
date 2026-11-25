package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.StockpotMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import java.util.ArrayList;
import vectorwing.farmersdelight.common.block.entity.inventory.InventoryRecipeInput;
import vectorwing.farmersdelight.common.block.entity.inventory.ItemInventory;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

public class CookingPotCompat {
   static void getTransformRecipeForJei(Level level, List<RecipeHolder<StockpotRecipe>> recipes) {
      if (level == null || !(level.recipeAccess() instanceof RecipeManager recipeManager)) {
         return;
      }
      // Minecraft 26.3 exposes RecipeManager#getRecipes() instead of getAllRecipesFor(type).
      recipeManager.getRecipes().stream()
         .filter(holder -> holder.value() instanceof CookingPotRecipe)
         .forEach(holder -> recipes.add(transformRecipe((RecipeHolder<CookingPotRecipe>) holder, level)));
   }

   static RecipeHolder<StockpotRecipe> transformRecipe(RecipeHolder<CookingPotRecipe> holder, Level level) {
      CookingPotRecipe cookingPotRecipe = (CookingPotRecipe)holder.value();
      StockpotRecipe recipe = new StockpotRecipe(
         cookingPotRecipe.getIngredients(),
         cookingPotRecipe.getResultItem(),
         cookingPotRecipe.getCookTime(),
         cookingPotRecipe.getOutputContainer()
      );
      return new RecipeHolder(holder.id(), recipe);
   }

   @SubscribeEvent
   static void afterStockpotRecipeMatch(StockpotMatchRecipeEvent.Post event) {
      if (!(event.getLevel().recipeAccess() instanceof RecipeManager recipeManager)) {
         return;
      }
      if (event.getRawOutput().equals(StockpotRecipeSerializer.EMPTY_ID)) {
         List<ItemStack> items = event.getInput().getInputs();
         // NeoForge 26.3 removed items.wrapper.RecipeWrapper, so feed the cooking pot
         // recipes through the mod's own RecipeInput implementation.
         // Feed the ingredients through Farmer's Delight's own recipe input type so its
         // own matches() semantics decide, not a re-implementation.
         InventoryRecipeInput wrapper = new InventoryRecipeInput(new ListBackedInventory(items));
         recipeManager.getRecipeFor(ModRecipeTypes.COOKING.get(), wrapper, event.getLevel())
            .ifPresent(recipe -> event.setOutput(transformRecipe(recipe, event.getLevel())));
      }
   }

   /** Minimal read/write {@link ItemInventory} view over a list of stacks. */
   private static final class ListBackedInventory implements ItemInventory {
      private final List<ItemStack> stacks;

      private ListBackedInventory(List<ItemStack> stacks) {
         this.stacks = new ArrayList<>(stacks);
      }

      @Override
      public int getSlots() {
         return this.stacks.size();
      }

      @Override
      public ItemStack getStackInSlot(int slot) {
         return this.stacks.get(slot);
      }

      @Override
      public void setStackInSlot(int slot, ItemStack stack) {
         this.stacks.set(slot, stack);
      }
   }
}
