package org.jetbrains.plugins.template.clazz;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.template.model.suggestion.FileType;
import org.jetbrains.plugins.template.model.suggestion.Suggestion;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNode;

import java.util.List;

public interface ClassSuggestionNode extends SuggestionNode {
  @NotNull
  Suggestion buildSuggestionForKey(Module module, FileType fileType,
                                   List<SuggestionNode> matchesRootTillMe, int numOfAncestors);
}
