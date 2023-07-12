package net.mine_diver.smoothbeta.client.render.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class Uniform {
	public void set(float value1) {}

	public void set(float value1, float value2, float value3) {}

	public void setForDataType(float value1, float value2, float value3, float value4) {}

	public void setForDataType(int value1, int value2, int value3, int value4) {}

	public void set(int value) {}

	public void set(float[] values) {}

	public void set(Vec3f vector) {}
}