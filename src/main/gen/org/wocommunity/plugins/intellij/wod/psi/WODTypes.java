// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.wod.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.wocommunity.plugins.intellij.wod.psi.impl.*;

public interface WODTypes {

  IElementType ASSIGNMENT = new WODElementType("ASSIGNMENT");
  IElementType ASSIGNMENT_LIST = new WODElementType("ASSIGNMENT_LIST");
  IElementType DECLARATION = new WODElementType("DECLARATION");
  IElementType WOD_BINDING = new WODElementType("WOD_BINDING");
  IElementType WOD_COMPONENT = new WODElementType("WOD_COMPONENT");
  IElementType WOD_ELEMENT = new WODElementType("WOD_ELEMENT");
  IElementType WOD_KEY_PATH = new WODElementType("WOD_KEY_PATH");
  IElementType WOD_VALUE = new WODElementType("WOD_VALUE");

  IElementType ASSIGN = new WODTokenType("=");
  IElementType COLON = new WODTokenType(":");
  IElementType COMMENT = new WODTokenType("COMMENT");
  IElementType DOT = new WODTokenType(".");
  IElementType IDENTIFIER = new WODTokenType("IDENTIFIER");
  IElementType LBRACE = new WODTokenType("{");
  IElementType NUMBER = new WODTokenType("NUMBER");
  IElementType RBRACE = new WODTokenType("}");
  IElementType SEMI = new WODTokenType(";");
  IElementType STRING = new WODTokenType("STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ASSIGNMENT) {
        return new AssignmentImpl(node);
      }
      else if (type == ASSIGNMENT_LIST) {
        return new AssignmentListImpl(node);
      }
      else if (type == DECLARATION) {
        return new DeclarationImpl(node);
      }
      else if (type == WOD_BINDING) {
        return new WODBindingImpl(node);
      }
      else if (type == WOD_COMPONENT) {
        return new WODComponentImpl(node);
      }
      else if (type == WOD_ELEMENT) {
        return new WODElementImpl(node);
      }
      else if (type == WOD_KEY_PATH) {
        return new WODKeyPathImpl(node);
      }
      else if (type == WOD_VALUE) {
        return new WODValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
