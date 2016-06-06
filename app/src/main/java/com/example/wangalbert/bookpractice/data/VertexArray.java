package com.example.wangalbert.bookpractice.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.example.wangalbert.bookpractice.Constants.*;
import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;

/**
 * android
 * <p>
 * Created by wangalbert on 6/6/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class VertexArray {
  private static final String TAG = "VertexArray";

  private final FloatBuffer floatBuffer;

  public VertexArray(float[] vertexData) { floatBuffer = ByteBuffer
    .allocateDirect(vertexData.length * BYTES_PER_FLOAT) .order(ByteOrder.nativeOrder())
    .asFloatBuffer()
    .put(vertexData);
  }

  public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
    floatBuffer.position(dataOffset);
    glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
      false, stride, floatBuffer);
    glEnableVertexAttribArray(attributeLocation);
    floatBuffer.position(0);
  }

}
