package com.eokwingster.karkinoscore.client.renderer.blockentity;

import com.eokwingster.karkinoscore.core.gametext.GameTextMapper;
import com.eokwingster.karkinoscore.util.KCUtils;
import com.eokwingster.karkinoscore.world.level.block.SteleBlock;
import com.eokwingster.karkinoscore.world.level.block.entity.SteleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SteleBlockEntityRenderer implements BlockEntityRenderer<SteleBlockEntity> {
    private static final ResourceLocation WITHERED_GOSPEL_STROKES = KCUtils.modLoc("textures/entity/blood_stele/withered_gospel_strokes.png");
    private static final ResourceLocation WITHERED_GOSPEL_SPECIAL_CHARACTERS = KCUtils.modLoc("textures/entity/blood_stele/withered_gospel_special_characters.png");
    private static final int STROKES_TEXTURE_SIZE = 64;
    private static final int SPECIAL_CHARACTERS_TEXTURE_SIZE = 128;
    private static final int TEXTURE_STROKE_WIDTH = 10;
    private static final int TEXTURE_STROKE_HEIGHT = 16;
    private static final int TEXTURE_SPECIAL_CHARACTER_WIDTH = 8;
    private static final int TEXTURE_SPECIAL_CHARACTER_HEIGHT = 16;
    private static final int TEXTURE_SPACE_WIDTH = 4;
    private static final int TEXTURE_SPACE_HEIGHT = 16;
    private static final float RENDER_SCALE = 0.35F;
    private static final int RENDER_WIDTH = 165;
    private static final int RENDER_HEIGHT_TOP_PART = 145;
    private static final int RENDER_HEIGHT_BOTTOM_PART = 165;
    private static final int TEXT_LIGHT_LEVEL = 15728880;

    @Override
    public void render(SteleBlockEntity steleBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        BlockState blockstate = steleBlockEntity.getBlockState();
        SteleBlock.StelePart part = blockstate.getValue(SteleBlock.PART);
        if (part == SteleBlock.StelePart.BOTTOM || part == SteleBlock.StelePart.TOP) {
            Direction facing = blockstate.getValue(SteleBlock.FACING);
            List<FormattedStroke> formattedText = formatText(GameTextMapper.mapText(steleBlockEntity.getEnglishMessage()));
            renderText(poseStack, multiBufferSource, facing, formattedText, part);
            renderText(poseStack, multiBufferSource, facing.getOpposite(), formattedText, part);
        }
    }

    private void renderText(PoseStack poseStack, MultiBufferSource multiBufferSource, Direction facing, List<FormattedStroke> formattedText, SteleBlock.StelePart part) {
        poseStack.pushPose();
        translateText(poseStack, facing, part);
        List<FormattedStroke> partText = getPartText(formattedText, part);
        for (FormattedStroke formattedStroke: partText) {
            if (formattedStroke.isSpecialCharacter) {
                renderStroke(poseStack, multiBufferSource.getBuffer(RenderType.text(WITHERED_GOSPEL_SPECIAL_CHARACTERS)), formattedStroke, SPECIAL_CHARACTERS_TEXTURE_SIZE);
            } else {
                renderStroke(poseStack, multiBufferSource.getBuffer(RenderType.text(WITHERED_GOSPEL_STROKES)), formattedStroke, STROKES_TEXTURE_SIZE);
            }
        }
        poseStack.popPose();
    }

    private void renderStroke(PoseStack poseStack, VertexConsumer consumer, FormattedStroke formattedStroke, int textureSize) {
        PoseStack.Pose pose = poseStack.last();
        int blockSize = 64;
        float x0 = formattedStroke.x / (float) blockSize;
        float y0 = formattedStroke.y / (float) blockSize;
        float x1 = (formattedStroke.x + formattedStroke.width) / (float) blockSize;
        float y1 = (formattedStroke.y - formattedStroke.height) / (float) blockSize;
        float u0 = formattedStroke.spriteX / (float) textureSize;
        float v0 = formattedStroke.spriteY / (float) textureSize;
        float u1 = (formattedStroke.spriteX + formattedStroke.width) / (float) textureSize;
        float v1 = (formattedStroke.spriteY + formattedStroke.height) / (float) textureSize;
        consumer.addVertex(pose, x0, -y0, 0F).setColor(-1).setUv(u0, v1).setLight(TEXT_LIGHT_LEVEL);
        consumer.addVertex(pose, x1, -y0, 0F).setColor(-1).setUv(u1, v1).setLight(TEXT_LIGHT_LEVEL);
        consumer.addVertex(pose, x1, -y1, 0F).setColor(-1).setUv(u1, v0).setLight(TEXT_LIGHT_LEVEL);
        consumer.addVertex(pose, x0, -y1, 0F).setColor(-1).setUv(u0, v0).setLight(TEXT_LIGHT_LEVEL);
    }

    private void translateText(PoseStack poseStack, Direction facing, SteleBlock.StelePart part) {
        float angle = switch (facing) {
            case NORTH -> 0F;
            case SOUTH -> 180F;
            case WEST -> 270F;
            case EAST -> 90F;
            default -> 0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        float xOffset = switch (facing) {
            case NORTH -> 0.0625F;
            case SOUTH -> -0.9375F;
            case WEST -> 0.0625F;
            case EAST -> -0.9375F;
            default -> 0;
        };
        float zOffset = switch (facing) {
            case NORTH -> 0.8126F;
            case SOUTH -> -0.1874F;
            case WEST -> -0.1874F;
            case EAST -> 0.8126F;
            default -> 0;
        };
        poseStack.translate(xOffset, part == SteleBlock.StelePart.TOP ? 0.74F : 0.94F, zOffset);
        poseStack.scale(RENDER_SCALE, RENDER_SCALE, RENDER_SCALE);
    }

    private List<FormattedStroke> getPartText(List<FormattedStroke> formattedText, SteleBlock.StelePart part) {
        List<FormattedStroke> partText = new ArrayList<>();
        int minHeight = part == SteleBlock.StelePart.TOP ? 0 : RENDER_HEIGHT_TOP_PART;
        int maxHeight = part == SteleBlock.StelePart.TOP ? RENDER_HEIGHT_TOP_PART : RENDER_HEIGHT_TOP_PART + RENDER_HEIGHT_BOTTOM_PART;
        for (FormattedStroke stroke : formattedText) {
            if (stroke.y < maxHeight && stroke.y >= minHeight) {
                partText.add(new FormattedStroke(stroke.x, stroke.y - minHeight, stroke.isSpecialCharacter, stroke.spriteX, stroke.spriteY, stroke.width, stroke.height));
            }
        }
        return partText;
    }

    private List<FormattedStroke> formatText(List<int[][]> text) {
        List<FormattedStroke> formattedText = new ArrayList<>();
        int x = 0;
        int y = 0;
        for (int[][] word : text) {
            for (int[] character : word) {
                int x1 = x;
                if (GameTextMapper.SPECIAL_CHARACTERS_CODE.contains(character[0])) {
                    if (GameTextMapper.isSpace(character[0])) {
                        x1 += TEXTURE_SPACE_WIDTH;
                    } else {
                        x1 += TEXTURE_SPECIAL_CHARACTER_WIDTH;
                    }
                } else {
                    x1 += TEXTURE_STROKE_WIDTH;
                }
                if (x1 > RENDER_WIDTH) {
                    x1 = x1 - x;
                    x = 0;
                    y += TEXTURE_STROKE_HEIGHT;
                    if (y >  RENDER_HEIGHT_TOP_PART + RENDER_HEIGHT_BOTTOM_PART) {
                        return formattedText;
                    }
                }
                for (int strokeCode : character) {
                    formattedText.add(formatStroke(strokeCode, x, y));
                }
                x = x1;
            }
        }
        return formattedText;
    }

    private FormattedStroke formatStroke(int strokeCode, int x, int y) {
        int lineCount;
        if (GameTextMapper.STROKE_CODE.contains(strokeCode)) {
            int index = strokeCode - GameTextMapper.STROKE_CODE.getFirst();
            lineCount = STROKES_TEXTURE_SIZE / TEXTURE_STROKE_WIDTH;
            return new FormattedStroke(x, y, false, index % lineCount * TEXTURE_STROKE_WIDTH, index / lineCount * TEXTURE_STROKE_HEIGHT, TEXTURE_STROKE_WIDTH, TEXTURE_STROKE_HEIGHT);
        } else if (GameTextMapper.SPECIAL_CHARACTERS_CODE.contains(strokeCode)) {
            int index = strokeCode - GameTextMapper.SPECIAL_CHARACTERS_CODE.getFirst();
            lineCount = SPECIAL_CHARACTERS_TEXTURE_SIZE / TEXTURE_SPECIAL_CHARACTER_WIDTH;
            int spriteX = index % lineCount * TEXTURE_SPECIAL_CHARACTER_WIDTH;
            int spriteY = index / lineCount * TEXTURE_SPECIAL_CHARACTER_HEIGHT;
            if (GameTextMapper.isSpace(strokeCode)) {
                return new FormattedStroke(x, y, true, spriteX, spriteY, TEXTURE_SPACE_WIDTH, TEXTURE_SPACE_HEIGHT);
            }
            return new FormattedStroke(x, y, true, spriteX, spriteY, TEXTURE_SPECIAL_CHARACTER_WIDTH, TEXTURE_SPECIAL_CHARACTER_HEIGHT);
        } else {
            throw new IllegalArgumentException("Invalid stroke code: " + strokeCode);
        }
    }

    private record FormattedStroke(int x, int y, boolean isSpecialCharacter, int spriteX, int spriteY, int width, int height) {}
}
