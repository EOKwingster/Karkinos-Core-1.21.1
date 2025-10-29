package com.eokwingster.karkinoscore.world.level.block;

import com.eokwingster.karkinoscore.KarkinosCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class KCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, KarkinosCore.MODID);

    public static final Supplier<Block> BLOOD_STELE = BLOCKS.register("blood_stele", () -> new BloodSteleBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(3,6)
                    .sound(SoundType.WET_SPONGE)
                    .pushReaction(PushReaction.BLOCK)
                    .lightLevel(state -> 7)
    ));
}
