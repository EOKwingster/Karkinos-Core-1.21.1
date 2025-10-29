package com.eokwingster.karkinoscore.data.client;

import com.eokwingster.karkinoscore.KarkinosCore;
import com.eokwingster.karkinoscore.util.KCUtils;
import com.eokwingster.karkinoscore.world.level.block.KCBlocks;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.io.InputStreamReader;
import java.util.Map;

public class KCChineseLanguageProvider extends LanguageProvider {
    private static final String LOCALE = "zh_cn";
    private final String modid;

    public KCChineseLanguageProvider(PackOutput output, String modid) {
        super(output, modid, LOCALE);
        this.modid = modid;
    }

    @Override
    protected void addTranslations() {
        // Blocks
        add(KCBlocks.BLOOD_STELE.get(), "血肉石碑");

        // Items

        // Key mappings

        // Miscellaneous
        add(KarkinosCore.MODID_LANG_KEY, "卡基诺斯");
        addGameTextTranslations();
    }

    protected void addVillagerProfession(DeferredHolder<VillagerProfession, VillagerProfession> proHolder, String value) {
        add(EntityType.VILLAGER.getDescriptionId() + "." + proHolder.getId().toLanguageKey(), value);
    }

    protected void addGameTextTranslations() {
        try {
            InputStreamReader in = KCUtils.getResourceAsReader(String.join("", "/assets/", modid, "/lang_extra/gametext/", LOCALE, ".json"));
            Map<String, String> translations = new Gson().fromJson(in, new TypeToken<>() {});
            translations.forEach((key, trans) -> this.add(formGameTextLanguageKey(key), trans));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load extra translations", e);
        }
    }

    private String formGameTextLanguageKey(String path) {
        return String.join(".", "gametext", modid, path);
    }
}
