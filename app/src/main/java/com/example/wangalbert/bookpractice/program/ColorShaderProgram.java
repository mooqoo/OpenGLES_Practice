package com.example.wangalbert.bookpractice.program;

import android.content.Context;

import com.example.wangalbert.bookpractice.R;
import static android.opengl.GLES20.*;

/**
 * android
 * <p>
 * Created by wangalbert on 6/6/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class ColorShaderProgram extends ShaderProgram {
  private final static String TAG = "ColorShaderProgram";

  // Uniform locations
  private final int uMatrixLocation;

  // Attribute locations
  private final int aPositionLocation;
  private final int aColorLocation;


  public ColorShaderProgram(Context context) {
    super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

    // Retrieve uniform locations for the shader program.
    uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

    // Retrieve attribute locations for the shader program.
    aPositionLocation = glGetAttribLocation(program, A_POSITION);
    aColorLocation = glGetAttribLocation(program, A_COLOR);
  }

  public void setUniforms(float[] matrix) {
    // Pass the matrix into the shader program.
    glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
  }

  public int getPositionAttributeLocation() {
    return aPositionLocation;
  }

  public int getColorAttributeLocation() {
    return aColorLocation;
  }

}
