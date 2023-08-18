#version 150 compatibility

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform int FogMode;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    fragColor = fog(FogMode, color, vertexDistance, gl_Fog.density, gl_Fog.start, gl_Fog.end, gl_Fog.color.rgba);
}
