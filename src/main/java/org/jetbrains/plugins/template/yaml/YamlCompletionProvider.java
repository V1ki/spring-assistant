package org.jetbrains.plugins.template.yaml;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import gnu.trove.THashSet;
import kotlin.io.ConsoleKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.template.suggestion.SuggestionService;
import org.jetbrains.plugins.template.suggestion.SuggestionServiceImpl;
import org.jetbrains.yaml.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.jetbrains.plugins.template.GenericUtil.truncateIdeaDummyIdentifier;
import static org.jetbrains.plugins.template.PsiCustomUtil.findModule;
import static org.jetbrains.plugins.template.model.suggestion.FileType.yaml;
import static org.jetbrains.plugins.template.model.suggestion.SuggestionNode.sanitise;

class YamlCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull final CompletionParameters completionParameters,
      final ProcessingContext processingContext, @NotNull final CompletionResultSet resultSet) {

    System.out.println(" ---- addCompletions");
    PsiElement element = completionParameters.getPosition();
    if (element instanceof PsiComment) {
      return;
    }

    Project project = element.getProject();
    Module module = findModule(element);

    SuggestionService service = ServiceManager.getService(project, SuggestionService.class);
    System.out.println("module:"+module);
    if ((module == null || !service.canProvideSuggestions(project, module))) {
      return;
    }

    Set<String> siblingsToExclude = null;

    PsiElement elementContext = element.getContext();
    PsiElement parent = requireNonNull(elementContext).getParent();
    if (parent instanceof YAMLSequence) {
      // lets force user to create array element prefix before he can ask for suggestions
      return;
    }
    if (parent instanceof YAMLSequenceItem) {
      for (PsiElement child : parent.getParent().getChildren()) {
        if (child != parent) {
          if (child instanceof YAMLSequenceItem) {
            YAMLValue value = YAMLSequenceItem.class.cast(child).getValue();
            if (value != null) {
              siblingsToExclude = getNewIfNotPresent(siblingsToExclude);
              siblingsToExclude.add(sanitise(value.getText()));
            }
          } else if (child instanceof YAMLKeyValue) {
            siblingsToExclude = getNewIfNotPresent(siblingsToExclude);
            siblingsToExclude.add(sanitise(YAMLKeyValue.class.cast(child).getKeyText()));
          }
        }
      }
    } else if (parent instanceof YAMLMapping) {
      for (PsiElement child : parent.getChildren()) {
        if (child != elementContext) {
          if (child instanceof YAMLKeyValue) {
            siblingsToExclude = getNewIfNotPresent(siblingsToExclude);
            siblingsToExclude.add(sanitise(YAMLKeyValue.class.cast(child).getKeyText()));
          }
        }
      }
    }

    List<LookupElementBuilder> suggestions;
    // For top level element, since there is no parent parentKeyValue would be null
    String queryWithDotDelimitedPrefixes = truncateIdeaDummyIdentifier(element);

    List<String> ancestralKeys = null;
    PsiElement context = elementContext;
    do {
      if (context instanceof YAMLKeyValue) {
        if (ancestralKeys == null) {
          ancestralKeys = new ArrayList<>();
        }
        ancestralKeys.add(0, truncateIdeaDummyIdentifier(((YAMLKeyValue) context).getKeyText()));
      }
      context = requireNonNull(context).getParent();
    } while (context != null);

    suggestions = service
        .findSuggestionsForQueryPrefix(project, module, yaml, element, ancestralKeys,
            queryWithDotDelimitedPrefixes, siblingsToExclude);

    if (suggestions != null) {
      suggestions.forEach(resultSet::addElement);
    }
  }

  @NotNull
  private Set<String> getNewIfNotPresent(@Nullable Set<String> siblingsToExclude) {
    if (siblingsToExclude == null) {
      return new THashSet<>();
    }
    return siblingsToExclude;
  }

}
