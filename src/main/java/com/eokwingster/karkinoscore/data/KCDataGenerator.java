package com.eokwingster.karkinoscore.data;

import com.eokwingster.karkinoscore.KarkinosCore;
import com.eokwingster.karkinoscore.data.client.KCBlockStateProvider;
import com.eokwingster.karkinoscore.data.client.KCChineseLanguageProvider;
import com.eokwingster.karkinoscore.data.client.KCItemModelProvider;
import com.eokwingster.karkinoscore.data.server.KCBlockLootSubProvider;
import com.eokwingster.karkinoscore.data.server.KCGameTextEntryGroupProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = KarkinosCore.MODID)
public class KCDataGenerator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        boolean client = event.includeClient();
        boolean server = event.includeServer();

        // Client-side
        generator.addProvider(client, new KCBlockStateProvider(output, KarkinosCore.MODID, exFileHelper));
        generator.addProvider(client, new KCChineseLanguageProvider(output, KarkinosCore.MODID));
        generator.addProvider(client, new KCItemModelProvider(output, KarkinosCore.MODID, exFileHelper));

        // Server-side
        generator.addProvider(server, new LootTableProvider(
                output, Set.of(), List.of(
                        new LootTableProvider.SubProviderEntry(KCBlockLootSubProvider::new, LootContextParamSets.BLOCK)
                ), lookupProvider
        ));
        generator.addProvider(server, new KCGameTextEntryGroupProvider(output, KarkinosCore.MODID));
    }
}
