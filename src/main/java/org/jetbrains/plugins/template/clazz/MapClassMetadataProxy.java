package org.jetbrains.plugins.template.clazz;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.template.model.suggestion.FileType;
import org.jetbrains.plugins.template.model.suggestion.Suggestion;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNode;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class MapClassMetadataProxy extends ClassMetadataProxy {

  MapClassMetadataProxy(@NotNull PsiClassType type) {
    super(type);
  }

  @Nullable
  public SortedSet<Suggestion> findChildKeySuggestionForQueryPrefix(Module module,
                                                                    FileType fileType, List<SuggestionNode> matchesRootTillMe, int numOfAncestors,
                                                                    String[] querySegmentPrefixes, int querySegmentPrefixStartIndex) {
    return findChildKeySuggestionForQueryPrefix(module, fileType, matchesRootTillMe, numOfAncestors,
        querySegmentPrefixes, querySegmentPrefixStartIndex, null);
  }

  public SortedSet<Suggestion> findChildKeySuggestionForQueryPrefix(Module module,
      FileType fileType, List<SuggestionNode> matchesRootTillMe, int numOfAncestors,
      String[] querySegmentPrefixes, int querySegmentPrefixStartIndex,
      Set<String> siblingsToExclude) {
    return doWithTargetAndReturn(module, target -> {
      assert target instanceof MapClassMetadata;
      return MapClassMetadata.class.cast(target)
          .findChildKeySuggestionForQueryPrefix(module, fileType, matchesRootTillMe, numOfAncestors,
              querySegmentPrefixes, querySegmentPrefixStartIndex, siblingsToExclude);
    }, null);
  }

  @Nullable
  public PsiType getMapKeyType(Module module) {
    return doWithTargetAndReturn(module, target -> {
      assert target instanceof MapClassMetadata;
      return MapClassMetadata.class.cast(target).getKeyType();
    }, null);
  }

  @Nullable
  public PsiType getMapValueType(Module module) {
    return doWithTargetAndReturn(module, target -> {
      assert target instanceof MapClassMetadata;
      return MapClassMetadata.class.cast(target).getValueType();
    }, null);
  }

}
