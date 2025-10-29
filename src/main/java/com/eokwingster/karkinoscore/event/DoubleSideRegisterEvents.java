package com.eokwingster.karkinoscore.event;

import com.eokwingster.karkinoscore.KarkinosCore;
import com.eokwingster.karkinoscore.core.registries.KCRegistries;
import com.eokwingster.karkinoscore.server.packs.resources.KCResourceManagerReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = KarkinosCore.MODID)
public class DoubleSideRegisterEvents {
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(KCRegistries.TEXT_HOLDER_REGISTRY);
    }

    @SubscribeEvent
    public static void register(AddReloadListenerEvent event) {
        event.addListener(new KCResourceManagerReloadListener());
    }
}
