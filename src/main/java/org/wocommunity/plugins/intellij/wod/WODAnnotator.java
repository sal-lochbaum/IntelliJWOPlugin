package org.wocommunity.plugins.intellij.wod;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.wod.psi.WODBinding;
import org.wocommunity.plugins.intellij.wod.psi.WODComponent;
import org.wocommunity.plugins.intellij.wod.psi.WODElement;
import org.wocommunity.plugins.intellij.wod.psi.WODKeyPath;

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
        if (psiElement instanceof WODElement element) {
            annotateElement(element, holder);
        }
        else if (psiElement instanceof WODComponent component) {
            annotateComponent(component, holder);
        }
        else if (psiElement instanceof WODBinding binding) {
            annotateBinding(binding, holder);
        }
        else if (psiElement instanceof WODKeyPath keyPath) {
            annotateKeyPath(keyPath, holder);
        }
    }

    private void annotateElement(@NotNull WODElement element, @NotNull AnnotationHolder holder) {
        // TODO: find out which elements are named in the HTML file and match against the identifier of this element
        //       -> Identifier is used => normal color
        //       -> Identifier is not used => unused color
        // TODO: Check duplicate identifiers => error
        // Im HTML im Eclipse Plugin steht: The element 'LabelTargetUrl' is not defined in AdminModalEditExternalReference.wod
        // There is no element named 'LocClose' in your component HTML file
        holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved identifier")
                .range(element)
                .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                .create();
    }

    private void annotateComponent(@NotNull WODComponent component, @NotNull AnnotationHolder holder) {
        // TODO: Validate component name against WebObjects tag names or java classes...
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
                // Success
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(component)
                        .textAttributes(JavaHighlightingColors.CLASS_NAME_ATTRIBUTES)
                        .create();
                return;
            }
        }

        if (candidateClasses.length == 0) {
            holder.newAnnotation(HighlightSeverity.ERROR, "The class for '" + className + "' is missing")
                    .range(component)
                    //.highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
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
