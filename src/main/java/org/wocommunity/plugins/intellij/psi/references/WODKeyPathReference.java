package org.wocommunity.plugins.intellij.psi.references;

import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.psi.wod.WODKeyPathElement;
import org.wocommunity.plugins.intellij.psi.wod.impl.WODKeyPathElementMixin;
import org.wocommunity.plugins.intellij.tools.KeyValueCodingUtil;

public final class WODKeyPathReference extends PsiReferenceBase<WODKeyPathElement> {

    public WODKeyPathReference(@NotNull WODKeyPathElement element) {
        super(element, ElementManipulators.getValueTextRange(element));
    }

    @Override
    public @Nullable PsiElement resolve() {
        try {
            return KeyValueCodingUtil.resolveKeyPathElement(myElement);
        } catch (Exception e) {
            return null;
        }
    }
}