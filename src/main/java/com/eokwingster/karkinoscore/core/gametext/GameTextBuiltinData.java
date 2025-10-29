package com.eokwingster.karkinoscore.core.gametext;

import com.eokwingster.karkinoscore.util.KCUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;

public class GameTextBuiltinData {
    public static final Map<String, List<TextEntry>> TEXT_ENTRY_GROUPS = new HashMap<>();

    public static void load(ResourceManager resourceManager) {
        Gson gson = new Gson();
        KCTextHolders.TEXT_HOLDERS.getEntries().forEach(textHolerHolder -> {
            String fileName = textHolerHolder.getId().getPath();
            ResourceLocation resLoc = KCUtils.modLoc("gametexts/" + fileName + ".json");
            resourceManager.getResource(resLoc).ifPresent(resource -> {
                try (BufferedReader reader = resource.openAsReader()) {
                    List<TextEntry> entries = gson.fromJson(reader, new TypeToken<List<TextEntry>>(){}.getType());
                    UnaryOperator<String> stringToNull = s -> Objects.equals(s, "null") ? null : s;
                    entries = entries.stream().map(entry -> new TextEntry(
                            stringToNull.apply(entry.require_global),
                            stringToNull.apply(entry.require_advancement),
                            stringToNull.apply(entry.require_state),
                            entry.keys
                    )).toList();
                    TEXT_ENTRY_GROUPS.put(fileName, entries);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load textKey file: " + resLoc, e);
                }
            });
        });
    }

    public static TextEntry getTextEntry(DeferredHolder<Class<?>, ? extends Class<?>> textHolderHolder, String require_global, String require_advancement, String require_state) {
        String fileName = textHolderHolder.getId().getPath();
        List<TextEntry> entries = TEXT_ENTRY_GROUPS.get(fileName);
        return entries.get(entries.indexOf(new TextEntry(require_global, require_advancement, require_state)));
    }

    public record TextEntry(String require_global, String require_advancement, String require_state, List<String> keys) {
        public TextEntry(String require_global, String require_advancement, String require_state) {
            this(require_global, require_advancement, require_state, new ArrayList<>());
        }

        public String getKey(int index) {
            return keys.get(index);
        }

        public String getRandomKey(RandomSource random) {
            return keys.get(random.nextInt(keys.size()));
        }

        public void addKeys(List<String> keys) {
            this.keys.addAll(keys);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TextEntry entry1) {
                return Objects.equals(entry1.require_global, this.require_global) &&
                        Objects.equals(entry1.require_advancement, this.require_advancement) &&
                        Objects.equals(entry1.require_state, this.require_state);
            }
            return false;
        }
    }
}
