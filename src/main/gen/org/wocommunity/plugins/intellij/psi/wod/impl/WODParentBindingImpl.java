// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.psi.wod.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.psi.wod.WODParentBinding;

import static org.wocommunity.plugins.intellij.psi.wod.WODTypes.IDENTIFIER;

public class WODParentBindingImpl extends ASTWrapperPsiElement implements WODParentBinding {

  public WODParentBindingImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }
}
