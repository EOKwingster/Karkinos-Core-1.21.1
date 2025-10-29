package com.eokwingster.karkinoscore.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class BloodSteleBlock extends SteleBlock {
    public static final MapCodec<SteleBlock> CODEC = simpleCodec(BloodSteleBlock::new);

    public BloodSteleBlock(Properties properties) {
        super(properties, SteleType.BLOOD);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }


}
