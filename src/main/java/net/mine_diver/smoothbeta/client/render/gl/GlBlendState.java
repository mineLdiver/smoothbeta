package net.mine_diver.smoothbeta.client.render.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public class GlBlendState {
	@Nullable
	private static GlBlendState activeBlendState;
	private final int srcRgb;
	private final int srcAlpha;
	private final int dstRgb;
	private final int dstAlpha;
	private final int func;
	private final boolean separateBlend;
	private final boolean blendDisabled;

	private GlBlendState(boolean separateBlend, boolean blendDisabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
		this.separateBlend = separateBlend;
		this.srcRgb = srcRgb;
		this.dstRgb = dstRgb;
		this.srcAlpha = srcAlpha;
		this.dstAlpha = dstAlpha;
		this.blendDisabled = blendDisabled;
		this.func = func;
	}

	public GlBlendState() {
		this(false, true, 1, 0, 1, 0, GL14.GL_FUNC_ADD);
	}

	public GlBlendState(int srcRgb, int dstRgb, int func) {
		this(false, false, srcRgb, dstRgb, srcRgb, dstRgb, func);
	}

	public GlBlendState(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
		this(true, false, srcRgb, dstRgb, srcAlpha, dstAlpha, func);
	}

	public void enable() {
		if (!this.equals(activeBlendState)) {
			if (activeBlendState == null || this.blendDisabled != activeBlendState.isBlendDisabled()) {
				activeBlendState = this;
				if (this.blendDisabled) {
					GlStateManager._disableBlend();
					return;
				}

				GlStateManager._enableBlend();
			}

			GL14.glBlendEquation(this.func);
			if (this.separateBlend)
				GlStateManager._blendFuncSeparate(this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha);
			else GlStateManager._blendFunc(this.srcRgb, this.dstRgb);

		}
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		else if (!(o instanceof GlBlendState glBlendState)) return false;
		else if (this.func != glBlendState.func) return false;
		else if (this.dstAlpha != glBlendState.dstAlpha) return false;
		else if (this.dstRgb != glBlendState.dstRgb) return false;
		else if (this.blendDisabled != glBlendState.blendDisabled) return false;
		else if (this.separateBlend != glBlendState.separateBlend) return false;
		else if (this.srcAlpha != glBlendState.srcAlpha) return false;
		else return this.srcRgb == glBlendState.srcRgb;
	}

	public int hashCode() {
		int i = this.srcRgb;
		i = 31 * i + this.srcAlpha;
		i = 31 * i + this.dstRgb;
		i = 31 * i + this.dstAlpha;
		i = 31 * i + this.func;
		i = 31 * i + (this.separateBlend ? 1 : 0);
		i = 31 * i + (this.blendDisabled ? 1 : 0);
		return i;
	}

	public boolean isBlendDisabled() {
		return this.blendDisabled;
	}

	public static int getFuncFromString(String name) {
		String string = name.trim().toLowerCase(Locale.ROOT);
		return switch (string) {
			case "add" -> GL14.GL_FUNC_ADD;
			case "subtract" -> GL14.GL_FUNC_SUBTRACT;
			case "reversesubtract", "reverse_subtract" -> GL14.GL_FUNC_REVERSE_SUBTRACT;
			case "min" -> GL14.GL_MIN;
			default -> "max".equals(string) ? GL14.GL_MAX : GL14.GL_FUNC_ADD;
		};
	}

	public static int getComponentFromString(String expression) {
		String string = expression.trim().toLowerCase(Locale.ROOT);
		string = string.replaceAll("_", "");
		string = string.replaceAll("one", "1");
		string = string.replaceAll("zero", "0");
		string = string.replaceAll("minus", "-");
		return switch (string) {
			case "0" -> 0;
			case "1" -> 1;
			case "srccolor" -> GL11.GL_SRC_COLOR;
			case "1-srccolor" -> GL11.GL_ONE_MINUS_SRC_COLOR;
			case "dstcolor" -> GL11.GL_DST_COLOR;
			case "1-dstcolor" -> GL11.GL_ONE_MINUS_DST_COLOR;
			case "srcalpha" -> GL11.GL_SRC_ALPHA;
			case "1-srcalpha" -> GL11.GL_ONE_MINUS_SRC_ALPHA;
			case "dstalpha" -> GL11.GL_DST_ALPHA;
			default -> "1-dstalpha".equals(string) ? GL11.GL_ONE_MINUS_DST_ALPHA : -1;
		};
	}
}