package org.wocommunity.plugins.intellij.wotemplate;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.psi.wod.WODElement;
import org.wocommunity.plugins.intellij.psi.wod.WODFile;

/** Resolves {@code <webobject name="...">} names to their WOD declarations. */
public final class WOHtmlWebObjectReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                            @NotNull ProcessingContext context) {
                        if (!(element instanceof XmlAttributeValue value)
                                || !(value.getParent() instanceof XmlAttribute attribute)
                                || !isWebObjectName(attribute)) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        return new PsiReference[]{new WODNameReference(value,
                                ElementManipulators.getValueTextRange(value))};
                    }
                }, PsiReferenceRegistrar.HIGHER_PRIORITY);
    }

    private static boolean isWebObjectName(@NotNull XmlAttribute attribute) {
        return "webobject".equalsIgnoreCase(attribute.getParent().getName())
                && "name".equalsIgnoreCase(attribute.getName());
    }

    private static final class WODNameReference extends PsiReferenceBase<PsiElement> {
        private WODNameReference(@NotNull PsiElement element, @NotNull com.intellij.openapi.util.TextRange range) {
            super(element, range);
        }

        @Override
        public @Nullable PsiElement resolve() {
            String name = stripQuotes(myElement.getText());
            if (name == null || name.isBlank()) {
                return null;
            }

            var htmlFile = myElement.getContainingFile().getVirtualFile();
            if (htmlFile == null || htmlFile.getParent() == null) {
                return null;
            }
            var componentDirectory = htmlFile.getParent();
            var wodVirtualFile = componentDirectory.findChild(htmlFile.getNameWithoutExtension() + ".wod");
            if (wodVirtualFile == null) {
                return null;
            }

            PsiElement file = PsiManager.getInstance(myElement.getProject()).findFile(wodVirtualFile);
            if (!(file instanceof WODFile wod)) {
                return null;
            }
            return PsiTreeUtil.findChildrenOfType(wod, WODElement.class).stream()
                    .filter(element -> name.equals(element.getIdentifier().getText()))
                    .findFirst()
                    .orElse(null);
        }

        private static String stripQuotes(String text) {
            if (text.length() >= 2
                    && ((text.startsWith("\"") && text.endsWith("\""))
                    || (text.startsWith("'") && text.endsWith("'")))) {
                return text.substring(1, text.length() - 1);
            }
            return text;
        }
    }
}
