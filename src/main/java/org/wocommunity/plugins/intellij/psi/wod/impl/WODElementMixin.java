package org.wocommunity.plugins.intellij.psi.wod.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.psi.references.WODElementReference;
import org.wocommunity.plugins.intellij.psi.references.WODKeyPathReference;
import org.wocommunity.plugins.intellij.psi.wod.WODElement;

abstract public class WODElementMixin extends ASTWrapperPsiElement implements WODElement {
    public WODElementMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiReference getReference() {
        return new WODElementReference(this);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return new PsiReference[]{getReference()};
    }
}
