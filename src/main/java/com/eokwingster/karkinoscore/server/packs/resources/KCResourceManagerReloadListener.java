package com.eokwingster.karkinoscore.server.packs.resources;

import com.eokwingster.karkinoscore.core.gametext.GameTextBuiltinData;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class KCResourceManagerReloadListener implements ResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        GameTextBuiltinData.load(resourceManager);
    }
}
