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
public class BlurVShaderProgram extends TextureShaderProgram {
  private final static String TAG = "BlurVShaderProgram";

  // Uniform locations
  private final int uBlurLocation;

  // Constructor
  public BlurVShaderProgram(Context context) {
    super(context, R.raw.a_v_blur_vertex_shader, R.raw.a_blur_fragment_shader);

    // Retrieve uniform locations for the shader program.
    uBlurLocation = glGetUniformLocation(program, "u_Blur");
  }

  public void setUniforms(float[] matrix, int textureId, float blur) {
    super.setUniforms(matrix, textureId);

    // Pass in the blur information
    glUniform1f(uBlurLocation, blur);
  }

}