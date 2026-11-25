package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CandiedSkewerItem extends BlockItem implements IHasContainer {
    private static final UUID KNOCKBACK_MODIFIER_UUID = UUID.fromString("d0190f53-13a1-4c8c-baa2-d3d0059911d4");

    private static final net.minecraft.world.item.component.ItemAttributeModifiers ATTRIBUTE_MODIFIERS =
            net.minecraft.world.item.component.ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_KNOCKBACK,
                            new AttributeModifier(
                                    net.minecraft.resources.Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "candied_skewer_knockback"),
                                    3,
                                    AttributeModifier.Operation.ADD_VALUE),
                            net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND)
                    .build();

    private final @Nullable MobEffect attackEffect;
    private final int attackEffectDuration;
    private final Supplier<Block> sideBlock;

    public CandiedSkewerItem(Supplier<Block> uprightBlock, Supplier<Block> sideBlock, FoodProperties foodProperties) {
        this(uprightBlock, sideBlock, foodProperties, null, 0);
    }

    public CandiedSkewerItem(Supplier<Block> uprightBlock, Supplier<Block> sideBlock, FoodProperties foodProperties,
                             @Nullable MobEffect attackEffect, int attackEffectDuration) {
        super(uprightBlock.get(), com.github.ysbbbbbb.kaleidoscopecookery.init.ModRegistrationProperties.itemProperties().food(foodProperties).component(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, ATTRIBUTE_MODIFIERS));
        this.sideBlock = sideBlock;
        this.attackEffect = attackEffect;
        this.attackEffectDuration = attackEffectDuration;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        Direction face = context.getClickedFace();

        if (face == Direction.UP) {
            Block target = this.getBlock();
            return target.defaultBlockState().setValue(FACING,
                    context.getHorizontalDirection().getOpposite());
        }

        if (face.getAxis().isHorizontal()) {
            Block target = this.sideBlock.get();
            return target.defaultBlockState().setValue(FACING, face);
        }

        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context,
                                net.minecraft.world.item.component.TooltipDisplay display,
                                java.util.function.Consumer<Component> tooltip, TooltipFlag flag) {
        net.minecraft.resources.Identifier key = BuiltInRegistries.ITEM.getKey(this);
        if (key == null) {
            return;
        }
        tooltip.accept(Component.translatable(key.toLanguageKey("tooltip"))
                .withStyle(ChatFormatting.DARK_GRAY)
                .withStyle(ChatFormatting.ITALIC));
    }


    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.attackEffect != null && !target.level().isClientSide()) {
            target.addEffect(new MobEffectInstance(net.minecraft.core.Holder.direct(this.attackEffect), this.attackEffectDuration), attacker);
        }
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        return this.returnContainerToEntity(itemStack, level, entity);
    }

    @Override
    public Item getContainerItem() {
        return Items.STICK;
    }
}
