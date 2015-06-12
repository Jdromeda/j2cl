/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.j2cl.generator;

import com.google.j2cl.ast.CompilationUnit;
import com.google.j2cl.ast.JavaType;

/**
 * Utility functions to transpile the j2cl AST.
 */
public class TranspilerUtils {
  public static String getBinaryName(JavaType javaType) {
    // The information in TypeReference is not enough to compute binary name,
    // for example, the enclosing type, etc. so a JavaType object is needed.
    //TODO: to be implemented.
    return getCanonicName(javaType);
  }

  public static String getCanonicName(JavaType javaType) {
    // TODO: to be implemented
    return javaType.getTypeReference().getPackageName()
        + "."
        + javaType.getTypeReference().getSimpleName();
  }

  /**
   * Returns the unqualified name that will be used in JavaScript.
   */
  public static String getClassName(JavaType javaType) {
    //TODO: to be implemented.
    return javaType.getTypeReference().getSimpleName();
  }

  /**
   * Returns the JsDoc type name.
   */
  public static String getJsDocName(JavaType javaType) {
    //TODO: to be implemented.
    return getClassName(javaType);
  }

  /**
   * Returns the mangled name.
   */
  public static String getMangledName(JavaType javaType) {
    //TODO: to be implemented.
    return getCanonicName(javaType).replace('.', '_');
  }

  /**
   * Returns the relative output path for a given compilation unit.
   */
  public static String getOutputPath(CompilationUnit compilationUnit) {
    String unitName = compilationUnit.getName();
    String packageName = compilationUnit.getPackageName();
    return packageName.replace('.', '/') + "/" + unitName;
  }

  private TranspilerUtils() {}
}
