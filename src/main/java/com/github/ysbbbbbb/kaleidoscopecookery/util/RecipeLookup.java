package com.github.ysbbbbbb.kaleidoscopecookery.util;

import java.util.List;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Helper for the Minecraft 26 recipe API, where {@code RecipeManager#getAllRecipesFor(type)}
 * was replaced by {@code getRecipes()} returning {@code Collection<RecipeHolder<?>>}: every
 * caller would otherwise need its own unchecked narrowing cast.
 */
public final class RecipeLookup {
   private RecipeLookup() {
   }

   @SuppressWarnings("unchecked")
   public static <T extends Recipe<?>> List<RecipeHolder<T>> byType(RecipeManager manager, RecipeType<T> type) {
      if (manager == null) {
         return List.of();
      }
      List<?> matching = manager.getRecipes().stream().filter(holder -> holder.value().getType() == type).toList();
      return (List<RecipeHolder<T>>) matching;
   }
}
