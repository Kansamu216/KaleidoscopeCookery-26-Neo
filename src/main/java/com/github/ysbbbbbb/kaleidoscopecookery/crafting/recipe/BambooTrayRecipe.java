package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Bamboo tray drying / wetting recipe. Ported from the 1.20.1 SingleItemRecipe based
 * implementation onto the 26.x {@link BaseRecipe} pipeline (ItemStackTemplate + codecs).
 */
public class BambooTrayRecipe implements BaseRecipe<SingleRecipeInput> {
   private final Ingredient ingredient;
   private final ItemStackTemplate result;
   private final Subtype subtype;
   private final int duration;

   public BambooTrayRecipe(Ingredient ingredient, ItemStackTemplate result, Subtype subtype, int duration) {
      this.ingredient = ingredient;
      this.result = result;
      this.subtype = subtype;
      this.duration = Math.max(duration, 1);
   }

   public boolean matches(SingleRecipeInput inv, Level level) {
      return this.ingredient.test(inv.getItem(0));
   }

   public boolean isSpecial() {
      return true;
   }

   public Ingredient getIngredient() {
      return this.ingredient;
   }

   public ItemStack getResult() {
      return this.result.create();
   }

   public ItemStackTemplate resultTemplate() {
      return this.result;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.result.create();
   }

   public Subtype getSubtype() {
      return this.subtype;
   }

   /** Serialized subtype name, used by the stream codec. */
   public String getSubtypeName() {
      return this.subtype.getSerializedName();
   }

   /** Stream-codec factory that rehydrates the subtype from its serialized name. */
   public static BambooTrayRecipe fromStream(Ingredient ingredient, ItemStackTemplate result, String subtypeName, int duration) {
      return new BambooTrayRecipe(ingredient, result, Subtype.fromSerializedName(subtypeName), duration);
   }

   public int getDuration() {
      return this.duration;
   }

   @SuppressWarnings("unchecked")
   public RecipeSerializer<BambooTrayRecipe> getSerializer() {
      return (RecipeSerializer<BambooTrayRecipe>) (RecipeSerializer<?>) ModRecipes.BAMBOO_TRAY_SERIALIZER.get();
   }

   public net.minecraft.world.item.crafting.RecipeType<BambooTrayRecipe> getType() {
      return ModRecipes.BAMBOO_TRAY_RECIPE;
   }

   /**
    * Drying vs. wetting. The serialized form is the lower-case name used by the
    * recipe JSON ({@code "drying"} / {@code "wetting"}).
    */
   public enum Subtype {
      WETTING("wetting"),
      DRYING("drying");

      private final String serializedName;

      Subtype(String serializedName) {
         this.serializedName = serializedName;
      }

      public String getSerializedName() {
         return this.serializedName;
      }

      public static Subtype fromSerializedName(String name) {
         for (Subtype subtype : values()) {
            if (subtype.serializedName.equalsIgnoreCase(name)) {
               return subtype;
            }
         }
         throw new IllegalArgumentException("Unknown bamboo tray recipe subtype: " + name);
      }
   }
}
