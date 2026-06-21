package org.wocommunity.plugins.intellij.psi.wod;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.wod.WODLanguage;

public class WODTokenType extends IElementType {
    public WODTokenType(@NonNls @NotNull String debugName) {
        super(debugName, WODLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "WODTokenType." + super.toString();
    }
}
