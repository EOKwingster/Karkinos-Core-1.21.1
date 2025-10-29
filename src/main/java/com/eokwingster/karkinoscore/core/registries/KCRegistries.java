package com.eokwingster.karkinoscore.core.registries;

import com.eokwingster.karkinoscore.util.KCUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class KCRegistries {
    public static final ResourceKey<Registry<Class<?>>> TEXT_HOLDER_REGISTRY_KEY = ResourceKey.createRegistryKey(KCUtils.modLoc("text_holder"));

    public static final Registry<Class<?>> TEXT_HOLDER_REGISTRY = new RegistryBuilder<>(TEXT_HOLDER_REGISTRY_KEY).create();
}
