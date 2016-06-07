package com.example.wangalbert.bookpractice.objects;


import com.example.wangalbert.bookpractice.data.VertexArray;
import com.example.wangalbert.bookpractice.program.BlurShaderProgram;

import static com.example.wangalbert.bookpractice.Constants.BYTES_PER_FLOAT;
import static android.opengl.GLES20.*;


/**
 * android
 * <p>
 * Created by wangalbert on 6/6/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class BlurTable {
  private static final String TAG = "BlurTable";

  private static final int POSITION_COMPONENT_COUNT = 2;
  private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
  private static final int STRIDE = (POSITION_COMPONENT_COUNT
    + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

  private static final float[] VERTEX_DATA = {
    // Order of coordinates: X, Y, S, T
    // Triangle Fan
    0f,    0f,      0.5f, 0.5f,
    -1.0f, -1.0f,   0f, 0.9f,
    1.0f, -1.0f,   1f, 0.9f,
    1.0f,  1.0f,   1f, 0.1f,
    -1.0f,  1.0f,   0f, 0.1f,
    -1.0f, -1.0f,   0f, 0.9f
  };

  private final VertexArray vertexArray;

  public BlurTable() {
    vertexArray = new VertexArray(VERTEX_DATA);
  }

  public void bindData(BlurShaderProgram textureProgram) {
    vertexArray.setVertexAttribPointer(
      0,
      textureProgram.getPositionAttributeLocation(),
      POSITION_COMPONENT_COUNT,
      STRIDE
    );

    vertexArray.setVertexAttribPointer(
      POSITION_COMPONENT_COUNT,
      textureProgram.getTextureCoordinatesAttributeLocation(),
      TEXTURE_COORDINATES_COMPONENT_COUNT,
      STRIDE
    );
  }

  public void draw() {
    glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
  }

}