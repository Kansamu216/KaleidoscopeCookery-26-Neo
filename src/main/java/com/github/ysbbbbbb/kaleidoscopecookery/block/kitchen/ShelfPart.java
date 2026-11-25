package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import net.minecraft.util.StringRepresentable;

/** Shelf connection part, replacing the removed vanilla Part enum. */
public enum ShelfPart implements StringRepresentable {
   SINGLE("single"),
   LEFT("left"),
   MIDDLE("middle"),
   RIGHT("right");

   private final String name;

   ShelfPart(String name) {
      this.name = name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }
}
