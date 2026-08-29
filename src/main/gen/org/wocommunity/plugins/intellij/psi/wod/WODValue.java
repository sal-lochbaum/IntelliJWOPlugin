// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.psi.wod;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WODValue extends PsiElement {

  @Nullable
  WODKeyPath getWODKeyPath();

  @Nullable
  WODParentBinding getWODParentBinding();

  @Nullable
  PsiElement getNumber();

  @Nullable
  PsiElement getBoolean();

  @Nullable
  PsiElement getString();

}
