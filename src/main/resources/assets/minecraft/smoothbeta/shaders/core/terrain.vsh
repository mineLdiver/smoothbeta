#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;

out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;
out vec4 normal;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vertexDistance = fog_distance(ModelViewMat, pos, 0);
    texCoord0 = UV0;
    vertexColor = Color;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
