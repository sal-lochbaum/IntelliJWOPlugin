package org.wocommunity.plugins.intellij.wod.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.wod.WODFileType;
import org.wocommunity.plugins.intellij.wod.WODLanguage;

public class WODFile extends PsiFileBase {
    public WODFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, WODLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return WODFileType.INSTANCE;
    }
}
