#version 330 core

in vec3 lFragmentPos;
in vec2 lTexCoord;

out vec4 colour;

uniform vec3 uTint;
uniform sampler2D ourTexture;

void main() {
    colour = texture(ourTexture, lTexCoord);
}