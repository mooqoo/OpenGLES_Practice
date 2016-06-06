package com.example.wangalbert.bookpractice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;

/**
 * android
 * <p>
 * Created by wangalbert on 6/6/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class TextureHelper {
  private static final String TAG = "TextureHelper";

  public static int loadTexture(Context context, int resourceId) {
    // generate texture object
    final int[] textureObjectIds = new int[1];
    glGenTextures(1, textureObjectIds, 0);

    if (textureObjectIds[0] == 0) {
      if (LoggerConfig.ON) {
        Log.w(TAG, "Could not generate a new OpenGL texture object.");
      }
      return 0;
    }

    // load bitmap
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;
    final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
    if (bitmap == null) {
      if (LoggerConfig.ON) {
        Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
      }
      glDeleteTextures(1, textureObjectIds, 0);
      return 0;
    }

    // tell openGL that texture calls should be applied to this texture object
    glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

    // filter texture
    //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // load bitmap into texture
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

    //glGenerateMipmap(GL_TEXTURE_2D);

    // recycle bitmap
    bitmap.recycle();

    // unbind texture so wont accidentally change the texture
    glBindTexture(GL_TEXTURE_2D, 0);

    return textureObjectIds[0];
  }

}
