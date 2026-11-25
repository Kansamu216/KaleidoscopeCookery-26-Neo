package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Codec / stream codec for {@link BambooTrayRecipe}. Mirrors the 26.x recipe serializer layout
 * used by {@code ChoppingBoardRecipeSerializer}: a {@link MapCodec} built from a record codec and a
 * composite {@link StreamCodec}. The {@code subtype} field is a {@code "drying"} / {@code "wetting"}
 * string that maps onto {@link BambooTrayRecipe.Subtype}.
 */
public final class BambooTrayRecipeSerializer {
   public static final int DEFAULT_DURATION = 60 * 20;

   private static final Codec<BambooTrayRecipe.Subtype> SUBTYPE_CODEC = Codec.STRING.xmap(
         BambooTrayRecipe.Subtype::fromSerializedName,
         BambooTrayRecipe.Subtype::getSerializedName);

   public static final MapCodec<BambooTrayRecipe> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(BambooTrayRecipe::getIngredient),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(BambooTrayRecipe::resultTemplate),
            SUBTYPE_CODEC.fieldOf("subtype").forGetter(BambooTrayRecipe::getSubtype),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(BambooTrayRecipe::getDuration)
         )
         .apply(instance, BambooTrayRecipe::new)
   );

   public static final StreamCodec<RegistryFriendlyByteBuf, BambooTrayRecipe> STREAM_CODEC = StreamCodec.composite(
      Ingredient.CONTENTS_STREAM_CODEC,
      BambooTrayRecipe::getIngredient,
      ItemStackTemplate.STREAM_CODEC,
      BambooTrayRecipe::resultTemplate,
      ByteBufCodecs.STRING_UTF8,
      BambooTrayRecipe::getSubtypeName,
      ByteBufCodecs.INT,
      BambooTrayRecipe::getDuration,
      BambooTrayRecipe::fromStream
   );

   private BambooTrayRecipeSerializer() {
   }

   public MapCodec<BambooTrayRecipe> codec() {
      return CODEC;
   }

   public StreamCodec<RegistryFriendlyByteBuf, BambooTrayRecipe> streamCodec() {
      return STREAM_CODEC;
   }
}
