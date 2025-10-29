package com.eokwingster.karkinoscore.client.gui.screen;

import com.eokwingster.karkinoscore.util.KCUtils;
import com.eokwingster.karkinoscore.world.level.block.SteleBlock;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class SteleBlockScreen extends Screen {
    private final String messageKey;
    private final SteleBlock.SteleType type;

    public SteleBlockScreen(String messageKey, SteleBlock.SteleType type) {
        super(Component.translatable(KCUtils.modKey("screen", "stele")));
        this.messageKey = messageKey;
        this.type = type;
    }

    @Override
    protected void init() {
        super.init();
        MultiLineTextWidget textWidget = this.addRenderableWidget(new MultiLineTextWidget(Component.translatable(messageKey), this.minecraft.font));
        textWidget.setMaxWidth(this.width);
        textWidget.setCentered(true);
        textWidget.setColor(0x9f00ff);
        textWidget.setX((this.width - textWidget.getWidth()) / 2);
        textWidget.setY((this.height - textWidget.getHeight()) / 2);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            this.onClose();
            return true;
        }
        return clicked;
    }
}
