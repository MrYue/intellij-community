/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.ide;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author yole
 */
public class TypeNameEP extends AbstractExtensionPointBean {
  public static ExtensionPointName<TypeNameEP> EP_NAME = ExtensionPointName.create("com.intellij.typeName");

  @Attribute("className")
  public String className;

  @Attribute("name")
  public String name;

  @Attribute("resourceBundle")
  public String resourceBundle;

  @Attribute("resourceKey")
  public String resourceKey;

  private NotNullLazyValue<Class> myClass = new NotNullLazyValue<Class>() {
    @NotNull
    @Override
    protected Class compute() {
      try {
        return findClass(className);
      }
      catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  };

  private NullableLazyValue<String> myName = new NullableLazyValue<String>() {
    @Override
    protected String compute() {
      if (name != null) {
        return name;
      }
      if (resourceKey != null) {
        String bundleName = resourceBundle;
        if (bundleName == null && myPluginDescriptor != null) {
          bundleName = ((IdeaPluginDescriptor) myPluginDescriptor).getResourceBundleBaseName();
        }
        if (bundleName != null) {
          ResourceBundle bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault(), getLoaderForClass());
          return bundle.getString(resourceKey);
        }
      }
      return null;
    }
  };

  @Nullable
  public String getTypeName(Class aClass) {
    if (myClass.getValue().isAssignableFrom(aClass)) {
      return myName.getValue();
    }
    return null;
  }
}
