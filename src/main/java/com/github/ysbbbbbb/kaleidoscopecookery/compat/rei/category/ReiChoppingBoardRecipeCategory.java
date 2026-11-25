package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

public class ReiChoppingBoardRecipeCategory implements DisplayCategory<ReiChoppingBoardRecipeCategory.ChoppingBoardDisplay> {
   public static final CategoryIdentifier<ReiChoppingBoardRecipeCategory.ChoppingBoardDisplay> ID = CategoryIdentifier.of("kaleidoscope_cookery", "plugin/chopping_board");
   private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.chopping_board");
   private static final Identifier BG = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "textures/gui/jei/chopping_board.png");
   public static final int WIDTH = 176;
   public static final int HEIGHT = 78;

   public CategoryIdentifier<? extends ReiChoppingBoardRecipeCategory.ChoppingBoardDisplay> getCategoryIdentifier() {
      return ID;
   }

   public List<Widget> setupDisplay(ReiChoppingBoardRecipeCategory.ChoppingBoardDisplay display, Rectangle bounds) {
      List<Widget> widgets = new ArrayList<>();
      int startX = bounds.x;
      int startY = bounds.y;
      widgets.add(Widgets.createRecipeBase(bounds));
      widgets.add(Widgets.createTexturedWidget(BG, startX, startY, 0.0F, 0.0F, 176, 78));
      widgets.add(Widgets.createSlot(new Point(startX + 38, startY + 27)).entries((Collection)display.getInputEntries().get(0)).disableBackground().markInput());
      widgets.add(
         Widgets.createSlot(new Point(startX + 128, startY + 30)).entries((Collection)display.getOutputEntries().get(0)).disableBackground().markOutput()
      );
      return widgets;
   }

   public int getDisplayWidth(ChoppingBoardDisplay display) {
      return 176;
   }

   public int getDisplayHeight() {
      return 78;
   }

   public Component getTitle() {
      return TITLE;
   }

   public Renderer getIcon() {
      return EntryStacks.of((ItemLike)ModItems.CHOPPING_BOARD.get());
   }

   public static void registerCategories(CategoryRegistry registry) {
      registry.add(new ReiChoppingBoardRecipeCategory());
      registry.addWorkstations(
         ID, new EntryIngredient[]{ReiUtil.ofItem((Item)ModItems.CHOPPING_BOARD.get()), ReiUtil.ofTag(TagMod.KITCHEN_KNIFE)}
      );
   }

   public static void registerDisplays(DisplayRegistry registry) {
      ReiUtil.recipesOf(ModRecipes.CHOPPING_BOARD_RECIPE).forEach(r -> {
         List<EntryIngredient> input = ReiUtil.ofIngredients(java.util.List.of(((ChoppingBoardRecipe)r.value()).getIngredient()));
         List<EntryIngredient> output = ReiUtil.ofItemStacks(((ChoppingBoardRecipe)r.value()).getResult());
         registry.add(new ChoppingBoardDisplay(input, output, r.id().identifier()));
      });
   }

   public static class ChoppingBoardDisplay extends me.shedaniel.rei.api.common.display.basic.BasicDisplay {
      public ChoppingBoardDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Identifier location) {
         super(inputs, outputs, java.util.Optional.ofNullable(location));
      }

      @Override
      public CategoryIdentifier<?> getCategoryIdentifier() {
         return ReiChoppingBoardRecipeCategory.ID;
      }

      @Override
      public me.shedaniel.rei.api.common.display.DisplaySerializer<? extends me.shedaniel.rei.api.common.display.Display> getSerializer() {
         return ReiUtil.nonPersistentSerializer(getClass());
      }
   }
}
