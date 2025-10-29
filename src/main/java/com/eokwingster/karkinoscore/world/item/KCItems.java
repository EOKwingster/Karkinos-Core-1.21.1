package com.eokwingster.karkinoscore.world.item;

import com.eokwingster.karkinoscore.KarkinosCore;
import com.eokwingster.karkinoscore.world.level.block.KCBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class KCItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, KarkinosCore.MODID);
    private static final List<Supplier<Item>> TAB_ITEMS = new ArrayList<>();

    public static final Supplier<Item> BLOOD_STELE = registerTab("blood_stele", () -> new BlockItem(
            KCBlocks.BLOOD_STELE.get(),
            new Item.Properties().stacksTo(1)
    ));


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, KarkinosCore.MODID);

    public static final Supplier<CreativeModeTab> TAB_KARKINOS = CREATIVE_MODE_TABS.register(
            "karkinos",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(KarkinosCore.MODID_LANG_KEY))
                    .displayItems((itemDisplayParameters, output) ->
                            TAB_ITEMS.forEach(sup -> output.accept(sup.get()))
                    ).build()
    );

    protected static Supplier<Item> registerTab(String name, Supplier<? extends Item> sup) {
        Supplier<Item> sup1 = ITEMS.register(name, sup);
        TAB_ITEMS.add(sup1);
        return sup1;
    }
}
