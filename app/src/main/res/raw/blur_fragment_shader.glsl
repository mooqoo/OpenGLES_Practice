precision mediump float; 
      	 				
uniform sampler2D u_TextureUnit;      	 								
varying vec2 v_TextureCoordinates;      	   								
  
void main()                    		
{
    vec4 tex = texture2D(u_TextureUnit, v_TextureCoordinates);
    //gl_FragColor = tex + vec4(0.5, 0, 0, 1) * tex.a;
    gl_FragColor = vec4(tex.r + 0.5, tex.g, tex.b, tex.a);
}