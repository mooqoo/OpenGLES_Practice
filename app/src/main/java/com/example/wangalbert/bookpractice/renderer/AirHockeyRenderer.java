package com.example.wangalbert.bookpractice.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.wangalbert.bookpractice.R;
import com.example.wangalbert.bookpractice.objects.Mallet;
import com.example.wangalbert.bookpractice.objects.Table;
import com.example.wangalbert.bookpractice.program.ColorShaderProgram;
import com.example.wangalbert.bookpractice.program.TextureShaderProgram;
import com.example.wangalbert.bookpractice.utils.MatrixHelper;
import com.example.wangalbert.bookpractice.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

/**
 * android
 * <p/>
 * Created by wangalbert on 6/5/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
  private static final String TAG = "Renderer";

  // matrix
  private final float[] projectionMatrix = new float[16];
  private final float[] modelMatrix = new float[16];

  // data object
  private Table table;
  private Mallet mallet;

  // shader program
  private TextureShaderProgram textureProgram;
  private ColorShaderProgram colorProgram;

  private int texture;
  private Context context;

  // Constructor
  public AirHockeyRenderer(Context context) {
    this.context = context;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    table = new Table();
    mallet = new Mallet();

    textureProgram = new TextureShaderProgram(context);
    colorProgram = new ColorShaderProgram(context);

    // load texture
    texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    Log.d(TAG, "textureProgram=" + textureProgram + ", colorProgram=" + colorProgram + ", texture=" + texture);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    // Set the OpenGL viewport to fill the entire surface.
    glViewport(0, 0, width, height);

    MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

    setIdentityM(modelMatrix, 0);
    translateM(modelMatrix, 0, 0f, 0f, -2.5f);
    rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

    final float[] temp = new float[16];
    multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
    System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // Clear the rendering surface.
    glClear(GL_COLOR_BUFFER_BIT);

    // Draw the table.
    textureProgram.useProgram();
    textureProgram.setUniforms(projectionMatrix, texture);
    table.bindData(textureProgram);
    table.draw();

    // Draw the mallets.
    colorProgram.useProgram();
    colorProgram.setUniforms(projectionMatrix);
    mallet.bindData(colorProgram);
    mallet.draw();
  }
}
