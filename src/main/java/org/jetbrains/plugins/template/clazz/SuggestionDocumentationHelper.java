package org.jetbrains.plugins.template.clazz;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.template.model.suggestion.FileType;
import org.jetbrains.plugins.template.model.suggestion.Suggestion;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNode;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNodeTypeProvider;

import javax.annotation.Nullable;
import java.util.List;

public interface SuggestionDocumentationHelper extends SuggestionNodeTypeProvider {

  @Nullable
  String getOriginalName();

  @NotNull
  Suggestion buildSuggestionForKey(Module module, FileType fileType,
                                   List<SuggestionNode> matchesRootTillMe, int numOfAncestors);

  /**
   * @return false if an intermediate node (neither group, nor property, nor class). true otherwise
   */
  boolean supportsDocumentation();

  @NotNull
  String getDocumentationForKey(Module module, String nodeNavigationPathDotDelimited);

}
