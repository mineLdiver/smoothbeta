package net.mine_diver.smoothbeta.client.render;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormats {
   public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.POSITION, 3);
   public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.UBYTE, VertexFormatElement.Type.COLOR, 4);
   public static final VertexFormatElement TEXTURE_0_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.UV, 2);
   public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.BYTE, VertexFormatElement.Type.NORMAL, 3);
   public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.BYTE, VertexFormatElement.Type.PADDING, 1);
   public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", POSITION_ELEMENT).put("UV0", TEXTURE_0_ELEMENT).put("Color", COLOR_ELEMENT).put("Normal", NORMAL_ELEMENT).put("Padding", PADDING_ELEMENT).build());
}
