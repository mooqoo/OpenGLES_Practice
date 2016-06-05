package com.example.wangalbert.bookpractice.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.wangalbert.bookpractice.R;
import com.example.wangalbert.bookpractice.utils.LoggerConfig;
import com.example.wangalbert.bookpractice.utils.ShaderHelper;
import com.example.wangalbert.bookpractice.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;


/**
 * android
 * <p/>
 * Created by wangalbert on 6/5/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
  private static final String TAG = "Renderer";

  private static final int POSITION_COMPONENT_COUNT = 2;
  private static final int BYTES_PER_FLOAT = 4;

  private final FloatBuffer vertexData;

  float[] tableVerticesWithTriangles = {
    // Triangle 1
    -0.5f, -0.5f,
    0.5f,  0.5f,
    -0.5f,  0.5f,

    // Triangle 2
    -0.5f, -0.5f,
    0.5f, -0.5f,
    0.5f,  0.5f,

    // Line 1
    -0.5f, 0f,
    0.5f, 0f,

    // Mallets
    0f, -0.25f,
    0f, 0.25f
  };

  private Context context;

  private int program;

  private static final String U_COLOR = "u_Color";
  private static final String A_POSITION = "a_Position";

  private int aPositionLocation;
  private int uColorLocation;


  // Constructor
  public AirHockeyRenderer(Context context) {
    this.context = context;

    // store vertextData into Buffer
    vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
      .order(ByteOrder.nativeOrder())
      .asFloatBuffer();
    vertexData.put(tableVerticesWithTriangles);
  }


  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    // read shader program from raw directory
    String vertexShaderSource =
      TextResourceReader.readTextFileFromResource(context, R.raw.table_vertex_shader);
    String fragmentShaderSource =
      TextResourceReader.readTextFileFromResource(context, R.raw.table_fragment_shader);

    // compile shader
    int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
    int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

    // link it to program
    program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

    if (LoggerConfig.ON) {
      ShaderHelper.validateProgram(program);
    }

    glUseProgram(program);

    //--------------- --------------
    uColorLocation = glGetUniformLocation(program, U_COLOR);
    aPositionLocation = glGetAttribLocation(program, A_POSITION);

    vertexData.position(0);
    glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
    glEnableVertexAttribArray(aPositionLocation);

  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    glViewport(0, 0, width, height);

  }

  @Override
  public void onDrawFrame(GL10 gl) {
    glClear(GL_COLOR_BUFFER_BIT);

    // draw the two triangle (rectangle)
    glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
    glDrawArrays(GL_TRIANGLES, 0, 6);

    // draw the line
    glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
    glDrawArrays(GL_LINES, 6, 2);

    // Draw the first mallet blue.
    glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
    glDrawArrays(GL_POINTS, 8, 1);

    // Draw the second mallet red.
    glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
    glDrawArrays(GL_POINTS, 9, 1);
  }
}
