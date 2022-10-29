package com.smashingmods.alchemylib.api.blockentity.container.data;

import com.smashingmods.alchemylib.api.blockentity.container.Direction2D;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ProgressDisplayData extends AbstractDisplayData {

    private final AbstractProcessingBlockEntity blockEntity;
    private final Direction2D direction2D;

    public ProgressDisplayData(AbstractProcessingBlockEntity pBlockEntity, int pX, int pY, int pWidth, int pHeight, Direction2D pDirection2D) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
        this.direction2D = pDirection2D;
    }

    @Override
    public int getValue() {
        return blockEntity.getProgress();
    }

    @Override
    public int getMaxValue() {
        return blockEntity.getMaxProgress();
    }

    public Direction2D getDirection() {
        return direction2D;
    }

    @Override
    public Component toTextComponent() {
        return new TextComponent("Show Recipes");
    }
}
