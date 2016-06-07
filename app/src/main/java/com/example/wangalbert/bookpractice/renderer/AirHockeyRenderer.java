package com.example.wangalbert.bookpractice.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

import com.example.wangalbert.bookpractice.R;
import com.example.wangalbert.bookpractice.objects.BlurTable;
import com.example.wangalbert.bookpractice.objects.Mallet;
import com.example.wangalbert.bookpractice.objects.Table;
import com.example.wangalbert.bookpractice.objects.Watermark;
import com.example.wangalbert.bookpractice.program.BlurHShaderProgram;
import com.example.wangalbert.bookpractice.program.BlurVShaderProgram;
import com.example.wangalbert.bookpractice.program.ColorShaderProgram;
import com.example.wangalbert.bookpractice.program.TextureOpacityShaderProgram;
import com.example.wangalbert.bookpractice.program.TextureShaderProgram;
import com.example.wangalbert.bookpractice.utils.MatrixHelper;
import com.example.wangalbert.bookpractice.utils.TextureHelper;
import com.example.wangalbert.bookpractice.utils.FrameBufferHelper;

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
  private float[] projectionMatrix = new float[16];
  protected float[] modelMatrix = new float[16];

  // data object
  private Table table;
  private BlurTable blurTable;
  private Mallet mallet;
  private Watermark watermark;

  // shader program
  private TextureShaderProgram textureProgram;
  private BlurHShaderProgram blurHorizontalProgram;
  private BlurVShaderProgram blurVerticalProgram;
  private ColorShaderProgram colorProgram;
  private TextureOpacityShaderProgram textureOpacityProgram;

  private Context context;

  // texture
  private int texture;
  private int textureWatermark;

  // FBO
  private int fboId;
  private int fboTextureId;
  //should be power of 2 (POT)
  private int fboWidth = 1024;
  private int fboHeight = 1024;

  // FBO
  private int fboBlurId;
  private int fboBlurTextureId;

  // viewport
  private int viewportWidth;
  private int viewportHeight;

  // Constructor
  public AirHockeyRenderer(Context context) {
    this.context = context;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    table = new Table();
    blurTable = new BlurTable();
    mallet = new Mallet();
    watermark = new Watermark();

    textureProgram = new TextureShaderProgram(context);
    textureOpacityProgram = new TextureOpacityShaderProgram(context);
    blurHorizontalProgram = new BlurHShaderProgram(context);
    blurVerticalProgram = new BlurVShaderProgram(context);
    colorProgram = new ColorShaderProgram(context);

    // load texture
    texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    textureWatermark = TextureHelper.loadTexture(context, R.drawable.logo_watermark);

    // create fbo
    int[] tmp = FrameBufferHelper.createFrameBuffer(fboWidth, fboHeight);
    fboId = tmp[0];
    fboTextureId = tmp[1];

    // create fboBlur
    tmp = FrameBufferHelper.createFrameBuffer(fboWidth, fboHeight);
    fboBlurId = tmp[0];
    fboBlurTextureId = tmp[1];

    Log.d(TAG, "texture=" + texture);
    Log.d(TAG, "fboId=" + fboId + ", fboTextureId=" + fboTextureId);
    Log.d(TAG, "fboBlurId=" + fboBlurId + ", fboBlurTextureId=" + fboBlurTextureId);
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
    // -------- manage time -----------
    // Do a complete rotation every 10 seconds.
    long time = SystemClock.uptimeMillis() % 8000L;
    float angleInDegrees = (360.0f / 3000.0f) * ((int) time);
    float blur; // = Math.min(2.0f,(float)((angleInDegrees % 360) / 120.0));   // blur goes from 0-2
    float opacity;
    if (time < 2000) {
      blur = 0;
      opacity = 0;
    }
    else if (time < 4000) {
      blur = (float) (1 - (4000 - time) / 2000.0);
      opacity = (float) (1 - (4000 - time) / 2000.0);
    }
    else {
      blur = 1.0f;
      opacity = 1.0f;
    }

    Log.d(TAG, "TEST: blur = " + blur + ", opacity = " + opacity);

    // -------- render to fbo ---------
    glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    setupViewport(fboWidth, fboWidth);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Draw the table.
    textureProgram.useProgram();
    textureProgram.setUniforms(projectionMatrix, texture); //texture //fboTextureId
    table.bindData(textureProgram);
    table.draw();

    // Draw the mallets.
    colorProgram.useProgram();
    colorProgram.setUniforms(projectionMatrix);
    mallet.bindData(colorProgram);
    mallet.draw();

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    // -------------------------------


    // -------- render to fbo blur --------
    glBindFramebuffer(GL_FRAMEBUFFER, fboBlurId);
    setupViewport(fboWidth, fboWidth);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    //draw to texture
    blurHorizontalProgram.useProgram();
    blurHorizontalProgram.setUniforms(projectionMatrix, fboTextureId, blur);
    blurTable.bindData(blurHorizontalProgram);
    blurTable.draw();

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    // --------------------------------------


    // -------- render to the screen ---------
    setupViewport(viewportWidth, viewportHeight);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    blurVerticalProgram.useProgram();
    blurVerticalProgram.setUniforms(projectionMatrix, fboBlurTextureId, blur); //fboTextureId //texture
    blurTable.bindData(blurVerticalProgram);
    blurTable.draw();

    textureOpacityProgram.useProgram();
    textureOpacityProgram.setUniforms(projectionMatrix, textureWatermark, opacity);
    watermark.bindData(textureOpacityProgram);
    watermark.draw();
    // ----------------------------------------
  }
}
