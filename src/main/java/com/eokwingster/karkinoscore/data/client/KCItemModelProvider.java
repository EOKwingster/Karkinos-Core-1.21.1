package com.eokwingster.karkinoscore.data.client;

import com.eokwingster.karkinoscore.world.item.KCItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class KCItemModelProvider extends ItemModelProvider {
    public KCItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(KCItems.BLOOD_STELE.get());
    }
}
