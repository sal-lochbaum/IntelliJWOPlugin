// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.psi.wod.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.wocommunity.plugins.intellij.psi.wod.WODTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.wocommunity.plugins.intellij.psi.wod.*;

public class WODAssignmentImpl extends ASTWrapperPsiElement implements WODAssignment {

  public WODAssignmentImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public WODAssignmentComment getWODAssignmentComment() {
    return findChildByClass(WODAssignmentComment.class);
  }

  @Override
  @NotNull
  public WODBinding getWODBinding() {
    return findNotNullChildByClass(WODBinding.class);
  }

  @Override
  @Nullable
  public WODValue getWODValue() {
    return findChildByClass(WODValue.class);
  }

}
