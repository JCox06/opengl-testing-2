#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec3 lFragmentPos;
out vec2 lTexCoord;

void main() {
    lFragmentPos = aPos;
    lTexCoord = aTexCoord;
    gl_Position = vec4(aPos, 1.0f);
}