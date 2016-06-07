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
  private final int uMatrixLocation;
  private final int uTextureUnitLocation;
  private final int uBlurLocation;

  // Attribute locations
  private final int aPositionLocation;
  private final int aTextureCoordinatesLocation;

  // Constructor
  public BlurVShaderProgram(Context context) {
    super(context, R.raw.a_v_blur_vertex_shader, R.raw.a_blur_fragment_shader);

    // Retrieve uniform locations for the shader program.
    // uniform mat4 u_MVPMatrix;
    // uniform sampler2D u_Texture;
    // uniform float u_Blur;
    uMatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
    uTextureUnitLocation = glGetUniformLocation(program, "u_Texture");
    uBlurLocation = glGetUniformLocation(program, "u_Blur");

    // Retrieve attribute locations for the shader program.
    // attribute vec4 a_Position;
    // attribute vec2 a_TexCoord;
    aPositionLocation = glGetAttribLocation(program, "a_Position");
    aTextureCoordinatesLocation = glGetAttribLocation(program, "a_TexCoord");
  }

  public void setUniforms(float[] matrix, int textureId, float blur) {
    // Pass the matrix into the shader program.
    glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

    // Set the active texture unit to texture unit 0.
    glActiveTexture(GL_TEXTURE0);

    // Bind the texture to this unit.
    glBindTexture(GL_TEXTURE_2D, textureId);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    // Tell the texture uniform sampler to use this texture in the shader by
    // telling it to read from texture unit 0.
    glUniform1i(uTextureUnitLocation, 0);

    // Pass in the blur information
    glUniform1f(uBlurLocation, blur);
  }

  public int getPositionAttributeLocation() {
    return aPositionLocation;
  }

  public int getTextureCoordinatesAttributeLocation() {
    return aTextureCoordinatesLocation;
  }


}