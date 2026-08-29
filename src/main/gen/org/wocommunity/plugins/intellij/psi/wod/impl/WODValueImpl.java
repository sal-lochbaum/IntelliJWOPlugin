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

public class WODValueImpl extends ASTWrapperPsiElement implements WODValue {

  public WODValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public WODKeyPath getWODKeyPath() {
    return findChildByClass(WODKeyPath.class);
  }

  @Override
  @Nullable
  public WODParentBinding getWODParentBinding() {
    return findChildByClass(WODParentBinding.class);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getBoolean() {
    return findChildByType(BOOLEAN);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(STRING);
  }

}
