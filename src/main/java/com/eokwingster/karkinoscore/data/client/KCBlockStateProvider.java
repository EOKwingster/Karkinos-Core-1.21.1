package com.eokwingster.karkinoscore.data.client;

import com.eokwingster.karkinoscore.util.KCUtils;
import com.eokwingster.karkinoscore.world.level.block.KCBlocks;
import com.eokwingster.karkinoscore.world.level.block.SteleBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class KCBlockStateProvider extends BlockStateProvider {
    public KCBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.steleBlock(SteleBlock.SteleType.BLOOD);
    }

    private void steleBlock(SteleBlock.SteleType steleType) {
        Function<String, ModelFile> getTexturedModel = parentModel -> models()
                .withExistingParent(steleType + "_" + parentModel, KCUtils.modLoc("block/" + parentModel))
                .texture("0", "block/" + steleType + "_" + parentModel)
                .texture("particle", "block/" + steleType + "_" + parentModel);
        this.horizontalBlock(
                KCBlocks.BLOOD_STELE.get(),
                state -> switch (state.getValue(SteleBlock.PART)) {
                    case BOTTOM -> getTexturedModel.apply("stele_bottom");
                    case TOP -> getTexturedModel.apply("stele_top");
                    case SIDE_BOTTOM -> getTexturedModel.apply("stele_side_bottom");
                    case SIDE_TOP -> getTexturedModel.apply("stele_side_top");
                }
        );
    }

    protected void horizontalBlockWithItem(Block block, ModelFile model) {
        this.horizontalBlock(block, model);
        this.simpleBlockItem(block, model);
    }
}
