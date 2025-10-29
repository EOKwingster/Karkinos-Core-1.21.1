package com.eokwingster.karkinoscore.event;

import com.eokwingster.karkinoscore.KarkinosCore;
import com.eokwingster.karkinoscore.client.renderer.blockentity.SteleBlockEntityRenderer;
import com.eokwingster.karkinoscore.world.level.block.entity.KCBlockEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = KarkinosCore.MODID, value = Dist.CLIENT)
public class ClientRegisterEvents {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                KCBlockEntityTypes.STELE.get(),
                context -> new SteleBlockEntityRenderer()
        );
    }
}
