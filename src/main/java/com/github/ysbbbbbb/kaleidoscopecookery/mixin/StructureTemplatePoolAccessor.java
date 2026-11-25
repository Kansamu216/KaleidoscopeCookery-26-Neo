package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Gives the village integration write access to a loaded {@link StructureTemplatePool}.
 * <p>
 * Injecting the kitchen houses at runtime instead of overriding the vanilla
 * {@code data/minecraft/worldgen/template_pool/village/<biome>/houses.json} files means
 * other village-adding mods keep their pools intact: a JSON override replaces the whole
 * pool (last pack wins), while this only appends one weighted element.
 * <p>
 * Field names verified against the Minecraft 26.1.2 decompiled sources:
 * <pre>
 * private final List&lt;Pair&lt;StructurePoolElement, Integer&gt;&gt; rawTemplates;
 * private final ObjectArrayList&lt;StructurePoolElement&gt; templates;
 * private int maxSize = Integer.MIN_VALUE;
 * </pre>
 */
@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolAccessor {
   @Accessor("templates")
   ObjectArrayList<StructurePoolElement> kaleidoscope_cookery$getTemplates();

   @Accessor("rawTemplates")
   List<Pair<StructurePoolElement, Integer>> kaleidoscope_cookery$getRawTemplates();

   @Mutable
   @Accessor("rawTemplates")
   void kaleidoscope_cookery$setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates);

   /**
    * {@code maxSize} is a lazily computed cache ({@code Integer.MIN_VALUE} until the first
    * {@code getMaxSize} call), so it has to be invalidated after appending elements.
    */
   @Mutable
   @Accessor("maxSize")
   void kaleidoscope_cookery$setMaxSize(int maxSize);
}
