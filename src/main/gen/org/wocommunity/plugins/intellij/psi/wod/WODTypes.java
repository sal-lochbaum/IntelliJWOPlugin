// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.psi.wod;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.wocommunity.plugins.intellij.psi.wod.impl.*;

public interface WODTypes {

  IElementType WOD_ASSIGNMENT = new WODElementType("WOD_ASSIGNMENT");
  IElementType WOD_ASSIGNMENT_COMMENT = new WODElementType("WOD_ASSIGNMENT_COMMENT");
  IElementType WOD_ASSIGNMENT_LIST = new WODElementType("WOD_ASSIGNMENT_LIST");
  IElementType WOD_BINDING = new WODElementType("WOD_BINDING");
  IElementType WOD_COMPONENT = new WODElementType("WOD_COMPONENT");
  IElementType WOD_DECLARATION = new WODElementType("WOD_DECLARATION");
  IElementType WOD_ELEMENT = new WODElementType("WOD_ELEMENT");
  IElementType WOD_KEY_PATH = new WODElementType("WOD_KEY_PATH");
  IElementType WOD_KEY_PATH_ELEMENT = new WODElementType("WOD_KEY_PATH_ELEMENT");
  IElementType WOD_PARENT_BINDING = new WODElementType("WOD_PARENT_BINDING");
  IElementType WOD_VALUE = new WODElementType("WOD_VALUE");

  IElementType ASSIGN = new WODTokenType("=");
  IElementType ASSIGNMENT_COMMENT = new WODTokenType("ASSIGNMENT_COMMENT");
  IElementType BOOLEAN = new WODTokenType("BOOLEAN");
  IElementType COLON = new WODTokenType(":");
  IElementType CARET = new WODTokenType("^");
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
      if (type == WOD_ASSIGNMENT) {
        return new WODAssignmentImpl(node);
      }
      else if (type == WOD_ASSIGNMENT_COMMENT) {
        return new WODAssignmentCommentImpl(node);
      }
      else if (type == WOD_ASSIGNMENT_LIST) {
        return new WODAssignmentListImpl(node);
      }
      else if (type == WOD_BINDING) {
        return new WODBindingImpl(node);
      }
      else if (type == WOD_COMPONENT) {
        return new WODComponentImpl(node);
      }
      else if (type == WOD_DECLARATION) {
        return new WODDeclarationImpl(node);
      }
      else if (type == WOD_ELEMENT) {
        return new WODElementImpl(node);
      }
      else if (type == WOD_KEY_PATH) {
        return new WODKeyPathImpl(node);
      }
      else if (type == WOD_KEY_PATH_ELEMENT) {
        return new WODKeyPathElementImpl(node);
      }
      else if (type == WOD_PARENT_BINDING) {
        return new WODParentBindingImpl(node);
      }
      else if (type == WOD_VALUE) {
        return new WODValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
