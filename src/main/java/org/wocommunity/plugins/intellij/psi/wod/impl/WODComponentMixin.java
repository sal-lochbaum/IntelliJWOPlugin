package org.wocommunity.plugins.intellij.psi.wod.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.psi.references.WODComponentReference;
import org.wocommunity.plugins.intellij.psi.references.WODElementReference;
import org.wocommunity.plugins.intellij.psi.wod.WODComponent;
import org.wocommunity.plugins.intellij.psi.wod.WODElement;

abstract public class WODComponentMixin extends ASTWrapperPsiElement implements WODComponent {
    public WODComponentMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiReference getReference() {
        return new WODComponentReference(this);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return new PsiReference[]{getReference()};
    }
}
