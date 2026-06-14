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

public class DeclarationImpl extends ASTWrapperPsiElement implements Declaration {

  public DeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WODComponent getWODComponent() {
    return findChildByClass(WODComponent.class);
  }

  @Override
  @NotNull
  public WODElement getWODElement() {
    return findNotNullChildByClass(WODElement.class);
  }

  @Override
  @Nullable
  public AssignmentList getAssignmentList() {
    return findChildByClass(AssignmentList.class);
  }

}
