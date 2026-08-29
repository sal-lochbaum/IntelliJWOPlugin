package org.wocommunity.plugins.intellij.wotemplate;

import com.intellij.codeInspection.XmlInspectionSuppressor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 * The HTML empty-tag inspection predates WebObjects and assumes that every
 * unprefixed HTML tag is sent to a browser. WebObjects tags are server-side
 * template constructs, so that warning is not applicable to them.
 */
public final class WOHtmlInspectionSuppressor extends XmlInspectionSuppressor {

    private static final String EMPTY_TAG_INSPECTION = "CheckEmptyScriptTag";

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!EMPTY_TAG_INSPECTION.equals(toolId)) {
            return super.isSuppressedFor(element, toolId);
        }

        XmlTag tag = element instanceof XmlTag
                ? (XmlTag) element
                : element.getParent() instanceof XmlTag ? (XmlTag) element.getParent() : null;
        if (tag == null || !isComponentHtml(tag.getContainingFile())) {
            return super.isSuppressedFor(element, toolId);
        }

        String name = tag.getName();
        String localName = tag.getLocalName();
        return "webobject".equalsIgnoreCase(name)
                || "webobject".equalsIgnoreCase(localName)
                || (localName != null && "wo".equalsIgnoreCase(tag.getNamespacePrefix()));
    }

    private static boolean isComponentHtml(@NotNull PsiFile file) {
        var virtualFile = file.getVirtualFile();
        if (virtualFile == null || !virtualFile.getName().endsWith(".html")) {
            return false;
        }
        var parent = virtualFile.getParent();
        return parent != null && parent.getName().endsWith(".wo");
    }
}
