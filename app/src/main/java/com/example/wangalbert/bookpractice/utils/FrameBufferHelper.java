package com.example.wangalbert.bookpractice.utils;

import android.util.Log;

import static android.opengl.GLES20.*;

/**
 * android
 * <p>
 * Created by wangalbert on 6/7/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class FrameBufferHelper {
  private static final String TAG = "FrameBufferHelper";

  public static int[] createFrameBuffer(int width, int height) {
    int[] ids = new int[2];

    // generate fbo
    int[] frameBufferObjectIds = new int[1];
    glGenFramebuffers(1, frameBufferObjectIds, 0);
    // bind fbo
    glBindFramebuffer(GL_FRAMEBUFFER, frameBufferObjectIds[0]);

    // generate texture
    int[] textureObjectIds = new int[1];
    glGenTextures(1, textureObjectIds, 0);
    // bind texture
    glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

    // Define texture parameters
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    // Attach texture FBO color attachment
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureObjectIds[0], 0);

    // we are done, reset
    glBindTexture(GL_TEXTURE_2D, 0);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);

    // return result. 0: frameBufferId, 1: textureObjectId
    ids[0] = frameBufferObjectIds[0];
    ids[1] = textureObjectIds[0];

    Log.d(TAG, "frameBufferObjectIds[0]="+frameBufferObjectIds[0]+", textureObjectIds[0]=" + textureObjectIds[0]);
    return ids;
  }
}
