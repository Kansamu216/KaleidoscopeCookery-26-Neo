package com.github.ysbbbbbb.kaleidoscopecookery.item;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ResourceHandlerUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRegistrationProperties;
import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class TransmutationLunchBagItem extends Item {
   public static final Identifier HAS_ITEMS_PROPERTY = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "has_items");
   public static final int NO_ITEMS = 0;
   public static final int HAS_ITEMS = 1;
   private static final int MAX_SIZE = 16;
   private static final String TAG_ITEMS = "Items";

   public TransmutationLunchBagItem() {
      super(ModRegistrationProperties.itemProperties().stacksTo(1));
   }

   public static boolean hasItems(ItemStack bag) {
      return bag.has(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
   }

   public static ItemStacksResourceHandler getItems(ItemStack bag) {
      TransmutationLunchBagItem.ItemContainer container = (TransmutationLunchBagItem.ItemContainer)bag.get(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
      return container != null ? container.items() : new ItemStacksResourceHandler(16);
   }

   public static void setItems(ItemStack bag, ItemStacksResourceHandler items) {
      boolean allEmpty = true;

      for (int i = 0; i < items.size(); i++) {
         if (!ResourceHandlerUtils.stackAt(items, i).isEmpty()) {
            allEmpty = false;
            break;
         }
      }

      if (allEmpty) {
         bag.remove(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
      } else {
         bag.set(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS, TransmutationLunchBagItem.ItemContainer.of(items));
      }
   }

   public InteractionResult useOn(UseOnContext context) {
      if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FruitBasketBlockEntity fruitBasket)) {
         return super.useOn(context);
      } else {
         Player player = context.getPlayer();
         if (player == null) {
            return super.useOn(context);
         } else {
            ItemStack bag = context.getItemInHand();
            ItemStacksResourceHandler bagItems = getItems(bag);
            ItemStacksResourceHandler fruitBasketItems = fruitBasket.getItems();
            boolean basketEmpty = true;

            for (int i = 0; i < fruitBasketItems.size(); i++) {
               if (!ResourceHandlerUtils.stackAt(fruitBasketItems, i).isEmpty()) {
                  basketEmpty = false;
                  break;
               }
            }

            if (hasItems(bag) && basketEmpty) {
               for (int ix = 0; ix < bagItems.size(); ix++) {
                  ItemStack stack = ResourceHandlerUtils.stackAt(bagItems, ix);
                  if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
                     ItemStack remaining = ItemUtil.insertItemReturnRemaining(fruitBasketItems, stack, false, null);
                     ResourceHandlerUtils.extract(bagItems, ix, stack.getCount() - remaining.getCount(), false);
                  }
               }

               setItems(bag, bagItems);
               fruitBasket.refresh();
               this.playRemoveOneSound(player);
               return context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            } else {
               for (int ixx = 0; ixx < fruitBasketItems.size(); ixx++) {
                  ItemStack stack = ResourceHandlerUtils.stackAt(fruitBasketItems, ixx);
                  if (!stack.isEmpty() && canAdd(stack)) {
                     ItemStack remaining = ItemUtil.insertItemReturnRemaining(bagItems, stack, false, null);
                     ResourceHandlerUtils.extract(fruitBasketItems, ixx, stack.getCount() - remaining.getCount(), false);
                  }
               }

               setItems(bag, bagItems);
               fruitBasket.refresh();
               this.playDropContentsSound(player);
               return context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
         }
      }
   }

   public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
      if (entity instanceof Player player && player.isSecondaryUseActive() && dropContents(stack, player)) {
         this.playDropContentsSound(player);
         return true;
      } else {
         return super.onEntitySwing(stack, entity, hand);
      }
   }

   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      ItemStack itemInHand = player.getItemInHand(hand);
      if (hasItems(itemInHand)) {
         boolean hasFood = false;
         ItemStacksResourceHandler items = getItems(itemInHand);

         for (int i = 0; i < items.size(); i++) {
            ItemStack stackInSlot = ResourceHandlerUtils.stackAt(items, i);
            if (!stackInSlot.isEmpty()) {
               hasFood = true;
               break;
            }
         }

         if (hasFood) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
         }
      }

      return InteractionResult.FAIL;
   }

   public ItemStack finishUsingItem(ItemStack bag, Level level, LivingEntity entity) {
      if (!hasItems(bag)) {
         return bag;
      } else {
         ItemStack food = ItemStack.EMPTY;
         List<List<ModFoods.LegacyEffect>> effects = Lists.newArrayList();
         ItemStacksResourceHandler items = getItems(bag);

         for (int i = 0; i < items.size(); i++) {
            ItemStack stackInSlot = ResourceHandlerUtils.stackAt(items, i);
            if (!stackInSlot.isEmpty()) {
               FoodProperties foodProperties = stackInSlot.get(DataComponents.FOOD);
               if (foodProperties != null) {
                  if (!food.isEmpty()) {
                     List<ModFoods.LegacyEffect> foodEffects = ModFoods.effectsFor(foodProperties);
                     effects.add(foodEffects);
                  } else {
                     food = ResourceHandlerUtils.extract(items, i, 1, false);
                  }
               } else {
                  PotionContents potionContents = (PotionContents)stackInSlot.get(DataComponents.POTION_CONTENTS);
                  if (potionContents != null) {
                     if (!food.isEmpty()) {
                        List<ModFoods.LegacyEffect> potionEffects = Lists.newArrayList();
                        potionContents.forEachEffect(e -> potionEffects.add(new ModFoods.LegacyEffect(() -> e, 1.0F)), 1.0F);
                        effects.add(potionEffects);
                     } else {
                        food = ResourceHandlerUtils.extract(items, i, 1, false);
                     }
                  }
               }
            }
         }

         if (food.isEmpty()) {
            return bag;
         } else {
            ItemStack returnStack = food.finishUsingItem(level, entity);
            Item containerItem = ItemUtils.getContainerItem(food);
            if (!returnStack.isEmpty()) {
               if (!(entity instanceof Player player && player.getAbilities().instabuild)) {
                  ItemUtils.getItemToLivingEntity(entity, returnStack);
               }
            } else if (containerItem != Items.AIR) {
               ItemUtils.getItemToLivingEntity(entity, containerItem.getDefaultInstance());
            }

            boolean hasExtraEffects = false;
            Collections.shuffle(effects, new Random());
            int effectsToApply = Math.min(3, effects.size());

            for (int ix = 0; ix < effectsToApply; ix++) {
               for (ModFoods.LegacyEffect effect : effects.get(ix)) {
                  if (!level.isClientSide() && !(effect.probability() <= 0.0F) && !(level.getRandom().nextFloat() >= effect.probability())) {
                     entity.addEffect(new MobEffectInstance(effect.effect()));
                     hasExtraEffects = true;
                  }
               }
            }

            if (hasExtraEffects) {
               IntList foodSlots = new IntArrayList();

               for (int ix = 0; ix < items.size(); ix++) {
                  ItemStack stackInSlot = ResourceHandlerUtils.stackAt(items, ix);
                  if (!stackInSlot.isEmpty()) {
                     foodSlots.add(ix);
                  }
               }

               if (!foodSlots.isEmpty()) {
                  int randomIndex = level.getRandom().nextInt(foodSlots.size());
                  int slotToExtract = foodSlots.getInt(randomIndex);
                  ResourceHandlerUtils.extract(items, slotToExtract, 1, false);
               }
            }

            if (entity instanceof ServerPlayer player) {
               ((ModEventTrigger)ModTrigger.EVENT.get()).trigger(player, "use_transmutation_lunch_bag");
            }

            setItems(bag, items);
            return bag;
         }
      }
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return hasItems(stack) ? ItemUseAnimation.EAT : ItemUseAnimation.NONE;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 32;
   }

   public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction action, Player player) {
      if (bag.getCount() == 1 && action == ClickAction.SECONDARY) {
         ItemStack clickItem = slot.getItem();
         if (clickItem.isEmpty()) {
            this.playRemoveOneSound(player);
            removeOne(bag).ifPresent(stack -> add(bag, slot.safeInsert(stack)));
         } else if (clickItem.getItem().canFitInsideContainerItems() && canAdd(clickItem)) {
            int addCount = add(bag, clickItem, true);
            if (addCount > 0) {
               ItemStack takeout = slot.safeTake(clickItem.getCount(), addCount, player);
               if (!takeout.isEmpty()) {
                  add(bag, takeout);
               }

               this.playInsertSound(player);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
      if (bag.getCount() != 1) {
         return false;
      } else if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
         if (other.isEmpty()) {
            removeOne(bag).ifPresent(stack -> {
               this.playRemoveOneSound(player);
               access.set(stack);
            });
         } else {
            int added = add(bag, other);
            if (added > 0) {
               this.playInsertSound(player);
               other.shrink(added);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean canAdd(ItemStack food) {
      if (food.isEmpty()) {
         return false;
      } else {
         return !food.getItem().canFitInsideContainerItems() ? false : food.has(DataComponents.FOOD) || food.has(DataComponents.POTION_CONTENTS);
      }
   }

   private static Optional<ItemStack> removeOne(ItemStack bag) {
      if (!hasItems(bag)) {
         return Optional.empty();
      } else {
         ItemStacksResourceHandler items = getItems(bag);

         for (int i = 0; i < items.size(); i++) {
            ItemStack extractItem = ResourceHandlerUtils.extract(items, i, items.getCapacityAsInt(i, items.getResource(i)), false);
            if (!extractItem.isEmpty()) {
               setItems(bag, items);
               return Optional.of(extractItem);
            }
         }

         return Optional.empty();
      }
   }

   private static int add(ItemStack bag, ItemStack food) {
      return add(bag, food, false);
   }

   private static int add(ItemStack bag, ItemStack food, boolean simulate) {
      if (!food.isEmpty() && food.getItem().canFitInsideContainerItems() && canAdd(food)) {
         int totalCount = food.getCount();
         ItemStacksResourceHandler items = getItems(bag);
         ItemStack remaining = ItemUtil.insertItemReturnRemaining(items, food, simulate, null);
         int addCount = totalCount - (remaining.isEmpty() ? 0 : remaining.getCount());
         if (!simulate && addCount > 0) {
            setItems(bag, items);
         }

         return addCount;
      } else {
         return 0;
      }
   }

   private static boolean dropContents(ItemStack bag, Player player) {
      if (!hasItems(bag)) {
         return false;
      } else {
         boolean result = false;
         ItemStacksResourceHandler items = getItems(bag);

         for (int i = 0; i < items.size(); i++) {
            ItemStack stack = ResourceHandlerUtils.stackAt(items, i);
            if (!stack.isEmpty()) {
               player.getInventory().add(stack);
               result = true;
            }
         }

         if (result) {
            items = new ItemStacksResourceHandler(16);
            setItems(bag, items);
         }

         return result;
      }
   }

   private void playRemoveOneSound(Entity pEntity) {
      pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
   }

   private void playInsertSound(Entity pEntity) {
      pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
   }

   private void playDropContentsSound(Entity pEntity) {
      pEntity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
      if (!hasItems(stack)) {
         return Optional.empty();
      } else {
         ItemStacksResourceHandler items = getItems(stack);
         return Optional.of(new ItemContainerTooltip(items));
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.translatable("tooltip.kaleidoscope_cookery.transmutation_lunch_bag").withStyle(ChatFormatting.GRAY));
   }

   public record ItemContainer(ItemStacksResourceHandler items) {
      public static final Codec<TransmutationLunchBagItem.ItemContainer> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(list -> {
         ItemStacksResourceHandler handler = new ItemStacksResourceHandler(16);

         for (int i = 0; i < Math.min(list.size(), handler.size()); i++) {
            ResourceHandlerUtils.setStack(handler, i, (ItemStack)list.get(i));
         }

         return new TransmutationLunchBagItem.ItemContainer(handler);
      }, container -> {
         ItemStacksResourceHandler handler = container.items();
         List<ItemStack> output = Lists.newArrayList();

         for (int i = 0; i < handler.size(); i++) {
            output.add(ResourceHandlerUtils.stackAt(handler, i));
         }

         return output;
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, TransmutationLunchBagItem.ItemContainer> STREAM_CODEC =
         ItemStack.OPTIONAL_LIST_STREAM_CODEC.map(TransmutationLunchBagItem.ItemContainer::fromList, TransmutationLunchBagItem.ItemContainer::toList);

      private static TransmutationLunchBagItem.ItemContainer fromList(List<ItemStack> list) {
         ItemStacksResourceHandler handler = new ItemStacksResourceHandler(16);
         for (int i = 0; i < Math.min(list.size(), handler.size()); i++) {
            ResourceHandlerUtils.setStack(handler, i, list.get(i));
         }
         return new TransmutationLunchBagItem.ItemContainer(handler);
      }

      private List<ItemStack> toList() {
         List<ItemStack> output = Lists.newArrayList();
         for (int i = 0; i < this.items.size(); i++) {
            output.add(ResourceHandlerUtils.stackAt(this.items, i));
         }
         return output;
      }

      public static TransmutationLunchBagItem.ItemContainer of(ItemStacksResourceHandler items) {
         ItemStacksResourceHandler copy = new ItemStacksResourceHandler(16);

         for (int i = 0; i < Math.min(items.size(), copy.size()); i++) {
            ResourceHandlerUtils.setStack(copy, i, ResourceHandlerUtils.stackAt(items, i).copy());
         }

         return new TransmutationLunchBagItem.ItemContainer(copy);
      }
   }
}
