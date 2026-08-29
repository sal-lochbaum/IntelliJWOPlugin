package org.wocommunity.plugins.intellij.psi.wod.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.psi.references.WODKeyPathReference;
import org.wocommunity.plugins.intellij.psi.wod.WODKeyPathElement;

abstract public class WODKeyPathElementMixin extends ASTWrapperPsiElement implements WODKeyPathElement {

    protected WODKeyPathElementMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull PsiReference getReference() {
        return new WODKeyPathReference(this);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return new PsiReference[]{getReference()};
    }

}