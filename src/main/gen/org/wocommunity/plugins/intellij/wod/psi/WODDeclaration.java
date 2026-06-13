// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.wod.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WODDeclaration extends PsiElement {

  @NotNull
  List<WODAssignment> getAssignmentList();

  @Nullable
  WODComponentName getComponentName();

  @NotNull
  WODElementName getElementName();

}
