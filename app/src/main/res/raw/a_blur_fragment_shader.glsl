precision mediump float;
 
uniform sampler2D u_Texture;
 
varying vec2 v_TexCoord;
varying vec2 v_BlurTexCoords[14];
 
void main()
{
    gl_FragColor = vec4(0.0);
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 0])*0.0044299121055113265;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 1])*0.00895781211794;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 2])*0.0215963866053;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 3])*0.0443683338718;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 4])*0.0776744219933;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 5])*0.115876621105;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 6])*0.147308056121;
    gl_FragColor += texture2D(u_Texture, v_TexCoord         )*0.159576912161;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 7])*0.147308056121;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 8])*0.115876621105;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[ 9])*0.0776744219933;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[10])*0.0443683338718;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[11])*0.0215963866053;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[12])*0.00895781211794;
    gl_FragColor += texture2D(u_Texture, v_BlurTexCoords[13])*0.0044299121055113265;
}