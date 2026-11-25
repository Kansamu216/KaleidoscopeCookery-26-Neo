package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.particle;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * Registers the client-side particle providers.
 *
 * <p>{@code ModParticles} has always registered the two particle types, but the only
 * provider registration lived in the excluded 1.20.1 {@code client/init} sources, so
 * neither the cooking smoke nor the stockpot bubbles ever rendered on a 26.x client.
 * Both providers are now registered here against the 26.x
 * {@link RegisterParticleProvidersEvent} API.
 */
@EventBusSubscriber(modid = KaleidoscopeCookery.MOD_ID, value = Dist.CLIENT)
public final class RuntimeParticleRegistration {
   private RuntimeParticleRegistration() {
   }

   @SubscribeEvent
   public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(ModParticles.COOKING.get(), RuntimeCookingParticle.Provider::new);
      event.registerSpriteSet(ModParticles.STOCKPOT.get(), RuntimeStockpotParticle.Provider::new);
   }
}
