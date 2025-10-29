package com.eokwingster.karkinoscore.data.server;

import com.eokwingster.karkinoscore.core.gametext.KCTextHolders;
import net.minecraft.data.PackOutput;

import java.util.List;

public class KCGameTextEntryGroupProvider extends GameTextEntryGroupProvider {
    public KCGameTextEntryGroupProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    @Override
    protected void generate() {
        this.addTextEntry(KCTextHolders.BLOOD_STELE, null, null, null, List.of(
                "respiratory_system", "digestive_system", "urinary_system"
        ));
    }
}
