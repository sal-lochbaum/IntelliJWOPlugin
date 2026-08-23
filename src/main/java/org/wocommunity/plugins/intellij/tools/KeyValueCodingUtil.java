package org.wocommunity.plugins.intellij.tools;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.lang3.StringUtils;
import org.wocommunity.plugins.intellij.psi.wod.WODKeyPath;
import org.wocommunity.plugins.intellij.psi.wod.WODKeyPathElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyValueCodingUtil {

    /**
     * Warning: we can't resolve key paths that go through collections
     * We might to communicate this to the caller somehow.
     */
    public static PsiClass resolveWODKeyPath(PsiClass baseClass, WODKeyPath keyPath) {
        return resolveWODKeyPath(baseClass, keyPath, false);
    }
    public static PsiClass resolveWODKeyPath(PsiClass baseClass, WODKeyPath keyPath, boolean returnLastMatch) {
        PsiClass lastClass = baseClass;
        List<WODKeyPathElement> pathElements = keyPath.getWODKeyPathElementList();
        for (WODKeyPathElement element : pathElements) {
            String key = element.getText();
            key = key.replace(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED, "");
            if (key.isBlank()) {
                break;
            }
            PsiClass newClass = resolveSegment(lastClass, key);
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

    public static PsiClass resolveSegment(PsiClass baseClass, String key) {
        String capKey = StringUtils.capitalize(key);

        // 1. Public Accessor Methods (getKey(), key(), isKey())
        PsiMethod[] m = baseClass.findMethodsByName("get" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(m[0].getReturnType()); }
        m = baseClass.findMethodsByName(key, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(m[0].getReturnType()); }
        m = baseClass.findMethodsByName("is" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(m[0].getReturnType()); }

        // 2. Private Accessor Methods (_getKey(), _key(), _isKey())
        m = baseClass.findMethodsByName("_get" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(m[0].getReturnType()); }
        m = baseClass.findMethodsByName("_" + key, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(m[0].getReturnType()); }
        m = baseClass.findMethodsByName("_is" + capKey, true);
        if (m.length > 0 && m[0].hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(m[0].getReturnType()); }

        // 3. Direct Field / Instance Variable Access (_key, _isKey, key, isKey)
        PsiField field = baseClass.findFieldByName("_" + key, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(field.getType()); }
        field = baseClass.findFieldByName("_is" + capKey, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(field.getType()); }
        field = baseClass.findFieldByName(key, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(field.getType()); }
        field = baseClass.findFieldByName("is" + capKey, true);
        if (field != null && field.hasModifierProperty(PsiModifier.PUBLIC)) { return resolveTypeToClass(field.getType()); }

        return null;
    }

    private static PsiClass resolveTypeToClass(PsiType type) {
        return PsiTypesUtil.getPsiClass(type);
    }

    public static void addCompletionSuggestions(CompletionResultSet result, PsiClass baseClass) {
        Set<String> keys = new HashSet<String>();

        PsiMethod[] methods = baseClass.getAllMethods();
        for (PsiMethod method : methods) {
            if (!method.hasModifierProperty(PsiModifier.PUBLIC)) {
                continue;
            }
            if (!method.getParameterList().isEmpty()) {
                continue;
            }
            if (PsiTypes.voidType().equals(method.getReturnType())) {
                continue;
            }
            String methodName = method.getName();
            if (methodName.startsWith("_")) {
                methodName = methodName.substring(1);
            }
            if (methodName.startsWith("get")) {
                methodName = methodName.substring(3);
                methodName = StringUtils.uncapitalize(methodName);
            }
            else if (methodName.startsWith("is")) {
                methodName = methodName.substring(2);
                methodName = StringUtils.uncapitalize(methodName);
            }
            keys.add(methodName);
        }

        PsiField[] fields = baseClass.getFields();
        for (PsiField field : fields) {
            if (!field.hasModifierProperty(PsiModifier.PUBLIC)) {
                continue;
            }
            String fieldName = field.getName();
            if (fieldName.startsWith("_")) {
                fieldName = fieldName.substring(1);
            }
            if (fieldName.startsWith("is")) {
                fieldName = fieldName.substring(2);
                fieldName = StringUtils.uncapitalize(fieldName);
            }
            keys.add(fieldName);
        }

        for (String key : keys) {
            result.addElement(LookupElementBuilder.create(key));
        }
    }
}
