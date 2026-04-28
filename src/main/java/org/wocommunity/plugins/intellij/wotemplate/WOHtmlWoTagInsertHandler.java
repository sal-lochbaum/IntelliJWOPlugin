package org.wocommunity.plugins.intellij.wotemplate;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.text.CharArrayUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Completes an inserted {@code wo:*} tag name to a full tag pair and positions the caret
 * for attribute input: {@code <wo:If |></wo:If>}.
 */
final class WOHtmlWoTagInsertHandler implements InsertHandler<com.intellij.codeInsight.lookup.LookupElement> {

    static final WOHtmlWoTagInsertHandler INSTANCE = new WOHtmlWoTagInsertHandler();

    private WOHtmlWoTagInsertHandler() {
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull com.intellij.codeInsight.lookup.LookupElement item) {
        Editor editor = context.getEditor();
        Document doc = editor.getDocument();

        int tail = context.getTailOffset(); // right after inserted tag name (e.g. "wo:If")
        if (tail < 0 || tail > doc.getTextLength()) {
            return;
        }

        // Avoid interfering if the user is already in a completed/closed tag.
        int nextNonWs = CharArrayUtil.shiftForward(doc.getCharsSequence(), tail, " \t\r\n");
        if (nextNonWs < doc.getTextLength()) {
            char c = doc.getCharsSequence().charAt(nextNonWs);
            if (c == '>' || c == '/' || c == '=') {
                return;
            }
        }

        // Extract the inserted qualified name from the document (best-effort).
        String qName = readQNameBackwards(doc, tail);
        if (qName == null || !qName.startsWith("wo:") || qName.length() <= 3) {
            return;
        }

        // Insert: space + "></wo:Name>"
        String closing = "></" + qName + ">";
        doc.insertString(tail, " " + closing);

        // Place caret after the inserted space (ready to type attributes).
        editor.getCaretModel().moveToOffset(tail + 1);
        PsiDocumentManager.getInstance(context.getProject()).commitDocument(doc);
    }

    private static String readQNameBackwards(@NotNull Document doc, int endOffset) {
        CharSequence text = doc.getCharsSequence();
        int i = endOffset - 1;
        if (i < 0) {
            return null;
        }
        while (i >= 0) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == ':') {
                i--;
                continue;
            }
            break;
        }
        int start = i + 1;
        if (start >= endOffset) {
            return null;
        }
        return text.subSequence(start, endOffset).toString();
    }
}

