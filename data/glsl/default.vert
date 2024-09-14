#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec3 lFragmentPos;
out vec2 lTexCoord;

uniform mat4 uModel;
uniform mat4 uCamera;

void main() {
    lFragmentPos = aPos;
    lTexCoord = aTexCoord;
    gl_Position = uCamera * uModel * vec4(aPos, 1.0f);
}