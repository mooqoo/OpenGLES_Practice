precision mediump float;    // Set the default precision to medium. We don't need as high of a

uniform float u_Opacity;
uniform sampler2D u_TextureUnit;

varying vec2 v_TextureCoordinates;

void main() {
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
    gl_FragColor *= u_Opacity;
}