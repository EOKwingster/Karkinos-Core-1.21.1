package com.eokwingster.karkinoscore;

import com.eokwingster.karkinoscore.core.gametext.KCTextHolders;
import com.eokwingster.karkinoscore.world.item.KCItems;
import com.eokwingster.karkinoscore.world.level.block.KCBlocks;
import com.eokwingster.karkinoscore.world.level.block.entity.KCBlockEntityTypes;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(KarkinosCore.MODID)
public class KarkinosCore {
    public static final String MODID = "karkinoscore";
    public static final String MODID_LANG_KEY = MODID + ".modid";
    public static final Logger LOGGER = LogUtils.getLogger();

    public KarkinosCore(IEventBus modEventBus, ModContainer modContainer) {
        KCItems.ITEMS.register(modEventBus);
        KCItems.CREATIVE_MODE_TABS.register(modEventBus);
        KCBlocks.BLOCKS.register(modEventBus);
        KCBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        KCTextHolders.TEXT_HOLDERS.register(modEventBus);
    }
}
