package com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.mehvahdjukaar.every_compat.api.ECPlugin;
import net.mehvahdjukaar.every_compat.api.EveryCompatPlugin;
import net.mehvahdjukaar.every_compat.api.PluginEvent;

/**
 * Every Compat addon entry point.
 * <p>
 * Verified against the Every Compat 3.0.2 (NeoForge 26.1.2) jar: Moonlight's
 * {@code PlatHelper.loadPlugins(EveryCompatPlugin.class, "everycomp", ECPlugin.class)}
 * scans every mod file's {@code ModFileScanData} for classes annotated with
 * {@link ECPlugin}, instantiates them with their no-argument constructor and
 * calls {@link #register(PluginEvent)}. No extra entry point or service file is
 * needed.
 */
@ECPlugin
public final class EveryCompatCompat implements EveryCompatPlugin {
   @Override
   public void register(PluginEvent event) {
      event.addModule(KaleidoscopeCookery.MOD_ID, () -> KaleidoscopeCookeryModule.class);
   }
}
