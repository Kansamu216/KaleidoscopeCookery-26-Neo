package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ResourceHandlerUtils;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

public enum TableComponentProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
   INSTANCE;

   public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> list) {
      return ClientViewGroup.map(list, ItemView::new, null);
   }

   @Nullable
   public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
      if (accessor.getTarget() instanceof TableBlockEntity table) {
         ItemStacksResourceHandler handler = table.getItems();
         List<ItemStack> list = Lists.newArrayList();

         for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = ResourceHandlerUtils.stackAt(handler, i);
            if (!stack.isEmpty()) {
               list.add(stack.copy());
            }
         }

         return List.of(new ViewGroup(list));
      } else {
         return null;
      }
   }

   public Identifier getUid() {
      return ModPlugin.TABLE;
   }
}
