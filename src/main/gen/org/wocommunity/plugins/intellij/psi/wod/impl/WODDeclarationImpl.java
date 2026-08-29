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

public class WODDeclarationImpl extends ASTWrapperPsiElement implements WODDeclaration {

  public WODDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public WODAssignmentList getWODAssignmentList() {
    return findChildByClass(WODAssignmentList.class);
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

}
