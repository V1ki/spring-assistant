package org.jetbrains.plugins.template.clazz;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.template.model.suggestion.FileType;
import org.jetbrains.plugins.template.model.suggestion.Suggestion;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNode;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNodeType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

class IterableKeySuggestionNode implements SuggestionNode {

  private final SuggestionNode unwrapped;

  IterableKeySuggestionNode(SuggestionNode unwrapped) {
    this.unwrapped = unwrapped;
  }

  @Nullable
  @Override
  public List<SuggestionNode> findDeepestSuggestionNode(Module module,
      List<SuggestionNode> matchesRootTillParentNode, String[] pathSegments,
      int pathSegmentStartIndex) {
    return unwrapped.findDeepestSuggestionNode(module, matchesRootTillParentNode, pathSegments,
        pathSegmentStartIndex);
  }

  @Nullable
  @Override
  public SortedSet<Suggestion> findKeySuggestionsForQueryPrefix(Module module, FileType fileType,
                                                                List<SuggestionNode> matchesRootTillMe, int numOfAncestors, String[] querySegmentPrefixes,
                                                                int querySegmentPrefixStartIndex) {
    return findKeySuggestionsForQueryPrefix(module, fileType, matchesRootTillMe, numOfAncestors,
        querySegmentPrefixes, querySegmentPrefixStartIndex, null);
  }

  @Nullable
  @Override
  public SortedSet<Suggestion> findKeySuggestionsForQueryPrefix(Module module, FileType fileType,
      List<SuggestionNode> matchesRootTillMe, int numOfAncestors, String[] querySegmentPrefixes,
      int querySegmentPrefixStartIndex, @Nullable Set<String> siblingsToExclude) {
    return unwrapped
        .findKeySuggestionsForQueryPrefix(module, fileType, matchesRootTillMe, numOfAncestors,
            querySegmentPrefixes, querySegmentPrefixStartIndex, siblingsToExclude);
  }

  @Override
  public String getDocumentationForKey(Module module, String nodeNavigationPathDotDelimited) {
    return unwrapped.getDocumentationForKey(module, nodeNavigationPathDotDelimited);
  }

  @Nullable
  @Override
  public SortedSet<Suggestion> findValueSuggestionsForPrefix(Module module, FileType fileType,
      List<SuggestionNode> matchesRootTillMe, String prefix) {
    return findValueSuggestionsForPrefix(module, fileType, matchesRootTillMe, prefix, null);
  }

  @Nullable
  @Override
  public SortedSet<Suggestion> findValueSuggestionsForPrefix(Module module, FileType fileType,
      List<SuggestionNode> matchesRootTillMe, String prefix,
      @Nullable Set<String> siblingsToExclude) {
    return unwrapped.findValueSuggestionsForPrefix(module, fileType, matchesRootTillMe, prefix,
        siblingsToExclude);
  }

  @Override
  public String getDocumentationForValue(Module module, String nodeNavigationPathDotDelimited,
      String originalValue) {
    return unwrapped.getDocumentationForValue(module, nodeNavigationPathDotDelimited, originalValue);
  }

  @Override
  public boolean isLeaf(Module module) {
    return unwrapped.isLeaf(module);
  }

  @Override
  public boolean isMetadataNonProperty() {
    return false;
  }

  @Nullable
  @Override
  public String getOriginalName() {
    return unwrapped.getOriginalName();
  }

  @Nullable
  @Override
  public String getNameForDocumentation(Module module) {
    return getOriginalName();
  }

  @Override
  public boolean supportsDocumentation() {
    return unwrapped.supportsDocumentation();
  }

  @NotNull
  @Override
  public SuggestionNodeType getSuggestionNodeType(Module module) {
    return unwrapped.getSuggestionNodeType(module);
  }

}
