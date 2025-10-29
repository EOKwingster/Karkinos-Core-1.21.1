package com.eokwingster.karkinoscore.data.server;

import com.eokwingster.karkinoscore.world.level.block.KCBlocks;
import com.eokwingster.karkinoscore.world.level.block.SteleBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class KCBlockLootSubProvider extends BlockLootSubProvider {
    public KCBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return KCBlocks.BLOCKS.getEntries().stream().map(e -> (Block)e.value()).toList();
    }

    @Override
    protected void generate() {
        add(KCBlocks.BLOOD_STELE.get(), createSinglePropConditionTable(KCBlocks.BLOOD_STELE.get(), SteleBlock.PART, SteleBlock.StelePart.BOTTOM));
    }
}
