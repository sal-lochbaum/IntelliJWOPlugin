package org.wocommunity.plugins.intellij.psi.wod.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.psi.wod.WODElement;

abstract public class WODElementMixin extends ASTWrapperPsiElement implements WODElement {
    public WODElementMixin(@NotNull ASTNode node) {
        super(node);
    }
}
