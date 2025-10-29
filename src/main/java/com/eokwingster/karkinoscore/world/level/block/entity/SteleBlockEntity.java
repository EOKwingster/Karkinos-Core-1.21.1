package com.eokwingster.karkinoscore.world.level.block.entity;

import com.eokwingster.karkinoscore.core.gametext.GameTextBuiltinData;
import com.eokwingster.karkinoscore.core.gametext.KCTextHolders;
import com.eokwingster.karkinoscore.util.KCUtils;
import com.eokwingster.karkinoscore.world.level.block.SteleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SteleBlockEntity extends BlockEntity {
    private String messageKey = "";
    private final SteleBlock.StelePart part;
    private final SteleBlock.SteleType type;

    public SteleBlockEntity(BlockPos pos, BlockState blockState) {
        super(KCBlockEntityTypes.STELE.get(), pos, blockState);
        part = this.getBlockState().getValue(SteleBlock.PART);
        type = this.getBlockState().getValue(SteleBlock.TYPE);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public String getEnglishMessage() {
        return KCUtils.getDefaultTranslationOf(getMessageKey());
    }

    public String getMessageKey() {
        if (part == SteleBlock.StelePart.TOP) {
            BlockEntity blockEntity = level.getBlockEntity(getBlockPos().below());
            if (blockEntity instanceof SteleBlockEntity bottomPartEntity && bottomPartEntity.getBlockState().getValue(SteleBlock.PART) == SteleBlock.StelePart.BOTTOM) {
                return bottomPartEntity.messageKey;
            }
        }
        return this.messageKey;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (part == SteleBlock.StelePart.BOTTOM) {
            tag.putString("key", messageKey);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (part == SteleBlock.StelePart.BOTTOM) {
            this.messageKey = tag.getString("key");
        }
    }

    private String getDefaultMessageKey(RandomSource random) {
        return GameTextBuiltinData.getTextEntry(KCTextHolders.BLOOD_STELE, null, null, null).getRandomKey(random);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (part == SteleBlock.StelePart.BOTTOM && !this.getLevel().isClientSide() && this.messageKey.isEmpty()) {
            this.messageKey = getDefaultMessageKey(this.getLevel().getRandom());
            this.setChanged();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (part == SteleBlock.StelePart.BOTTOM) {
            tag.putString("key", messageKey);
        }
        return tag;
    }
}
