package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * First-person camera handling for the trash can seat.
 *
 * <p>When the player rides the trash can entity in first person, the pitch is pinned to 0
 * so the camera can only pan left and right. This mirrors the upstream 1.7.0
 * {@code CameraEvent}, which was part of the excluded 1.20.1 client sources.
 */
@EventBusSubscriber(modid = "kaleidoscope_cookery", value = Dist.CLIENT)
public final class RuntimeCameraEvents {
   private RuntimeCameraEvents() {
   }

   @SubscribeEvent
   public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
      Minecraft minecraft = Minecraft.getInstance();
      if (!minecraft.options.getCameraType().isFirstPerson()) {
         return;
      }
      LocalPlayer player = minecraft.player;
      if (player != null && player.getVehicle() instanceof SitEntity seat && seat.getSitType() == SitEntity.TRASH_CAN) {
         event.setPitch(0.0F);
      }
   }
}
