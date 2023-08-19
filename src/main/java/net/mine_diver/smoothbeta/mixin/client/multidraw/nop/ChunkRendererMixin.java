package net.mine_diver.smoothbeta.mixin.client.multidraw.nop;

import net.minecraft.class_66;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_66.class)
public class ChunkRendererMixin {
    @Redirect(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glNewList(II)V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glNewList(int list, int mode) {}

    @Redirect(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEndList()V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glEndList() {}

    @Redirect(
            method = {
                    "method_306",
                    "method_296"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glTranslatef(float x, float y, float z) {}

    @Redirect(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glPushMatrix() {}

    @Redirect(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glScalef(float x, float y, float z) {}

    @Redirect(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glPopMatrix() {}
}
