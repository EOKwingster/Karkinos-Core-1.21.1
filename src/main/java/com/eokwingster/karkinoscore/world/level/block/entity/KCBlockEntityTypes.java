package com.eokwingster.karkinoscore.world.level.block.entity;

import com.eokwingster.karkinoscore.KarkinosCore;
import com.eokwingster.karkinoscore.world.level.block.KCBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class KCBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, KarkinosCore.MODID);

    public static final Supplier<BlockEntityType<SteleBlockEntity>> STELE = BLOCK_ENTITY_TYPES.register(
            "stele", () -> BlockEntityType.Builder.of(
                    SteleBlockEntity::new,
                    KCBlocks.BLOOD_STELE.get()
            ).build(null)
    );
}
