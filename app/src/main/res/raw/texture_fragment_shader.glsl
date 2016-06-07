precision mediump float; 
      	 				
uniform sampler2D u_TextureUnit;      	 								
varying vec2 v_TextureCoordinates;      	   								
  
void main()                    		
{                              	
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
    //vec4 tex = texture2D(u_TextureUnit, v_TextureCoordinates);
    //gl_FragColor = vec4(tex.r, tex.g + 0.5, tex.b, tex.a);
}