package net.mine_diver.smoothbeta.mixin.client.multidraw.nop;

import net.minecraft.class_66;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_66.class)
public class ChunkRendererMixin {
    @Redirect(
            method = {
                    "method_298",
                    "method_296"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glNewList(II)V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glNewList(int list, int mode) {}

    @Redirect(
            method = "method_298",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;getOrCreate(DDDDDD)Lnet/minecraft/util/math/Box;"
            )
    )
    private Box smoothbeta_nop_Box_getOrCreate(double d, double e, double f, double g, double h, double i) {
        return null;
    }

    @Redirect(
            method = "method_298",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;method_2024(Lnet/minecraft/util/math/Box;)V"
            )
    )
    private void smoothbeta_nop_ItemRenderer_method_2024(Box box) {}

    @Redirect(
            method = {
                    "method_298",
                    "method_296"
            },
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

    @Redirect(
            method = "method_303",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    remap = false
            )
    )
    private void smoothbeta_nop_GL11_glCallList(int list) {}
}
