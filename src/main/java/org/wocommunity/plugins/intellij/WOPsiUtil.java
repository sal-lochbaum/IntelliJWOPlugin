package org.wocommunity.plugins.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.wod.psi.WODFile;

public class WOPsiUtil {
    private static final @NonNls @NotNull String WO_ELEMENT_FQN = "com.webobjects.appserver.WOElement";

    public static boolean elementIsComponent(PsiElement element) {
        return element instanceof PsiDirectory && WOFileUtil.COMPONENT_EXTENSION.equalsIgnoreCase(((PsiDirectory) element).getVirtualFile().getExtension());
    }

    public static boolean elementIsInComponent(@NotNull PsiElement element) {
        return elementIsComponentTemplate(element) || elementIsComponentDeclaration(element) || elementIsComponentApi(element) || elementIsComponentWoo(element);
    }

    public static boolean elementIsComponentTemplate(@NotNull PsiElement element) {
        return element instanceof PsiFile && WOFileUtil.TEMPLATE_EXTENSION.equalsIgnoreCase(((PsiFile) element).getVirtualFile().getExtension()) && elementIsComponent(element.getParent());
    }

    public static boolean elementIsComponentDeclaration(@NotNull PsiElement element) {
        return element instanceof WODFile
                && WOFileUtil.DECLARATION_EXTENSION.equalsIgnoreCase(((WODFile) element).getVirtualFile().getExtension())
                && elementIsComponent(element.getParent());
    }

    public static boolean elementIsComponentApi(@NotNull PsiElement element) {
        return element instanceof PsiFile && WOFileUtil.API_EXTENSION.equalsIgnoreCase(((PsiFile) element).getVirtualFile().getExtension()) && elementIsComponent(element.getParent());
    }

    public static boolean elementIsComponentWoo(@NotNull PsiElement element) {
        return element instanceof PsiFile && WOFileUtil.WOO_EXTENSION.equalsIgnoreCase(((PsiFile) element).getVirtualFile().getExtension()) && elementIsComponent(element.getParent());
    }

    public static String getComponentName(@NotNull PsiElement element) {
        PsiDirectory component = getComponent(element);
        if (component != null) {
            return component.getVirtualFile().getNameWithoutExtension();
        }
        return null;
    }

    public static PsiDirectory getComponent(@NotNull PsiElement element) {
        if (elementIsComponent(element)) {
            return (PsiDirectory) element;
        }
        if (elementIsInComponent(element)) {
            return ((PsiFile) element).getParent();
        }
        return null;
    }

    public static PsiFile getTemplate(@NotNull PsiElement element) {
        if (elementIsComponentTemplate(element)) {
            return (PsiFile) element;
        }

        PsiDirectory component = getComponent(element);
        if (component != null) {
            String componentName = component.getVirtualFile().getNameWithoutExtension();
            return component.findFile(componentName + "." + WOFileUtil.TEMPLATE_EXTENSION);
        }
        return null;
    }

    public static WODFile getDeclaration(@NotNull PsiElement element) {
        if (elementIsComponentDeclaration(element)) {
            return (WODFile) element;
        }

        PsiDirectory component = getComponent(element);
        if (component != null) {
            String componentName = component.getVirtualFile().getNameWithoutExtension();
            PsiFile file = component.findFile(componentName + "." + WOFileUtil.DECLARATION_EXTENSION);
            if (file instanceof WODFile wodFile) {
                return wodFile;
            }
        }
        return null;
    }

    public static PsiFile getApi(@NotNull PsiElement element) {
        if (elementIsComponentApi(element)) {
            return (PsiFile) element;
        }

        PsiDirectory component = getComponent(element);
        if (component != null) {
            String componentName = component.getVirtualFile().getNameWithoutExtension();
            return component.findFile(componentName + "." + WOFileUtil.API_EXTENSION);
        }
        return null;
    }

    public static PsiFile getWoo(@NotNull PsiElement element) {
        if (elementIsComponentWoo(element)) {
            return (PsiFile) element;
        }

        PsiDirectory component = getComponent(element);
        if (component != null) {
            String componentName = component.getVirtualFile().getNameWithoutExtension();
            return component.findFile(componentName + "." + WOFileUtil.WOO_EXTENSION);
        }
        return null;
    }

    public static PsiClass getPsiClass(@NotNull String className, @NotNull Project project) throws Exception {
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        PsiClass baseClass = facade.findClass(WO_ELEMENT_FQN, scope);
        if (baseClass == null) {
            throw new Exception(WO_ELEMENT_FQN + " not found in project classpath");
        }

        PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
        PsiClass[] candidateClasses = cache.getClassesByName(className, scope);

        if (candidateClasses.length == 0) {
            throw new Exception("The class for '" + className + "' is missing");
        }

        for (PsiClass candidateClass : candidateClasses) {
            if (candidateClass.isInheritor(baseClass, true)) {
                // Found component :-)
                return candidateClass;
            }
        }

        // We found classes but none of them extends WOElement
        throw new Exception("The class for '" + className + "' does not extend WOElement");
    }
}
