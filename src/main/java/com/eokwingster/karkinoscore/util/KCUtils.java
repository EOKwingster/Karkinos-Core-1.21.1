package com.eokwingster.karkinoscore.util;

import com.eokwingster.karkinoscore.KarkinosCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.i18n.I18nManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class KCUtils {
    public static final Map<String, String> DEFAULT_TRANSLATIONS = Collections.unmodifiableMap(I18nManager.loadTranslations("en_us"));

    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(KarkinosCore.MODID, path);
    }

    public static String modKey(String path, String key) {
        return String.join(".", KarkinosCore.MODID, path, key);
    }

    public static VoxelShape rotateShape(VoxelShape shape, Rotation rotation) {
        return shape.toAabbs().stream().map(aabb -> {
            var x1 = aabb.minX;
            var y1 = aabb.minY;
            var z1 = aabb.minZ;
            var x2 = aabb.maxX;
            var y2 = aabb.maxY;
            var z2 = aabb.maxZ;
            return switch (rotation) {
                case Rotation.CLOCKWISE_90 -> Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2);
                case Rotation.CLOCKWISE_180 -> Shapes.box(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
                case Rotation.COUNTERCLOCKWISE_90 -> Shapes.box(z1, y1, 1 - x2, z2, y2, 1 - x1);
                default -> Shapes.box(x1, y1, z1, x2, y2, z2);
            };
            }).reduce(Shapes.empty(), Shapes::or);
    }

    public static String getDefaultTranslationOf(String key) {
        return DEFAULT_TRANSLATIONS.getOrDefault(key, key);
    }

    public static InputStreamReader getResourceAsReader(String path) throws FileNotFoundException {
        InputStream stream = KarkinosCore.class.getResourceAsStream(path);
        if (stream == null) throw new FileNotFoundException("Resource not found: " + path);
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }
}
