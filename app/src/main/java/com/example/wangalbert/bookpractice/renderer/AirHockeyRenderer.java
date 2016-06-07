package com.example.wangalbert.bookpractice.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.example.wangalbert.bookpractice.R;
import com.example.wangalbert.bookpractice.objects.BlurTable;
import com.example.wangalbert.bookpractice.objects.Mallet;
import com.example.wangalbert.bookpractice.objects.Table;
import com.example.wangalbert.bookpractice.program.BlurHShaderProgram;
import com.example.wangalbert.bookpractice.program.BlurVShaderProgram;
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
  private float[] projectionMatrix = new float[16];
  //private float[] modelMatrix = new float[16];

  protected float[] MVPMatrix = new float[16];
  protected float[] modelMatrix = new float[16];
  protected float[] viewMatrix = new float[16];
  //protected float[] projectionMatrix = new float[16];

  // data object
  private Table table;
  private BlurTable blurTable;
  private Mallet mallet;

  // shader program
  private TextureShaderProgram textureProgram;
  private BlurHShaderProgram blurHorizontalProgram;
  private BlurVShaderProgram blurVerticalProgram;
  private ColorShaderProgram colorProgram;

  private int texture;
  private Context context;

  // Constructor
  public AirHockeyRenderer(Context context) {
    this.context = context;
  }

  // FBO
  int fboId;
  int fboTextureId;
  int fboWidth = 1080;
  int fboHeight = 1533;

  // viewport
  int viewportWidth;
  int viewportHeight;

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    table = new Table();
    blurTable = new BlurTable();
    mallet = new Mallet();

    textureProgram = new TextureShaderProgram(context);
    blurHorizontalProgram = new BlurHShaderProgram(context);
    blurVerticalProgram = new BlurVShaderProgram(context);
    colorProgram = new ColorShaderProgram(context);

    // load texture
    texture = TextureHelper.loadTexture(context, R.drawable.texture);

    // ------------------- create fbo -----------------------------
    // generate fbo
    int[] frameBufferObjectIds = new int[1];
    glGenFramebuffers(1, frameBufferObjectIds, 0);
    fboId = frameBufferObjectIds[0];
    // bind fbo
    glBindFramebuffer(GL_FRAMEBUFFER, fboId);

    // generate render buffer
    int[] renderObjectIds = new int[1];
    GLES20.glGenRenderbuffers(1, renderObjectIds, 0);
    //Bind render buffer and define buffer dimension
    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderObjectIds[0]);
    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);

    // generate texture
    int[] textureObjectIds = new int[1];
    glGenTextures(1, textureObjectIds, 0);
    fboTextureId = textureObjectIds[0];
    // bind texture
    glBindTexture(GL_TEXTURE_2D, fboTextureId);
    //Define texture parameters
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboWidth, fboHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    // Attach texture FBO color attachment
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTextureId, 0);
    // Attach render buffer to depth attachment
    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderObjectIds[0]);

    // we are done, reset
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

    Log.d(TAG, "texture="+texture+", fboTextureId="+fboTextureId+", fboId="+fboId);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    viewportWidth = width;
    viewportHeight = height;

    setupViewport(width, height);

    Log.d(TAG, "fboId=" + fboId + ", texture=" + texture);
    Log.d(TAG, "viewportWidth=" + viewportWidth + ", viewportHeight=" + viewportHeight);
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

  private void setupFBOViewport(int width, int height) {
    // Set the OpenGL viewport to fill the entire surface.
    glViewport(0, 0, width, height);

    // Create a new perspective projection matrix. The height will stay the same
    // while the width will vary as per aspect ratio.
    final float left = -1.0f;   //-ratio;
    final float right = 1.0f;   //ratio;
    final float bottom = -1.0f;
    final float top = 1.0f;
    final float near = -1.0f;   //1.0f;
    final float far = 1.0f;     //10.0f;
    Matrix.orthoM(projectionMatrix, 0, left, right, bottom, top, near, far);

    // Position the eye behind the origin.
    final float eyeX = 0.0f;
    final float eyeY = 0.0f;
    final float eyeZ = 1.0f;
    // We are looking toward the distance
    final float lookX = 0.0f;
    final float lookY = 0.0f;
    final float lookZ = 0.0f;
    // Set our up vector. This is where our head would be pointing were we holding the camera.
    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;
    // Set the view matrix. This matrix can be said to represent the camera position.
    // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
    // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
    Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

    Matrix.setIdentityM(modelMatrix, 0);
    Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);
  }

  @Override
  public void onDrawFrame(GL10 gl) {

    // Do a complete rotation every 10 seconds.
    long time = SystemClock.uptimeMillis() % 10000L;
    float angleInDegrees = (360.0f / 3000.0f) * ((int) time);
    float blur = Math.min(2.0f,(float)((angleInDegrees % 360) / 120.0));   // blur goes from 0-2

    Log.d(TAG, "TEST: blur = " + blur);

    // Clear the rendering surface.
    //glClear(GL_COLOR_BUFFER_BIT);

    // --- render to fbo ---
    glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    setupFBOViewport(fboWidth, fboWidth);

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    //draw to texture
    blurHorizontalProgram.useProgram();
    blurHorizontalProgram.setUniforms(MVPMatrix, texture, blur);
    blurTable.bindData(blurHorizontalProgram);
    blurTable.draw();

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    // ---------------------

    setupViewport(viewportWidth, viewportHeight);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    //
    blurVerticalProgram.useProgram();
    blurVerticalProgram.setUniforms(MVPMatrix, fboTextureId, blur);
    blurTable.bindData(blurVerticalProgram);
    blurTable.draw();

    /*
    // Draw the table.
    textureProgram.useProgram();
    textureProgram.setUniforms(projectionMatrix, fboTextureId); //texture //fboTextureId
    table.bindData(textureProgram);
    table.draw();
    */

    // Draw the mallets.
    /*
    colorProgram.useProgram();
    colorProgram.setUniforms(projectionMatrix);
    mallet.bindData(colorProgram);
    mallet.draw();
    */
  }
}
