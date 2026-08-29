package org.wocommunity.plugins.intellij.wotemplate;

import com.intellij.openapi.project.DumbService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import com.intellij.xml.XmlExtension;
import com.intellij.xml.XmlTagNameProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class WOHtmlXmlExtension extends XmlExtension {

    private static final String WO_COMPONENT_FQN = "com.webobjects.appserver.WOComponent";
    private static final String WO_ELEMENT_FQN = "com.webobjects.appserver.WOElement";
    private static final String WO_DYNAMIC_ELEMENT_FQN = "com.webobjects.appserver.WODynamicElement";

    @Override
    public boolean isAvailable(PsiFile file) {
        var vFile = PsiUtilCore.getVirtualFile(file);
        if (vFile == null) {
            return false;
        }
        if (!vFile.getName().endsWith(".html")) {
            return false;
        }
        var parent = vFile.getParent();
        return parent != null && parent.getName().endsWith(".wo");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull List getAvailableTagNames(@NotNull XmlFile file, @NotNull XmlTag context) {
        Set<String> localNames = new LinkedHashSet<>();
        localNames.addAll(WOComponentTagAliasRegistry.getAliases().keySet());
        if (!DumbService.isDumb(file.getProject())) {
            localNames.addAll(collectWoInheritorNames(file));
        }

        List<TagInfo> out = new ArrayList<>(localNames.size());
        for (String localName : localNames) {
            out.add(new TagInfo(localName, WOHtmlFileNSInfoProvider.WO_NAMESPACE_URI));
        }
        return out;
    }

    @Override
    public @Nullable com.intellij.psi.impl.source.xml.SchemaPrefix getPrefixDeclaration(@NotNull XmlTag context, String namespacePrefix) {
        return null;
    }

    @Override
    public boolean isCustomTagAllowed(final XmlTag tag) {
        // Ensure inspections in HTML don't reject WebObjects tags/attributes
        // as "not allowed". Both the modern wo:* form and the legacy
        // <webobject> form are valid in component templates.
        PsiFile file = tag.getContainingFile();
        if (file == null) {
            return false;
        }
        if (!isAvailable(file)) {
            return false;
        }
        String prefix = tag.getNamespacePrefix();
        if (prefix == null || prefix.isEmpty()) {
            String qn = tag.getName();
            int colon = qn.indexOf(':');
            if (colon > 0) {
                prefix = qn.substring(0, colon);
            }
        }
        if (isLegacyWebObjectTag(tag)) {
            return true;
        }
        return "wo".equalsIgnoreCase(prefix);
    }

    @Override
    public boolean isSelfClosingTagAllowed(@NotNull XmlTag tag) {
        if (!isAvailable(tag.getContainingFile())) {
            return super.isSelfClosingTagAllowed(tag);
        }

        if (isLegacyWebObjectTag(tag)) {
            return true;
        }

        String prefix = tag.getNamespacePrefix();
        if (prefix == null || prefix.isEmpty()) {
            String qn = tag.getName();
            int colon = qn.indexOf(':');
            if (colon > 0) {
                prefix = qn.substring(0, colon);
            }
        }
        return "wo".equalsIgnoreCase(prefix) || super.isSelfClosingTagAllowed(tag);
    }

    @Override
    public boolean isCollapsibleTag(@NotNull XmlTag tag) {
        if (!isAvailable(tag.getContainingFile())) {
            return super.isCollapsibleTag(tag);
        }

        if (isLegacyWebObjectTag(tag)) {
            return true;
        }

        String prefix = tag.getNamespacePrefix();
        if (prefix == null || prefix.isEmpty()) {
            String qn = tag.getName();
            int colon = qn.indexOf(':');
            if (colon > 0) {
                prefix = qn.substring(0, colon);
            }
        }
        return "wo".equalsIgnoreCase(prefix) || super.isCollapsibleTag(tag);
    }

    private static boolean isLegacyWebObjectTag(@NotNull XmlTag tag) {
        // getLocalName() is not consistently populated for unprefixed HTML
        // tags; getName() is the value used by CheckEmptyTagInspection.
        return "webobject".equalsIgnoreCase(tag.getName())
                || "webobject".equalsIgnoreCase(tag.getLocalName());
    }

    private static @NotNull Set<String> collectWoInheritorNames(@NotNull XmlFile file) {
        Set<String> out = new LinkedHashSet<>();
        var project = file.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        PsiClass woComponent = JavaPsiFacade.getInstance(project).findClass(WO_COMPONENT_FQN, scope);
        PsiClass woElement = JavaPsiFacade.getInstance(project).findClass(WO_ELEMENT_FQN, scope);
        PsiClass woDynamicElement = JavaPsiFacade.getInstance(project).findClass(WO_DYNAMIC_ELEMENT_FQN, scope);

        addInheritors(out, woComponent, scope);
        addInheritors(out, woDynamicElement, scope);
        addInheritors(out, woElement, scope);

        return out;
    }

    private static void addInheritors(@NotNull Set<String> out, @Nullable PsiClass base, @NotNull GlobalSearchScope scope) {
        if (base == null) {
            return;
        }
        // Avoid Query.iterator() (scheduled for removal) by using Query.forEach().
        ClassInheritorsSearch.search(base, scope, true).forEach((Processor<PsiClass>) c -> {
            String name = c.getName();
            if (name != null && !name.isBlank()) {
                out.add(name);
            }
            return true;
        });
    }
}
