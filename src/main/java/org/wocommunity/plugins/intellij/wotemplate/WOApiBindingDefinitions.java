package org.wocommunity.plugins.intellij.wotemplate;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads WO binding definitions from classpath resources:
 * {@code Resources/<SimpleClassName>.api}
 *
 * These definitions have precedence over reflective member inspection.
 */
final class WOApiBindingDefinitions {

    private static final String RESOURCES_DIR = "Resources";

    private static final Map<String, Set<String>> CACHE_BY_VFILE_URL = new ConcurrentHashMap<>();

    private WOApiBindingDefinitions() {
    }

    static @NotNull Set<String> getBindingsForPsiClass(@NotNull PsiClass psiClass) {
        String simpleName = psiClass.getName();
        if (simpleName == null || simpleName.isBlank()) {
            return Collections.emptySet();
        }

        VirtualFile vFile = findApiVirtualFile(psiClass, simpleName);
        if (vFile == null) {
            return Collections.emptySet();
        }

        return CACHE_BY_VFILE_URL.computeIfAbsent(vFile.getUrl(), url -> loadBindingsFromApiFile(vFile));
    }

    private static VirtualFile findApiVirtualFile(@NotNull PsiClass psiClass, @NotNull String simpleName) {
        String relPath = RESOURCES_DIR + "/" + simpleName + ".api";
        String fileName = simpleName + ".api";
        Project project = psiClass.getProject();

        PsiFile containing = psiClass.getContainingFile();
        VirtualFile clsFile = containing != null ? containing.getVirtualFile() : null;

        Module module = ModuleUtilCore.findModuleForPsiElement(psiClass);

        if (clsFile != null) {
            // Prefer looking beside the class in the same JAR, if applicable.
            VirtualFile fromJar = findInSameJar(clsFile, relPath);
            if (fromJar != null) {
                return fromJar;
            }

            // If it's not a JAR class, it's part of the open project. In that case, prefer the unique
            // <SimpleName>.api found in the class' module (regardless of folder).
            if (!(clsFile.getFileSystem() instanceof JarFileSystem) && module != null && !DumbService.isDumb(project)) {
                // FilenameIndex is scope- and index-dependent. Start narrow, then expand.
                VirtualFile v = findUniqueApiByNameInScopes(fileName, relPath,
                        GlobalSearchScope.moduleScope(module),
                        GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module),
                        GlobalSearchScope.projectScope(project));
                if (v != null) {
                    return v;
                }
            }
        }

        // Fallback: look in module content roots (covers project sources + local resources).
        if (module != null) {
            for (VirtualFile root : ModuleRootManager.getInstance(module).getContentRoots()) {
                VirtualFile candidate = root.findFileByRelativePath(relPath);
                if (candidate != null && !candidate.isDirectory()) {
                    return candidate;
                }
                // Common Maven layout fallback:
                VirtualFile candidate2 = root.findFileByRelativePath("src/main/resources/" + relPath);
                if (candidate2 != null && !candidate2.isDirectory()) {
                    return candidate2;
                }
            }
        }

        return null;
    }

    private static VirtualFile findUniqueApiByNameInScopes(@NotNull String fileName,
                                                           @NotNull String relPath,
                                                           @NotNull GlobalSearchScope... scopes) {
        for (GlobalSearchScope scope : scopes) {
            var matches = FilenameIndex.getVirtualFilesByName(fileName, scope)
                    .stream()
                    .filter(v -> v != null && v.isValid() && !v.isDirectory())
                    .toList();
            if (matches.isEmpty()) {
                continue;
            }
            if (matches.size() == 1) {
                return matches.get(0);
            }
            // If multiple matches exist, prefer Resources/<SimpleName>.api if present.
            String preferredSuffix = "/" + relPath;
            for (VirtualFile v : matches) {
                if (v.getPath().endsWith(preferredSuffix)) {
                    return v;
                }
            }
        }
        return null;
    }

    private static VirtualFile findInSameJar(@NotNull VirtualFile classOrJavaFile, @NotNull String relPath) {
        if (!(classOrJavaFile.getFileSystem() instanceof JarFileSystem)) {
            return null;
        }
        VirtualFile localJar = JarFileSystem.getInstance().getVirtualFileForJar(classOrJavaFile);
        if (localJar == null) {
            return null;
        }
        VirtualFile jarRoot = JarFileSystem.getInstance().getJarRootForLocalFile(localJar);
        if (jarRoot == null) {
            return null;
        }
        VirtualFile candidate = jarRoot.findFileByRelativePath(relPath);
        return (candidate != null && !candidate.isDirectory()) ? candidate : null;
    }

    private static @NotNull Set<String> loadBindingsFromApiFile(@NotNull VirtualFile apiFile) {
        try (InputStream in = apiFile.getInputStream()) {
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            try {
                dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (Exception ignored) {
                // best-effort hardening
            }

            var doc = dbf.newDocumentBuilder().parse(in);
            var root = doc.getDocumentElement();
            if (root == null) {
                return Collections.emptySet();
            }

            Set<String> bindings = new LinkedHashSet<>();
            var bindingNodes = root.getElementsByTagName("binding");
            for (int j = 0; j < bindingNodes.getLength(); j++) {
                var bn = bindingNodes.item(j);
                if (!(bn instanceof org.w3c.dom.Element be)) {
                    continue;
                }
                String name = be.getAttribute("name");
                if (name != null && !name.isBlank()) {
                    bindings.add(name.trim());
                }
            }
            return bindings.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(bindings);
        } catch (Exception ignored) {
            return Collections.emptySet();
        }
    }
}

