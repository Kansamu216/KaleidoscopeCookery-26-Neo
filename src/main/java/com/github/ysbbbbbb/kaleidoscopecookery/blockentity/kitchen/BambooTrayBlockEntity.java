package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IBambooTray;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Bamboo tray block entity: holds up to 4 item slots and, exposed to rain / water drip or to
 * open sky (drying), progressively converts each slot by a {@link BambooTrayRecipe}.
 *
 * <p>Ported from the 1.20.1 Forge implementation. Storage now goes through the 26.x
 * {@link ValueOutput} / {@link ValueInput} storage views; the item handler capability is not
 * registered here (it is wired through {@code CapabilitiesRegistry} by the lead), but the block
 * entity still exposes the vanilla {@link WorldlyContainer} inventory API.</p>
 */
public class BambooTrayBlockEntity extends BaseBlockEntity implements WorldlyContainer, IBambooTray {
   private static final int[] SLOTS = {0, 1, 2, 3};
   private static final String PROGRESS_TAG = "ProcessingProgress";
   private static final String COMPLETION_STATES_TAG = "CompletionStates";

   private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
   private final int[] processingProgress = new int[4];
   private final CompletionState[] completionStates = new CompletionState[4];

   public BambooTrayBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.BAMBOO_TRAY_BE.get(), pos, state);
      Arrays.fill(this.completionStates, CompletionState.NONE);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, BambooTrayBlockEntity tray) {
      if (level.isClientSide()) {
         return;
      }

      long offset = pos.getX() + pos.getY() + pos.getZ();
      if (Math.floorMod(level.getGameTime() + offset, 19) != 0) {
         return;
      }

      boolean wetting = level.isRainingAt(pos.above()) || hasWaterDripstone(level, pos);
      boolean drying = !level.isRaining() && hasDryingExposure(level, pos);
      if (!wetting && !drying) {
         return;
      }
      BambooTrayRecipe.Subtype subtype = wetting
            ? BambooTrayRecipe.Subtype.WETTING
            : BambooTrayRecipe.Subtype.DRYING;

      List<BambooTrayRecipe> recipes = getAllRecipes(level);
      boolean changed = false;
      for (int slot = 0; slot < tray.items.size(); slot++) {
         ItemStack input = tray.items.get(slot);
         CompletionState completionState = tray.completionStates[slot];
         if (input.isEmpty() || completionState.matches(subtype)) {
            continue;
         }

         BambooTrayRecipe recipe = recipes.stream()
               .filter(candidate -> candidate.getSubtype() == subtype)
               .filter(candidate -> candidate.getIngredient().test(input))
               .findFirst()
               .orElse(null);
         if (recipe == null) {
            if (tray.processingProgress[slot] != 0) {
               tray.processingProgress[slot] = 0;
               changed = true;
            }
            continue;
         }

         if (completionState.isCompleted()) {
            tray.processingProgress[slot] = 0;
            tray.completionStates[slot] = CompletionState.NONE;
         }
         tray.processingProgress[slot] += 19;
         changed = true;

         if (tray.processingProgress[slot] >= recipe.getDuration()) {
            ItemStack result = recipe.getResult().copy();
            int count = Math.min(result.getMaxStackSize(), result.getCount() * input.getCount());
            result.setCount(count);

            tray.items.set(slot, result);
            tray.processingProgress[slot] = recipe.getDuration();
            tray.completionStates[slot] = CompletionState.fromSubtype(subtype);
         }
      }

      if (changed) {
         tray.refresh();
      }
   }

   /**
    * 26.x recipe lookup: {@code getAllRecipesFor} was removed, so gather every recipe and keep the
    * bamboo tray ones. Server-only path (serverTick guards isClientSide), so a ServerLevel cast is safe.
    */
   private static List<BambooTrayRecipe> getAllRecipes(Level level) {
      if (!(level instanceof ServerLevel serverLevel)) {
         return List.of();
      }
      return serverLevel.getServer().getRecipeManager().getRecipes().stream()
            .map(RecipeHolder::value)
            .filter(recipe -> recipe instanceof BambooTrayRecipe)
            .map(recipe -> (BambooTrayRecipe) recipe)
            .toList();
   }

   private static boolean hasDryingExposure(Level level, BlockPos pos) {
      BlockPos above = pos.above();
      if (level.canSeeSky(above)) {
         return true;
      }
      for (int y = above.getY(); y <= level.getMaxY(); y++) {
         BlockState state = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
         if (state.isAir()
               || state.is(Blocks.GLASS)
               || state.is(ModBlocks.BAMBOO_TRAY.get())) {
            continue;
         }
         return false;
      }
      return true;
   }

   private static boolean hasWaterDripstone(Level level, BlockPos pos) {
      if (!(level instanceof ServerLevel serverLevel)) {
         return false;
      }
      BlockPos tipPos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(level, pos);
      return tipPos != null && PointedDripstoneBlock.getCauldronFillFluidType(serverLevel, tipPos) == Fluids.WATER;
   }

   @Override
   public boolean onPutItem(Level level, LivingEntity user, ItemStack held, int slot) {
      if (slot < 0 || slot >= this.items.size() || held.isEmpty()) {
         return false;
      }

      int moved = insertIntoSlot(slot, held);
      if (moved <= 0) {
         return false;
      }
      if (!(user instanceof Player player && player.getAbilities().instabuild)) {
         held.shrink(moved);
      }
      user.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      this.refresh();
      return true;
   }

   @Override
   public boolean onTakeOut(Level level, LivingEntity user, int slot, boolean takeAll) {
      if (slot < 0 || slot >= this.items.size()) {
         return false;
      }

      ItemStack stored = this.items.get(slot);
      if (stored.isEmpty()) {
         return false;
      }

      int amount = takeAll ? stored.getCount() : 1;
      ItemStack extracted = stored.split(amount);
      if (stored.isEmpty()) {
         this.items.set(slot, ItemStack.EMPTY);
         resetProcessing(slot);
      }
      ItemUtils.getItemToLivingEntity(user, extracted);
      user.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
      this.refresh();
      return true;
   }

   private int insertIntoSlot(int slot, ItemStack stack) {
      ItemStack stored = this.items.get(slot);
      if (stored.isEmpty()) {
         int moved = Math.min(stack.getCount(), stack.getMaxStackSize());
         ItemStack inserted = stack.copy();
         inserted.setCount(moved);
         this.items.set(slot, inserted);
         resetProcessing(slot);
         return moved;
      }
      if (this.completionStates[slot].isCompleted() || !ItemStack.isSameItemSameComponents(stored, stack)) {
         return 0;
      }
      int moved = Math.min(stack.getCount(), stored.getMaxStackSize() - stored.getCount());
      stored.grow(moved);
      return moved;
   }

   private void resetProcessing(int slot) {
      this.processingProgress[slot] = 0;
      this.completionStates[slot] = CompletionState.NONE;
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   protected void saveAdditional(ValueOutput tag) {
      super.saveAdditional(tag);
      ContainerHelper.saveAllItems(tag, this.items, true);
      tag.putIntArray(PROGRESS_TAG, this.processingProgress);

      // 26.x ValueOutput has no byte-array writer, so the completion states are stored as an int array.
      int[] completionValues = new int[this.completionStates.length];
      for (int i = 0; i < this.completionStates.length; i++) {
         completionValues[i] = this.completionStates[i].getSerializedValue();
      }
      tag.putIntArray(COMPLETION_STATES_TAG, completionValues);
   }

   @Override
   protected void loadAdditional(ValueInput tag) {
      super.loadAdditional(tag);
      this.items.clear();

      Arrays.fill(this.processingProgress, 0);
      Arrays.fill(this.completionStates, CompletionState.NONE);
      ContainerHelper.loadAllItems(tag, this.items);

      int[] progress = tag.getIntArray(PROGRESS_TAG).orElseGet(() -> new int[0]);
      System.arraycopy(progress, 0, this.processingProgress, 0, Math.min(progress.length, this.processingProgress.length));
      int[] completionValues = tag.getIntArray(COMPLETION_STATES_TAG).orElseGet(() -> new int[0]);
      for (int i = 0; i < Math.min(completionValues.length, this.completionStates.length); i++) {
         this.completionStates[i] = CompletionState.fromSerializedValue(completionValues[i]);
      }
   }

   @Override
   public int[] getSlotsForFace(Direction side) {
      return SLOTS;
   }

   @Override
   public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
      if (side == Direction.DOWN || this.completionStates[slot].isCompleted()) {
         return false;
      }
      ItemStack stored = this.items.get(slot);
      return stored.isEmpty() || ItemStack.isSameItemSameComponents(stored, stack) && stored.getCount() < stored.getMaxStackSize();
   }

   @Override
   public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
      return side == Direction.DOWN && this.completionStates[slot].isCompleted();
   }

   @Override
   public int getContainerSize() {
      return this.items.size();
   }

   @Override
   public boolean isEmpty() {
      return this.items.stream().allMatch(ItemStack::isEmpty);
   }

   @Override
   public ItemStack getItem(int slot) {
      return this.items.get(slot);
   }

   @Override
   public ItemStack removeItem(int slot, int amount) {
      ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
      if (!result.isEmpty()) {
         if (this.items.get(slot).isEmpty()) {
            resetProcessing(slot);
         }
         this.refresh();
      }
      return result;
   }

   @Override
   public ItemStack removeItemNoUpdate(int slot) {
      ItemStack result = ContainerHelper.takeItem(this.items, slot);
      if (!result.isEmpty()) {
         resetProcessing(slot);
      }
      return result;
   }

   @Override
   public void setItem(int slot, ItemStack stack) {
      ItemStack previous = this.items.get(slot);
      this.items.set(slot, stack);
      if (stack.isEmpty() || this.completionStates[slot].isCompleted() || !ItemStack.isSameItemSameComponents(previous, stack)) {
         resetProcessing(slot);
      }
      this.refresh();
   }

   @Override
   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   @Override
   public void clearContent() {
      this.items.clear();
      Arrays.fill(this.processingProgress, 0);
      Arrays.fill(this.completionStates, CompletionState.NONE);
      this.refresh();
   }

   private enum CompletionState {
      NONE(0),
      DRYING_COMPLETED(1),
      WETTING_COMPLETED(2);

      private final int serializedValue;

      CompletionState(int serializedValue) {
         this.serializedValue = serializedValue;
      }

      private boolean isCompleted() {
         return this != NONE;
      }

      private boolean matches(BambooTrayRecipe.Subtype subtype) {
         return (this == DRYING_COMPLETED && subtype == BambooTrayRecipe.Subtype.DRYING)
               || (this == WETTING_COMPLETED && subtype == BambooTrayRecipe.Subtype.WETTING);
      }

      private int getSerializedValue() {
         return this.serializedValue;
      }

      private static CompletionState fromSubtype(BambooTrayRecipe.Subtype subtype) {
         return subtype == BambooTrayRecipe.Subtype.DRYING ? DRYING_COMPLETED : WETTING_COMPLETED;
      }

      private static CompletionState fromSerializedValue(int value) {
         for (CompletionState state : values()) {
            if (state.serializedValue == value) {
               return state;
            }
         }
         return NONE;
      }
   }
}
