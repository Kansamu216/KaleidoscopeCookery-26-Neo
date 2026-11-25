package com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.ChairBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.CookStoolBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.mehvahdjukaar.every_compat.api.PaletteStrategies;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.TabAddMode;
import net.mehvahdjukaar.every_compat.modules.EveryCompatModule;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * Registers wooden variants (cook stool, chair, table) of this mod's decoration
 * blocks into Every Compat, ported from upstream 1.7.0 with the Every Compat 3.x
 * API: the block factory is a {@code BiFunction<WoodType, Properties, Block>}
 * (Every Compat owns the properties and their registry id), tabs are supplied
 * with {@code setTab(...)} instead of a tab key, and {@code ResourceLocation}
 * became {@code Identifier}.
 */
public class KaleidoscopeCookeryModule extends EveryCompatModule {
   private final SimpleEntrySet<WoodType, CookStoolBlock> cookStools;
   private final SimpleEntrySet<WoodType, ChairBlock> chairs;
   private final SimpleEntrySet<WoodType, TableBlock> tables;

   @SuppressWarnings("unchecked")
   public KaleidoscopeCookeryModule() {
      super(KaleidoscopeCookery.MOD_ID, "kalc", KaleidoscopeCookery.MOD_ID);

      this.cookStools = (SimpleEntrySet<WoodType, CookStoolBlock>) SimpleEntrySet.builder(
               WoodType.class, "", "cook_stool",
               () -> (CookStoolBlock) ModBlocks.COOK_STOOL_OAK.get(),
               () -> VanillaWoodTypes.OAK,
               (wood, properties) -> new CookStoolBlock(properties))
            .addCustomItem((wood, block, properties) -> new BlockItem(block, properties))
            .includeModelsBlock(Identifier.tryParse("kaleidoscope_cookery:block/cook_stool/oak"))
            .addTexture(Identifier.tryParse("kaleidoscope_cookery:block/cook_stool/oak"), PaletteStrategies.STRIPPED_LOG_SIDE_STANDARD)
            .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
            .addTag(TagMod.COOK_STOOL, Registries.BLOCK)
            .addTag(TagMod.COOKERY_MOD_ITEMS, Registries.ITEM)
            .setTab(this.getTab(this.modRes("cookery_main")))
            .setTabMode(TabAddMode.AFTER_SAME_TYPE)
            .defaultRecipe()
            .build();

      this.chairs = (SimpleEntrySet<WoodType, ChairBlock>) SimpleEntrySet.builder(
               WoodType.class, "", "chair",
               () -> (ChairBlock) ModBlocks.CHAIR_OAK.get(),
               () -> VanillaWoodTypes.OAK,
               (wood, properties) -> new ChairBlock(properties))
            .addCustomItem((wood, block, properties) -> new BlockItem(block, properties))
            .addTile(ModBlocks.CHAIR_BE)
            .includeModelsBlock(Identifier.tryParse("kaleidoscope_cookery:block/chair/oak"))
            .addTexture(Identifier.tryParse("kaleidoscope_cookery:block/chair/oak"), PaletteStrategies.STRIPPED_LOG_SIDE_STANDARD)
            .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
            .addTag(TagMod.CHAIR, Registries.BLOCK)
            .addTag(TagMod.CAT_LIE_ON_BLOCKS, Registries.BLOCK)
            .addTag(TagMod.COOKERY_MOD_ITEMS, Registries.ITEM)
            .setTab(this.getTab(this.modRes("cookery_main")))
            .setTabMode(TabAddMode.AFTER_SAME_TYPE)
            .defaultRecipe()
            .build();

      this.tables = (SimpleEntrySet<WoodType, TableBlock>) SimpleEntrySet.builder(
               WoodType.class, "", "table",
               () -> (TableBlock) ModBlocks.TABLE_OAK.get(),
               () -> VanillaWoodTypes.OAK,
               (wood, properties) -> new TableBlock(properties))
            .addCustomItem((wood, block, properties) -> new BlockItem(block, properties))
            .addTile(ModBlocks.TABLE_BE)
            .includeModelsBlock(Identifier.tryParse("kaleidoscope_cookery:block/table/oak"))
            .addTexture(Identifier.tryParse("kaleidoscope_cookery:block/table/oak"), PaletteStrategies.STRIPPED_LOG_SIDE_STANDARD)
            .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
            .addTag(TagMod.TABLE, Registries.BLOCK)
            .addTag(TagMod.COOKERY_MOD_ITEMS, Registries.ITEM)
            .setTab(this.getTab(this.modRes("cookery_main")))
            .setTabMode(TabAddMode.AFTER_SAME_TYPE)
            .defaultRecipe()
            .build();

      this.addEntry(this.cookStools);
      this.addEntry(this.chairs);
      this.addEntry(this.tables);
   }
}
