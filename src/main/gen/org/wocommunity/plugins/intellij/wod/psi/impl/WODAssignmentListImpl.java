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

public class WODAssignmentListImpl extends ASTWrapperPsiElement implements WODAssignmentList {

  public WODAssignmentListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitWODAssignmentList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<WODAssignment> getWODAssignmentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WODAssignment.class);
  }

}
