package net.mine_diver.smoothbeta.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

@Environment(EnvType.CLIENT)
public class IncompatibleOSScreen extends Screen {
    private int ticksRan = 0;

    public IncompatibleOSScreen() {
    }

    public void tick() {
        ++this.ticksRan;
    }

    public void init() {
    }

    protected void buttonClicked(ButtonWidget button) {
    }

    protected void keyPressed(char character, int keyCode) {
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredTextWithShadow(this.textRenderer, "Operating system not supported!", this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawCenteredTextWithShadow(this.textRenderer, "MacOS is not supported by smoothbeta.", this.width / 2, this.height / 4 - 60 + 60 + 0, 10526880);
        super.render(mouseX, mouseY, delta);
    }
}
