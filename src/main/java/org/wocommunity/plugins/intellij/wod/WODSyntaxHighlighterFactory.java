package org.wocommunity.plugins.intellij.wod;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

final class WODSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @Override
    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
        return new WODSyntaxHighlighter();
    }
}
