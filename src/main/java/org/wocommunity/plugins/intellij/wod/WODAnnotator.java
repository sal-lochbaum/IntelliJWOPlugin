package org.wocommunity.plugins.intellij.wod;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.colors.impl.TextAttributeKeyColor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import kotlinx.html.HTMLTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.wod.psi.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/*
 * An Annotator helps highlight and annotate any code based on specific rules.
 *
 * Annotate the different usages of IDENTIFIER tokens
 */
final class WODAnnotator implements Annotator {
    private static final @NonNls
    @NotNull String WO_ELEMENT_FQN = "com.webobjects.appserver.WOElement";

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        switch (psiElement) {
            case WODElement element -> annotateElement(element, holder);
            case WODComponent component -> annotateComponent(component, holder);
            case WODBinding binding -> annotateBinding(binding, holder);
            case WODKeyPath keyPath -> annotateKeyPath(keyPath, holder);
            default -> {}
        }
    }

    private void annotateElement(@NotNull WODElement element, @NotNull AnnotationHolder holder) {
        // Find out which elements are named in the HTML file and match against the identifier of this element
        //       -> Identifier is not used => weak warning
        List<String> namesFromHtml = getNamesFromHTMLTemplate(element.getContainingFile());
        if (namesFromHtml.stream().noneMatch(element.getIdentifier().getText()::equals)) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "There is no element named '" + element.getIdentifier().getText() + "' in your component HTML file")
                    .range(element)
                    .create();
        }

        // Check duplicate identifiers => error
        PsiElement previousDeclaration = element.getParent().getPrevSibling();
        while (previousDeclaration != null) {
            if (previousDeclaration instanceof WODDeclaration && ((WODDeclaration) previousDeclaration).getWODElement().getIdentifier().getText().equals(element.getIdentifier().getText())) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Element '" + element.getIdentifier().getText() + "' is already defined above")
                        .range(element)
                        .create();
                break;
            }
            previousDeclaration = previousDeclaration.getPrevSibling();
        }

        // Nothing to complain about => normal color
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element)
                .textAttributes(TextAttributesKey.find("HTML_ATTRIBUTE_NAME"))
                .create();
    }

    // FIXME: This should probably be somewhere central or close to the HTML-File? :o
    private List<String> getNamesFromHTMLTemplate(PsiFile wodFile) {
        List<String> elementNames = new ArrayList<>();

        String componentName = wodFile.getName().replace(".wod", "");
        if (wodFile.getParent() == null) {
            return elementNames;
        }

        PsiFile htmlFile = wodFile.getParent().findFile(componentName + ".html");
        if (htmlFile == null) {
            return elementNames;
        }

        Collection<XmlTag> tags = PsiTreeUtil.findChildrenOfType(htmlFile, XmlTag.class);
        if (tags.isEmpty()) {
            return elementNames;
        }

        for (XmlTag tag : tags) {
            if (tag.getName().equals("webobject")) {
                @Nullable XmlAttribute name = tag.getAttribute("name");
                if (name != null) {
                    elementNames.add(name.getValue());
                }
            }
        }

        return elementNames;
    }

    private void annotateComponent(@NotNull WODComponent component, @NotNull AnnotationHolder holder) {
        // Validate component name against WebObjects java classes
        //       -> Valid => normal color
        //       -> Invalid => error annotation

        String className = component.getIdentifier().getText();
        if (className.isEmpty()) {
            return;
        }
        Project project = component.getProject();
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        PsiClass baseClass = facade.findClass(WO_ELEMENT_FQN, scope);
        if (baseClass == null) {
            holder.newAnnotation(HighlightSeverity.WARNING, WO_ELEMENT_FQN + " not found in project classpath")
                    .range(component)
                    .highlightType(ProblemHighlightType.WARNING)
                    .create();
            return;
        }

        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        PsiClass[] candidateClasses = cache.getClassesByName(className, scope);
        for (PsiClass candidateClass : candidateClasses) {
            if (candidateClass.isInheritor(baseClass, true)) {
                // Nothing to complain about => normal color
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(component)
                        .textAttributes(TextAttributesKey.find("HTML_TAG_NAME"))
                        .create();
                return;
            }
        }

        if (candidateClasses.length == 0) {
            holder.newAnnotation(HighlightSeverity.ERROR, "The class for '" + className + "' is missing")
                    .range(component)
                    .create();
            return;
        }

        holder.newAnnotation(HighlightSeverity.ERROR, "The class for '" + className + "' does not extend WOElement")
                .range(component)
                .create();
    }

    private void annotateBinding(@NotNull WODBinding binding, @NotNull AnnotationHolder holder) {
        // TODO: Validate against known bindings? Does that make sense i.e. with components that take all  data-xxx bindings
        // WOSystemBindingDefinitions



        holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved binding")
                .range(binding)
                .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                .create();
    }

    private void annotateKeyPath(@NotNull WODKeyPath keyPath, @NotNull AnnotationHolder holder) {
        // TODO: Validate against java fields or methods or getters
        // In the WOD, The key 'WOComponentName' uses a value that is deprecated.
        // In the WOD, Unable to verify key 'meldung' because the keypath 'iterUpload.fehler' in LPModernMediaUpload passes through a collection
        holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved key path")
                .range(keyPath)
                .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                .create();
    }
}
