package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei;

import java.util.Arrays;
import java.util.List;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ReiUtil {
   /** Non-persistent serializer used by the in-memory recipe displays. */
   @SuppressWarnings("unchecked")
   public static <D extends me.shedaniel.rei.api.common.display.Display> me.shedaniel.rei.api.common.display.DisplaySerializer<D> nonPersistentSerializer(Class<D> type) {
      return me.shedaniel.rei.api.common.display.DisplaySerializer.of(
         com.mojang.serialization.MapCodec.unit((D) null),
         net.minecraft.network.codec.StreamCodec.unit((D) null),
         false
      );
   }

   /** Delegates to {@link com.github.ysbbbbbb.kaleidoscopecookery.util.RecipeLookup}. */
   public static <T extends net.minecraft.world.item.crafting.Recipe<?>> java.util.List<net.minecraft.world.item.crafting.RecipeHolder<T>> recipesOf(
         net.minecraft.world.item.crafting.RecipeType<T> type) {
      net.minecraft.client.multiplayer.ClientLevel level = net.minecraft.client.Minecraft.getInstance().level;
      if (level == null || !(level.recipeAccess() instanceof net.minecraft.world.item.crafting.RecipeManager manager)) {
         return java.util.List.of();
      }
      return com.github.ysbbbbbb.kaleidoscopecookery.util.RecipeLookup.byType(manager, type);
   }

   public static EntryIngredient ofIngredient(Ingredient ingredient) {
      return EntryIngredient.of(ingredient.items().map(holder -> EntryStacks.of(holder.value())).toList());
   }

   /** Builds an entry ingredient from an item tag (26.3 no longer accepts TagKey in Ingredient.of). */
   public static EntryIngredient ofTag(net.minecraft.tags.TagKey<Item> tag) {
      return EntryIngredient.of(
         net.minecraft.core.registries.BuiltInRegistries.ITEM.getTagOrEmpty(tag).spliterator().getExactSizeIfKnown() >= 0
            ? java.util.stream.StreamSupport.stream(
                 net.minecraft.core.registries.BuiltInRegistries.ITEM.getTagOrEmpty(tag).spliterator(), false)
                 .map(h -> EntryStacks.of(h.value())).toList()
            : java.util.List.<me.shedaniel.rei.api.common.entry.EntryStack<?>>of());
   }

   public static EntryIngredient ofItem(Item item) {
      return EntryIngredient.of(EntryStacks.of(item));
   }

   public static EntryIngredient ofItemStack(ItemStack itemStack) {
      return EntryIngredient.of(EntryStacks.of(itemStack));
   }

   public static List<EntryIngredient> ofItems(Item... items) {
      return Arrays.stream(items).map(ReiUtil::ofItem).toList();
   }

   public static List<EntryIngredient> ofItemStacks(ItemStack... items) {
      return Arrays.stream(items).map(ReiUtil::ofItemStack).toList();
   }

   public static List<EntryIngredient> ofItems(List<Item> items) {
      return items.stream().map(ReiUtil::ofItem).toList();
   }

   public static List<EntryIngredient> ofIngredients(Ingredient... ingredients) {
      return Arrays.stream(ingredients).map(ReiUtil::ofIngredient).toList();
   }

   public static List<EntryIngredient> ofIngredients(List<Ingredient> ingredients) {
      return ingredients.stream().map(ReiUtil::ofIngredient).toList();
   }
}
