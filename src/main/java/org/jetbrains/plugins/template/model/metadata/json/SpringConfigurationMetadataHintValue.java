package org.jetbrains.plugins.template.model.metadata.json;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiType;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.template.GenericUtil;
import org.jetbrains.plugins.template.clazz.MetadataProxy;
import org.jetbrains.plugins.template.model.suggestion.FileType;
import org.jetbrains.plugins.template.model.suggestion.Suggestion;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNode;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNodeType;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.jetbrains.plugins.template.GenericUtil.*;
import static org.jetbrains.plugins.template.PsiCustomUtil.toClassFqn;
import static org.jetbrains.plugins.template.PsiCustomUtil.toClassNonQualifiedName;
import static org.jetbrains.plugins.template.yaml.YamlValueInsertHandler.unescapeValue;

/**
 * Refer to https://docs.spring.io/spring-boot/docs/2.0.0.M6/reference/htmlsingle/#configuration-metadata-hints-attributes
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpringConfigurationMetadataHintValue {
  /**
   * A valid value for the element to which the hint refers. If the type of the associated PROPERTY is an ARRAY, it can also be an ARRAY of value(s). This attribute is mandatory.
   */
  @SerializedName("value")
  private Object nameAsObjOrArray;
  @Nullable
  private String description;

  @Override
  public String toString() {
    if (nameAsObjOrArray instanceof Array) {
      StringBuilder builder = new StringBuilder("[");
      int length = Array.getLength(nameAsObjOrArray);
      for (int i = 0; i < length; i++) {
        Object arrayElement = Array.get(nameAsObjOrArray, i);
        builder.append(" ").append(arrayElement.toString());
        if (i == length - 1) {
          builder.append(" ");
        } else {
          builder.append(",");
        }
      }
      return builder.append("]").toString();
    } else if (nameAsObjOrArray instanceof Collection) {
      Collection nameAsCollection = Collection.class.cast(nameAsObjOrArray);
      StringBuilder builder = new StringBuilder("[");
      for (int i = 0; i < nameAsCollection.size(); i++) {
        Object arrayElement = Array.get(nameAsObjOrArray, i);
        builder.append(" ").append(arrayElement.toString());
        if (i == nameAsCollection.size() - 1) {
          builder.append(" ");
        } else {
          builder.append(",");
        }
      }
      return builder.append("]").toString();
    } else {
      return nameAsObjOrArray.toString();
    }
  }

  public boolean representsSingleValue() {
    return !nameAsObjOrArray.getClass().isArray() && !Collection.class.isInstance(nameAsObjOrArray);
  }

  @NotNull
  public Suggestion buildSuggestionForKey(FileType fileType,
      List<SuggestionNode> matchesRootTillParentNode, int numOfAncestors, SuggestionNode match,
      @Nullable PsiType keyType) {
    List<SuggestionNode> matchesRootTillMe = newListWithMembers(matchesRootTillParentNode, match);
    Suggestion.SuggestionBuilder builder = Suggestion.builder().suggestionToDisplay(
        GenericUtil.dotDelimitedOriginalNames(matchesRootTillMe, numOfAncestors))
        .description(description).numOfAncestors(numOfAncestors).matchesTopFirst(matchesRootTillMe);

    if (keyType != null) {
      builder.shortType(toClassNonQualifiedName(keyType));
      builder.icon(SuggestionNodeType.ENUM.getIcon());
    }
    return builder.fileType(fileType).build();
  }

  @NotNull
  public String getDocumentationForKey(Module module, String nodeNavigationPathDotDelimited,
      @Nullable MetadataProxy delegate) {
    StringBuilder builder =
        new StringBuilder().append("<b>").append(nodeNavigationPathDotDelimited).append("</b>");

    if (delegate != null && delegate.getPsiType(module) != null) {
      String classFqn = toClassFqn(requireNonNull(delegate.getPsiType(module)));
      if (classFqn != null) {
        builder.append(" (");
        updateClassNameAsJavadocHtml(builder, classFqn);
        builder.append(")");
      }
    }

    if (description != null) {
      builder.append("<p>").append(description).append("</p>");
    }

    return builder.toString();
  }

  @NotNull
  public Suggestion buildSuggestionForValue(FileType fileType,
                                            List<? extends SuggestionNode> matchesRootTillLeaf, @Nullable String defaultValue,
                                            @Nullable PsiType valueType) {
    Suggestion.SuggestionBuilder builder =
        Suggestion.builder().suggestionToDisplay(toString()).description(description).forValue(true)
            .matchesTopFirst(matchesRootTillLeaf).numOfAncestors(matchesRootTillLeaf.size());

    if (valueType != null) {
      builder.shortType(shortenedType(valueType.getCanonicalText()));
      builder.icon(SuggestionNodeType.ENUM.getIcon());
    }

    builder.representingDefaultValue(toString().equals(defaultValue));
    return builder.fileType(fileType).build();
  }

  @NotNull
  public String getDocumentationForValue(@NotNull String nodeNavigationPathDotDelimited,
      @Nullable PsiType mapValueType) {
    StringBuilder builder =
        new StringBuilder().append("<b>").append(nodeNavigationPathDotDelimited).append("</b>= <b>")
            .append(unescapeValue(unescapeValue(toString()))).append("</b>");

    if (mapValueType != null) {
      String className = mapValueType.getCanonicalText();
      builder.append(" (");
      updateClassNameAsJavadocHtml(builder, className);
      builder.append(")");
    }

    if (description != null) {
      builder.append("<p>").append(description).append("</p>");
    }

    return builder.toString();
  }

}
