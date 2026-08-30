package org.wocommunity.plugins.intellij.components;

import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.tools.WOIcons;
import org.wocommunity.plugins.intellij.tools.WOFileUtil;

public class WOComponentEditorTabTitleProvider implements EditorTabTitleProvider {
    @Override
    public @Nullable String getEditorTabTitle(@NotNull Project project, @NotNull VirtualFile file) {
        if (file.getName().endsWith(".wo"))
            return file.getNameWithoutExtension();

        VirtualFile component = WOFileUtil.getComponent(file);
        if (component != null) {
            return component.getNameWithoutExtension();
        }

        return null; // Return null to use the default title
    }
}
