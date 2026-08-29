package org.wocommunity.plugins.intellij.tools;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.jvm.JvmMember;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.util.PsiFormatUtilBase;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.PlatformIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.psi.wod.WODKeyPath;
import org.wocommunity.plugins.intellij.psi.wod.WODKeyPathElement;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class KeyValueCodingUtil {

    /**
     * Warning: we can't resolve key paths that go through collections
     * We might to communicate this to the caller somehow.
     */
    public static PsiMember resolveWODKeyPath(PsiClass baseClass, WODKeyPath keyPath) {
        return resolveWODKeyPath(baseClass, keyPath, false);
    }
    public static PsiMember resolveWODKeyPath(PsiClass baseClass, WODKeyPath keyPath, WODKeyPathElement elementToResolve) {
        return resolveWODKeyPath(baseClass, keyPath, elementToResolve, false);
    }
    public static PsiMember resolveWODKeyPath(PsiClass baseClass, WODKeyPath keyPath, boolean returnLastMatch) {
        return resolveWODKeyPath(baseClass, keyPath, null, returnLastMatch);
    }
    public static PsiMember resolveWODKeyPath(PsiClass baseClass, WODKeyPath keyPath, WODKeyPathElement elementToResolve, boolean returnLastMatch) {
        PsiClass lastClass = baseClass;
        List<WODKeyPathElement> pathElements = keyPath.getWODKeyPathElementList();
        for (WODKeyPathElement element : pathElements) {
            String key = element.getText();
            key = key.replace(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED, "");
            if (key.isBlank()) {
                break;
            }
            PsiMember resolved = resolveSegment(lastClass, key);
            if (element.equals(elementToResolve)) {
                return resolved;
            }

            PsiClass newClass = resolveMemberToClass(resolved);
            if (newClass == null) {
                if (returnLastMatch) {
                    return lastClass;
                }
                return null;
            }
            lastClass = newClass;
        }
        return lastClass;
    }

    public static @Nullable PsiElement resolveKeyPathElement(WODKeyPathElement elementToResolve) {
        PsiClass baseClass = WOPsiUtil.getPsiClass(elementToResolve);
        if (baseClass == null) {
            return null;
        }

        PsiElement keyPathElem = elementToResolve.getParent();
        if (!(keyPathElem instanceof WODKeyPath keyPath)) {
            return null;
        }
        return resolveWODKeyPath(baseClass, keyPath, elementToResolve);
    }

    public static PsiMember resolveSegment(PsiClass baseClass, String key) {
        String capKey = StringUtils.capitalize(key);

        // 1. Public Accessor Methods (getKey(), key(), isKey())
        PsiMethod[] m = baseClass.findMethodsByName("get" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return m[0]; }
        m = baseClass.findMethodsByName(key, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return m[0]; }
        m = baseClass.findMethodsByName("is" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return m[0]; }

        // 2. Private Accessor Methods (_getKey(), _key(), _isKey())
        m = baseClass.findMethodsByName("_get" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return m[0]; }
        m = baseClass.findMethodsByName("_" + key, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return m[0]; }
        m = baseClass.findMethodsByName("_is" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return m[0]; }

        // 3. Direct Field / Instance Variable Access (_key, _isKey, key, isKey)
        PsiField field = baseClass.findFieldByName("_" + key, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return field; }
        field = baseClass.findFieldByName("_is" + capKey, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return field; }
        field = baseClass.findFieldByName(key, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return field; }
        field = baseClass.findFieldByName("is" + capKey, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return field; }

        return null;
    }

    private static PsiClass resolveMemberToClass(PsiMember member) {
        if (member instanceof PsiField field) {
            return PsiTypesUtil.getPsiClass(field.getType());
        }
        if (member instanceof PsiMethod method) {
            return PsiTypesUtil.getPsiClass(method.getReturnType());
        }
        if (member instanceof PsiClass clazz) {
            return clazz;
        }

        return null;
    }

    public static void addCompletionSuggestions(CompletionResultSet result, PsiClass baseClass) {
        Set<String> keys = new HashSet<String>();

        PsiMethod[] methods = baseClass.getAllMethods();
        for (PsiMethod method : methods) {
            if (method.isConstructor()) {
                continue;
            }
            if ("java.lang.Object".equals(Objects.requireNonNull(method.getContainingClass()).getQualifiedName())) {
                continue;
            }
            if (!method.hasModifierProperty(PsiModifier.PUBLIC)) {
                continue;
            }
            if (!method.getParameterList().isEmpty()) {
                continue;
            }
            if (PsiTypes.voidType().equals(method.getReturnType())) {
                continue;
            }
            String normalizedName = method.getName();
            if (normalizedName.startsWith("_")) {
                normalizedName = normalizedName.substring(1);
            }
            if (normalizedName.startsWith("get")) {
                normalizedName = normalizedName.substring(3);
                normalizedName = StringUtils.uncapitalize(normalizedName);
            }
            else if (normalizedName.startsWith("is")) {
                normalizedName = normalizedName.substring(2);
                normalizedName = StringUtils.uncapitalize(normalizedName);
            }
            if (keys.contains(normalizedName)) {
                continue;
            }

            keys.add(normalizedName);

            LookupElementBuilder lookup = LookupElementBuilder.create(method, method.getName())
                    .withIcon(PlatformIcons.METHOD_ICON) // Setzt das schicke "m"-Icon
                    .withTypeText(method.getReturnType() != null ? method.getReturnType().getPresentableText() : "void") // Zeigt den Rückgabetyp rechts an
                    .withTailText(PsiFormatUtil.formatMethod(method, PsiSubstitutor.EMPTY, PsiFormatUtilBase.SHOW_PARAMETERS, PsiFormatUtilBase.SHOW_TYPE)); // Zeigt Parameter an (z.B. "(String arg)")
            result.addElement(lookup);
        }

        PsiField[] fields = baseClass.getFields();
        for (PsiField field : fields) {
            if (!field.hasModifierProperty(PsiModifier.PUBLIC)) {
                continue;
            }
            String normalizedName = field.getName();
            if (normalizedName.startsWith("_")) {
                normalizedName = normalizedName.substring(1);
            }
            if (normalizedName.startsWith("is")) {
                normalizedName = normalizedName.substring(2);
                normalizedName = StringUtils.uncapitalize(normalizedName);
            }
            if (keys.contains(normalizedName)) {
                continue;
            }

            keys.add(normalizedName);
            LookupElementBuilder lookup = LookupElementBuilder.create(field, field.getName())
                    .withIcon(PlatformIcons.FIELD_ICON) // Setzt das "f"-Icon
                    .withTypeText(field.getType().getPresentableText()) // Zeigt den Feld-Typ rechts an
                    .bold(); // Macht den Namen fett, weil es z.B. ein direktes Attribut ist
            result.addElement(lookup);
        }
    }
}
