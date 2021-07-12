package org.jetbrains.plugins.template.model.suggestion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.plugins.template.yaml.YamlKeyInsertHandler;
import org.jetbrains.plugins.template.yaml.YamlValueInsertHandler;

public enum FileType {
  yaml, properties;

  public InsertHandler<LookupElement> newKeyInsertHandler() {
    if (this == FileType.yaml) {
      return new YamlKeyInsertHandler();
    }
    return null;
  }

  public InsertHandler<LookupElement> newValueInsertHandler() {
    if (this == FileType.yaml) {
      return new YamlValueInsertHandler();
    }
    return null;
  }

}
