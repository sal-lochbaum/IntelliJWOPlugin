package org.wocommunity.plugins.intellij.wod.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.wod.WODLanguage;

public class WODElementType extends IElementType {
    public WODElementType(@NonNls @NotNull String debugName) {
        super(debugName, WODLanguage.INSTANCE);
    }
}
