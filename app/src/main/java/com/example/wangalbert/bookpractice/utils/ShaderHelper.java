package com.example.wangalbert.bookpractice.utils;

import android.util.Log;

import static android.opengl.GLES20.*;

/**
 * android
 * <p>
 * Created by wangalbert on 6/5/16.
 * Copyright (c) 2016 MobiusBobs Inc. All rights reserved.
 */
public class ShaderHelper {
  private static final String TAG = "ShaderHelper";

  public static int compileVertexShader(String shaderCode) {
    return compileShader(GL_VERTEX_SHADER, shaderCode);
  }

  public static int compileFragmentShader(String shaderCode) {
    return compileShader(GL_FRAGMENT_SHADER, shaderCode);
  }

  private static int compileShader(int type, String shaderCode) {
    // create shader object
    final int shaderObjectId = glCreateShader(type);
    if (shaderObjectId == 0) {
      if (LoggerConfig.ON) {
        Log.w(TAG, "Could not create new shader.");
      }
      return 0;
    }

    // upload the source code to shader object
    glShaderSource(shaderObjectId, shaderCode);

    // compile the shader object
    glCompileShader(shaderObjectId);

    // check the status of the shader object
    final int[] compileStatus = new int[1];
    glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

    if (LoggerConfig.ON) {
      Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
    }

    if (compileStatus[0] == 0) {
      // If it failed, delete the shader object. glDeleteShader(shaderObjectId);
      if (LoggerConfig.ON) {
        Log.w(TAG, "Compilation of shader failed.");
      }
      return 0;
    }

    return shaderObjectId;
  }

  public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
    // create program object
    final int programObjectId = glCreateProgram();
    if (programObjectId == 0) {
      if (LoggerConfig.ON) {
        Log.w(TAG, "Could not create new program");
      }
      return 0;
    }

    // attach both fragment/vertex shader to the program object
    glAttachShader(programObjectId, vertexShaderId);
    glAttachShader(programObjectId, fragmentShaderId);

    // link the program
    glLinkProgram(programObjectId);

    // check the status of the program object
    final int[] linkStatus = new int[1];
    glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

    if (LoggerConfig.ON) {
      // Print the program info log to the Android log output.
      Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));
    }

    if (linkStatus[0] == 0) {
      // If it failed, delete the program object. glDeleteProgram(programObjectId);
      if (LoggerConfig.ON) {
        Log.w(TAG, "Linking of program failed.");
      }
      return 0;
    }


    return programObjectId;
  }

  public static boolean validateProgram(int programObjectId) {
    glValidateProgram(programObjectId);

    final int[] validateStatus = new int[1];
    glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

    Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programObjectId));
    return validateStatus[0] != 0;
  }

}
