// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.wod.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.wocommunity.plugins.intellij.wod.psi.WODTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.wocommunity.plugins.intellij.wod.psi.*;

public class WODValueImpl extends ASTWrapperPsiElement implements WODValue {

  public WODValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WODVisitor visitor) {
    visitor.visitValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WODVisitor) accept((WODVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WODKeyPath getKeyPath() {
    return findChildByClass(WODKeyPath.class);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(STRING);
  }

}
