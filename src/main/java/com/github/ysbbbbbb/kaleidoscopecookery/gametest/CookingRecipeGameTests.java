package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * Confirms that recipe types migrated to deferred {@code ItemStackTemplate}
 * results decode from their shipped JSON into a non-empty, correctly resolved
 * ItemStack. This exercises the {@code create()} resolution path introduced by
 * the item-components migration, without depending on block interaction.
 */
public final class CookingRecipeGameTests {
   private CookingRecipeGameTests() {
   }

   public static void potRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "pot/beef_to_cooked_beef_1", Items.COOKED_BEEF, 1);
   }

   public static void stockpotRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "stockpot/beef_noodle", ModItem.BEEF_NOODLE.item(), 1);
   }

   public static void millstoneRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "millstone/green_dye_from_cactus", net.minecraft.world.item.Items.DYE.pick(net.minecraft.world.item.DyeColor.GREEN), 2);
   }

   public static void choppingBoardRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "chopping_board/bone_from_cod", Items.BONE, 2);
   }

   /**
    * Verifies the millstone automation capability accepts only items that match
    * a millstone recipe, and rejects unrelated items.
    */
   public static void millstoneInputCapabilityValidatesRecipes(GameTestHelper helper) {
      ServerLevel level = helper.getLevel();
      var be = new com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity(
         helper.absolutePos(new net.minecraft.core.BlockPos(0, 1, 0)),
         com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks.MILLSTONE.get().defaultBlockState()
      );
      be.setLevel(level);
      if (!be.isInputValid(Items.CACTUS)) {
         helper.fail("Millstone should accept cactus (has a millstone recipe)");
         return;
      }
      if (be.isInputValid(Items.DIRT)) {
         helper.fail("Millstone should reject dirt (no millstone recipe)");
         return;
      }
      helper.succeed();
   }


   /**
    * Counts the mod's recipes and logs the total so recipe loading can be
    * verified from the test log. Also fails if the mod loaded no recipes.
    */
   public static void recipeCountIsReported(GameTestHelper helper) {
      ServerLevel level = helper.getLevel();
      RecipeManager manager = level.recipeAccess();
      long total = manager.getRecipes().size();
      long modRecipes = manager.getRecipes().stream()
         .filter(holder -> holder.id().identifier().getNamespace().equals("kaleidoscope_cookery"))
         .count();
      org.slf4j.LoggerFactory.getLogger("kaleidoscope_cookery-gametest")
         .info("Recipe counts: total={}, kaleidoscope_cookery={}", total, modRecipes);
      if (modRecipes <= 0) {
         helper.fail("No kaleidoscope_cookery recipes were loaded (total=" + total + ")");
         return;
      }
      helper.succeed();
   }

   private static void assertResolves(GameTestHelper helper, String recipePath, Item expectedItem, int expectedCount) {
      ServerLevel level = helper.getLevel();
      RecipeManager manager = level.recipeAccess();
      ResourceKey<Recipe<?>> key = ResourceKey.create(
         Registries.RECIPE, Identifier.fromNamespaceAndPath("kaleidoscope_cookery", recipePath)
      );
      Optional<RecipeHolder<?>> holder = manager.byKey(key);
      if (holder.isEmpty()) {
         helper.fail("Migrated recipe was not loaded: " + recipePath);
         return;
      }

      ItemStack resolved = resolveResult(holder.get());
      if (resolved.isEmpty()) {
         helper.fail("Migrated recipe resolved to an empty stack: " + recipePath);
         return;
      }
      if (!resolved.is(expectedItem)) {
         helper.fail("Result item mismatch for " + recipePath + ": expected " + expectedItem + ", got " + resolved.getItem());
         return;
      }
      if (resolved.getCount() != expectedCount) {
         helper.fail("Result count mismatch for " + recipePath + ": expected " + expectedCount + ", got " + resolved.getCount());
         return;
      }
      helper.succeed();
   }

   /** Calls the type-specific result accessor so the deferred template is created. */
   private static ItemStack resolveResult(RecipeHolder<?> holder) {
      Recipe<?> recipe = holder.value();
      if (recipe instanceof PotRecipe pot) {
         return pot.result();
      }
      if (recipe instanceof StockpotRecipe stockpot) {
         return stockpot.result();
      }
      if (recipe instanceof MillstoneRecipe millstone) {
         return millstone.results().getFirst().stack();
      }
      if (recipe instanceof ChoppingBoardRecipe board) {
         return board.getResult();
      }
      return ItemStack.EMPTY;
   }

   /** Lazily resolves a mod item so the test does not run during class init. */
   private enum ModItem {
      BEEF_NOODLE("beef_noodle");

      private final String path;

      ModItem(String path) {
         this.path = path;
      }

      Item item() {
         return net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(
            Identifier.fromNamespaceAndPath("kaleidoscope_cookery", this.path)
         );
      }
   }
}
