// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.psi.wod.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.wocommunity.plugins.intellij.psi.wod.WODTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.wocommunity.plugins.intellij.psi.wod.*;

public class WODBindingImpl extends ASTWrapperPsiElement implements WODBinding {

  public WODBindingImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitWODBinding(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
