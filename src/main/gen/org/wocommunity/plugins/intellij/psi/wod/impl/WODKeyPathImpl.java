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

public class WODKeyPathImpl extends ASTWrapperPsiElement implements WODKeyPath {

  public WODKeyPathImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<WODKeyPathElement> getWODKeyPathElementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WODKeyPathElement.class);
  }

}
