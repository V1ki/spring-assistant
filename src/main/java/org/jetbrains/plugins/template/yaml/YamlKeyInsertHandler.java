package org.jetbrains.plugins.template.yaml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.template.model.suggestion.OriginalNameProvider;
import org.jetbrains.plugins.template.model.suggestion.Suggestion;
import org.jetbrains.plugins.template.model.suggestion.SuggestionNodeType;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.List;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.openapi.editor.EditorModificationUtil.insertStringAtCaret;
import static org.jetbrains.plugins.template.GenericUtil.*;
import static org.jetbrains.plugins.template.PsiCustomUtil.findModule;
import static org.jetbrains.plugins.template.model.suggestion.SuggestionNodeType.*;

public class YamlKeyInsertHandler implements InsertHandler<LookupElement> {

  @Override
  public void handleInsert(final InsertionContext context, final LookupElement lookupElement) {
    if (!nextCharAfterSpacesAndQuotesIsColon(getStringAfterAutoCompletedValue(context))) {
      String existingIndentation = getExistingIndentation(context, lookupElement);
      Suggestion suggestion = (Suggestion) lookupElement.getObject();
      String indentPerLevel = getCodeStyleIntent(context);
      Module module = findModule(context);
      String suggestionWithCaret =
          getSuggestionReplacementWithCaret(module, suggestion, existingIndentation,
              indentPerLevel);
      String suggestionWithoutCaret = suggestionWithCaret.replace(CARET, "");

      PsiElement currentElement = context.getFile().findElementAt(context.getStartOffset());
      assert currentElement != null : "no element at " + context.getStartOffset();

      this.deleteLookupTextAndRetrieveOldValue(context, currentElement);

      insertStringAtCaret(context.getEditor(), suggestionWithoutCaret, false, true,
          getCaretIndex(suggestionWithCaret));
    }
  }

  private int getCaretIndex(final String suggestionWithCaret) {
    return suggestionWithCaret.indexOf(CARET);
  }

  private String getExistingIndentation(final InsertionContext context, final LookupElement item) {
    final String stringBeforeAutoCompletedValue = getStringBeforeAutoCompletedValue(context, item);
    return getExistingIndentationInRowStartingFromEnd(stringBeforeAutoCompletedValue);
  }

  @NotNull
  private String getStringAfterAutoCompletedValue(final InsertionContext context) {
    return context.getDocument().getText().substring(context.getTailOffset());
  }

  @NotNull
  private String getStringBeforeAutoCompletedValue(final InsertionContext context,
      final LookupElement item) {
    return context.getDocument().getText()
        .substring(0, context.getTailOffset() - item.getLookupString().length());
  }

  private boolean nextCharAfterSpacesAndQuotesIsColon(final String string) {
    for (int i = 0; i < string.length(); i++) {
      final char c = string.charAt(i);
      if (c != ' ' && c != '"') {
        return c == ':';
      }
    }
    return false;
  }

  private String getExistingIndentationInRowStartingFromEnd(final String val) {
    int count = 0;
    for (int i = val.length() - 1; i >= 0; i--) {
      final char c = val.charAt(i);
      if (c != '\t' && c != ' ' && c != '-') {
        break;
      }
      count++;
    }
    return val.substring(val.length() - count, val.length()).replaceAll("-", " ");
  }

  private void deleteLookupTextAndRetrieveOldValue(InsertionContext context,
      @NotNull PsiElement elementAtCaret) {
    if (elementAtCaret.getNode().getElementType() != YAMLTokenTypes.SCALAR_KEY) {
      deleteLookupPlain(context);
    } else {
      YAMLKeyValue keyValue = PsiTreeUtil.getParentOfType(elementAtCaret, YAMLKeyValue.class);
      assert keyValue != null;
      context.commitDocument();

      // TODO: Whats going on here?
      if (keyValue.getValue() != null) {
        YAMLKeyValue dummyKV =
            YAMLElementGenerator.getInstance(context.getProject()).createYamlKeyValue("foo", "b");
        dummyKV.setValue(keyValue.getValue());
      }

      context.setTailOffset(keyValue.getTextRange().getEndOffset());
      runWriteCommandAction(context.getProject(),
          () -> keyValue.getParentMapping().deleteKeyValue(keyValue));
    }
  }

  private void deleteLookupPlain(InsertionContext context) {
    Document document = context.getDocument();
    document.deleteString(context.getStartOffset(), context.getTailOffset());
    context.commitDocument();
  }

  @NotNull
  private String getSuggestionReplacementWithCaret(Module module, Suggestion suggestion,
      String existingIndentation, String indentPerLevel) {
    StringBuilder builder = new StringBuilder();
    int i = 0;
    List<? extends OriginalNameProvider> matchesTopFirst = suggestion.getMatchesForReplacement();
    do {
      OriginalNameProvider nameProvider = matchesTopFirst.get(i);
      builder.append("\n").append(existingIndentation).append(getIndent(indentPerLevel, i))
          .append(nameProvider.getOriginalName()).append(":");
      i++;
    } while (i < matchesTopFirst.size());
    builder.delete(0, existingIndentation.length() + 1);
    String indentForNextLevel =
        getOverallIndent(existingIndentation, indentPerLevel, matchesTopFirst.size());
    String sufix = getPlaceholderSufixWithCaret(module, suggestion, indentForNextLevel);
    builder.append(sufix);
    return builder.toString();
  }

  @NotNull
  private String getPlaceholderSufixWithCaret(Module module, Suggestion suggestion,
      String indentForNextLevel) {
    if (suggestion.getLastSuggestionNode().isMetadataNonProperty()) {
      return "\n" + indentForNextLevel + CARET;
    }
    SuggestionNodeType nodeType = suggestion.getSuggestionNodeType(module);
    if (nodeType == UNDEFINED || nodeType == UNKNOWN_CLASS) {
      return CARET;
    } else if (nodeType.representsLeaf()) {
      return " " + CARET;
    } else if (nodeType.representsArrayOrCollection()) {
      return "\n" + indentForNextLevel + "- " + CARET;
    } else { // map or class
      return "\n" + indentForNextLevel + CARET;
    }
  }

}
