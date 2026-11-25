package com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public record ItemContainerTooltip(ResourceHandler<ItemResource> handler) implements TooltipComponent {
}
