package com.example.wangalbert.bookpractice.renderer;

import android.content.Context;
import android.opengl.GLES20;
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

  // FBO
  int fboId;
  int fboWidth = 720;
  int fboHeight = 1280;

  // viewport
  int viewportWidth;
  int viewportHeight;

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

    // ------------------- create fbo -----------------------------
    // generate fbo
    int[] frameBufferObjectIds = new int[1];
    glGenFramebuffers(1, frameBufferObjectIds, 0);
    fboId = frameBufferObjectIds[0];
    // bind fbo
    glBindFramebuffer(GL_FRAMEBUFFER, fboId);

    // generate texture
    int[] textureObjectIds = new int[1];
    glGenTextures(1, textureObjectIds, 0);
    // bind texture
    glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
    //Define texture parameters
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboWidth, fboHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    // Attach texture FBO color attachment
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureObjectIds[0], 0);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    viewportWidth = width;
    viewportHeight = height;

    setupViewport(width, height);
  }

  private void setupViewport(int width, int height) {
    // Set the OpenGL viewport to fill the entire surface.
    glViewport(0, 0, width, height);

    MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

    setIdentityM(modelMatrix, 0);
    translateM(modelMatrix, 0, 0f, 0f, -2.5f);
    rotateM(modelMatrix, 0, 0f, 1f, 0f, 0f);

    final float[] temp = new float[16];
    multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
    System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // Clear the rendering surface.
    //glClear(GL_COLOR_BUFFER_BIT);

    // --- render to fbo ---
    glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    setupViewport(fboWidth, fboWidth);

    glClear(GL_COLOR_BUFFER_BIT);

    //draw to texture
    textureProgram.useProgram();
    textureProgram.setUniforms(projectionMatrix, texture);
    table.bindData(textureProgram);
    table.draw();

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    // ---------------------

    setupViewport(viewportWidth, viewportHeight);
    glClear(GLES20.GL_COLOR_BUFFER_BIT);

    // TODO use the texture from FBO
    // Draw the table.
    textureProgram.useProgram();
    textureProgram.setUniforms(projectionMatrix, fboId);
    table.bindData(textureProgram);
    table.draw();

    // Draw the mallets.
    colorProgram.useProgram();
    colorProgram.setUniforms(projectionMatrix);
    mallet.bindData(colorProgram);
    mallet.draw();
  }
}
