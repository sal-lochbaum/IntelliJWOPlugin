package org.wocommunity.plugins.intellij.java.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.psi.references.WODKeyPathReference;
import org.wocommunity.plugins.intellij.tools.WOIcons;

import java.util.Collection;
import java.util.List;

/**
 * Adds WO icon to java members used in WO templates
 */
public class JavaLineMarkerProvider implements LineMarkerProvider {
    @Override
    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (!(element instanceof PsiIdentifier)) return null;

        PsiElement parent = element.getParent();

        if (parent instanceof PsiField || parent instanceof PsiMethod) {
            FileType wodFileType = FileTypeManager.getInstance().getFileTypeByExtension("wod");
            FileType htmlFileType = FileTypeManager.getInstance().getFileTypeByExtension("html");


            GlobalSearchScope projectScope = ProjectScope.getProjectScope(element.getProject());
            GlobalSearchScope typeScope = GlobalSearchScope.getScopeRestrictedByFileTypes(projectScope, wodFileType, htmlFileType);

            Collection<?> references = ReferencesSearch.search(parent, typeScope).findAll();

            if (!references.isEmpty()) {
                return NavigationGutterIconBuilder.create(WOIcons.WOCOMPONENT_ICON)
                        .setTargets(references.stream()
                                .filter(ref -> ref instanceof WODKeyPathReference)
                                .map(ref -> ((WODKeyPathReference) ref).getElement())
                                .toList())
                        .setTooltipText("User in WO Component").createLineMarkerInfo(element);
            }
        }

        return null;
    }
}
