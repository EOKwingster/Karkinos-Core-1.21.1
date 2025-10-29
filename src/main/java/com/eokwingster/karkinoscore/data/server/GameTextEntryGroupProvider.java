package com.eokwingster.karkinoscore.data.server;

import com.eokwingster.karkinoscore.core.gametext.GameTextBuiltinData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public abstract class GameTextEntryGroupProvider implements DataProvider {
    private final Map<String, List<GameTextBuiltinData.TextEntry>> data;
    private final PackOutput output;
    private final String modid;

    public GameTextEntryGroupProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
        this.data = GameTextBuiltinData.TEXT_ENTRY_GROUPS;
    }

    protected abstract void generate();

    protected void addTextEntry(DeferredHolder<Class<?>, ? extends Class<?>> textHolderHolder, String global, String advancement, String state, List<String> keyNames) {
        String fileName = textHolderHolder.getId().getPath();
        data.putIfAbsent(fileName, new ArrayList<>());
        List<GameTextBuiltinData.TextEntry> textEntries = data.get(fileName);
        GameTextBuiltinData.TextEntry textEntry = new GameTextBuiltinData.TextEntry(global, advancement, state);
        if (textEntries.contains(textEntry)) {
            throw new IllegalStateException("Duplicate text entry detected: " + textEntry);
        }
        textEntries.add(textEntry);
        textEntry.addKeys(keyNames.stream().map(keyName -> formLanguageKey(fileName, keyName)).toList());
    }

    private String formLanguageKey(String fileName, String keyName) {
        return String.join(".", "gametext", modid, fileName, keyName);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        this.generate();
        return !this.data.isEmpty() ?
                CompletableFuture.allOf(
                        this.data.entrySet().stream()
                                .map(entry -> this.save(
                                        cachedOutput,
                                        this.output.getOutputFolder(PackOutput.Target.DATA_PACK)
                                                .resolve(this.modid)
                                                .resolve("gametexts")
                                                .resolve(entry.getKey() + ".json"),
                                        entry.getValue()
                                ))
                                .toArray(CompletableFuture[]::new)
                ) :
                CompletableFuture.allOf();
    }

    private CompletableFuture<?> save(CachedOutput cache, Path target, List<GameTextBuiltinData.TextEntry> textEntries) {
        JsonArray jsonTextEntries = new JsonArray();
        UnaryOperator<String> nullToString = s -> s == null ? "null" : s;
        textEntries.forEach(textEntry -> {
            JsonObject jsonTextEntry = new JsonObject();
            jsonTextEntry.addProperty("require_global", nullToString.apply(textEntry.require_global()));
            jsonTextEntry.addProperty("require_advancement", nullToString.apply(textEntry.require_advancement()));
            jsonTextEntry.addProperty("require_state", nullToString.apply(textEntry.require_state()));
            JsonArray jsonKeys = new JsonArray();
            textEntry.keys().forEach(jsonKeys::add);
            jsonTextEntry.add("keys", jsonKeys);
            jsonTextEntries.add(jsonTextEntry);
        });
        return DataProvider.saveStable(cache, jsonTextEntries, target);
    }

    @Override
    public String getName() {
        return "Game Text";
    }
}
