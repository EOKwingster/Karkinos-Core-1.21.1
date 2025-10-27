package com.eokwingster.karkinos_core;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(KarkinosCore.MODID)
public class KarkinosCore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "karkinos_core";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public KarkinosCore(IEventBus modEventBus, ModContainer modContainer) {

    }
}
