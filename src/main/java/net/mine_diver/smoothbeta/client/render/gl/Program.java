package net.mine_diver.smoothbeta.client.render.gl;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.client.texture.TextureUtil;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class Program {

	private static final int MAX_LOG_LENGTH = 0x8000;
	private final Type shaderType;
	private final String name;
	private int shaderRef;

	protected Program(Type shaderType, int shaderRef, String name) {
		this.shaderType = shaderType;
		this.shaderRef = shaderRef;
		this.name = name;
	}

	public void attachTo(GlShader program) {
		GL20.glAttachShader(program.getProgramRef(), this.getShaderRef());
	}

	public void release() {
		if (this.shaderRef != -1) {
			GL20.glDeleteShader(this.shaderRef);
			this.shaderRef = -1;
			this.shaderType.getProgramCache().remove(this.name);
		}
	}

	public String getName() {
		return this.name;
	}

	public static Program createFromResource(Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
		int i = loadProgram(type, name, stream, domain, loader);
		Program program = new Program(type, i, name);
		type.getProgramCache().put(name, program);
		return program;
	}

	protected static int loadProgram(Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
		String string = TextureUtil.readResourceAsString(stream);
		if (string == null) throw new IOException("Could not load program " + type.getName());
		else {
			int i = GL20.glCreateShader(type.getGlType());
			GlStateManager.glShaderSource(i, loader.readSource(string));
			GL20.glCompileShader(i);
			if (GL20.glGetShaderi(i, GL20.GL_COMPILE_STATUS) == 0) {
				String string2 = StringUtils.trim(GL20.glGetShaderInfoLog(i, MAX_LOG_LENGTH));
				throw new IOException("Couldn't compile " + type.getName() + " program (" + domain + ", " + name + ") : " + string2);
			} else return i;
		}
	}

	protected int getShaderRef() {
		return this.shaderRef;
	}

	@Environment(EnvType.CLIENT)
	public enum Type {
		VERTEX("vertex", ".vsh", GL20.GL_VERTEX_SHADER),
		FRAGMENT("fragment", ".fsh", GL20.GL_FRAGMENT_SHADER);

		private final String name;
		private final String fileExtension;
		private final int glType;
		private final Map<String, Program> programCache = Maps.newHashMap();

		Type(String name, String extension, int glType) {
			this.name = name;
			this.fileExtension = extension;
			this.glType = glType;
		}

		public String getName() {
			return this.name;
		}

		public String getFileExtension() {
			return this.fileExtension;
		}

		int getGlType() {
			return this.glType;
		}

		/**
		 * Gets a map of loaded shaders.
		 */
		public Map<String, Program> getProgramCache() {
			return this.programCache;
		}
	}
}