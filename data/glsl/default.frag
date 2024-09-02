#version 330 core

in vec3 lFragmentPos;
in vec2 lTexCoord;

out vec4 colour;

uniform float uGreenTint;
uniform sampler2D ourTexture;

void main() {
//    colour = vec4(lFragmentPos * uGreenTint, 1.0f);
    colour = texture(ourTexture, lTexCoord) * (vec4(lFragmentPos * uGreenTint, 1.0f));

}