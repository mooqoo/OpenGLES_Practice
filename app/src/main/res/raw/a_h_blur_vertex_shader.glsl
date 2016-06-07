uniform mat4 u_MVPMatrix;
uniform float u_Blur;

attribute vec4 a_Position;
attribute vec2 a_TexCoord;
 
varying vec2 v_TexCoord;
varying vec2 v_BlurTexCoords[14];

void main()
{
    gl_Position = u_MVPMatrix * a_Position;
    v_TexCoord = a_TexCoord;
    v_BlurTexCoords[ 0] = v_TexCoord + vec2(-0.028*u_Blur, 0.0);
    v_BlurTexCoords[ 1] = v_TexCoord + vec2(-0.024*u_Blur, 0.0);
    v_BlurTexCoords[ 2] = v_TexCoord + vec2(-0.020*u_Blur, 0.0);
    v_BlurTexCoords[ 3] = v_TexCoord + vec2(-0.016*u_Blur, 0.0);
    v_BlurTexCoords[ 4] = v_TexCoord + vec2(-0.012*u_Blur, 0.0);
    v_BlurTexCoords[ 5] = v_TexCoord + vec2(-0.008*u_Blur, 0.0);
    v_BlurTexCoords[ 6] = v_TexCoord + vec2(-0.004*u_Blur, 0.0);
    v_BlurTexCoords[ 7] = v_TexCoord + vec2( 0.004*u_Blur, 0.0);
    v_BlurTexCoords[ 8] = v_TexCoord + vec2( 0.008*u_Blur, 0.0);
    v_BlurTexCoords[ 9] = v_TexCoord + vec2( 0.012*u_Blur, 0.0);
    v_BlurTexCoords[10] = v_TexCoord + vec2( 0.016*u_Blur, 0.0);
    v_BlurTexCoords[11] = v_TexCoord + vec2( 0.020*u_Blur, 0.0);
    v_BlurTexCoords[12] = v_TexCoord + vec2( 0.024*u_Blur, 0.0);
    v_BlurTexCoords[13] = v_TexCoord + vec2( 0.028*u_Blur, 0.0);
}
