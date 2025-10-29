package com.eokwingster.karkinoscore.core.gametext;

import com.eokwingster.karkinoscore.KarkinosCore;
import com.eokwingster.karkinoscore.core.registries.KCRegistries;
import com.eokwingster.karkinoscore.world.level.block.BloodSteleBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KCTextHolders {
    public static final DeferredRegister<Class<?>> TEXT_HOLDERS = DeferredRegister.create(KCRegistries.TEXT_HOLDER_REGISTRY, KarkinosCore.MODID);

    public static final DeferredHolder<Class<?>, Class<?>> BLOOD_STELE = TEXT_HOLDERS.register(
            "blood_stele", () -> BloodSteleBlock.class
    );
}
