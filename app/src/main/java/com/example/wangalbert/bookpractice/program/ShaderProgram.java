package com.example.wangalbert.bookpractice.program;

import android.content.Context;

import com.example.wangalbert.bookpractice.utils.ShaderHelper;
import com.example.wangalbert.bookpractice.utils.TextResourceReader;

import static android.opengl.GLES20.*;

/**
 * android
 * <p>
 * Created by wangalbert on 6/6/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class ShaderProgram {
  private final static String TAG = "ShaderProgram";

  // Uniform constants
  protected static final String U_MATRIX = "u_Matrix";
  protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

  // Attribute constants
  protected static final String A_POSITION = "a_Position";
  protected static final String A_COLOR = "a_Color";
  protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

  // Shader program
  protected final int program;

  // Constructor
  protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {

    String vertexShader = TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId);
    String fragmentShader = TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId);

    // Compile the shaders and link the program.
    program = ShaderHelper.buildProgram(vertexShader, fragmentShader);
  }

  public void useProgram() {
    // Set the current OpenGL shader program to this program.
    glUseProgram(program);
  }

}
