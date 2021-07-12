package org.jetbrains.plugins.template.model.metadata.json;

public interface GsonPostProcessable {
  void doOnGsonDeserialization();
}
