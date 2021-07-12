package org.jetbrains.plugins.template.model.suggestion;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public interface SuggestionNodeTypeProvider {
  /**
   * @param module module to which this node belongs
   * @return type of node
   */
  @NotNull
  SuggestionNodeType getSuggestionNodeType(Module module);
}
