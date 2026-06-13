// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.wod.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.wocommunity.plugins.intellij.wod.psi.impl.*;

public interface WODTypes {

  IElementType ASSIGNMENT = new WODElementType("ASSIGNMENT");
  IElementType BINDING_NAME = new WODElementType("BINDING_NAME");
  IElementType COMPONENT_NAME = new WODElementType("COMPONENT_NAME");
  IElementType DECLARATION = new WODElementType("DECLARATION");
  IElementType ELEMENT_NAME = new WODElementType("ELEMENT_NAME");
  IElementType KEY_PATH = new WODElementType("KEY_PATH");
  IElementType VALUE = new WODElementType("VALUE");

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
        return new WODAssignmentImpl(node);
      }
      else if (type == BINDING_NAME) {
        return new WODBindingNameImpl(node);
      }
      else if (type == COMPONENT_NAME) {
        return new WODComponentNameImpl(node);
      }
      else if (type == DECLARATION) {
        return new WODDeclarationImpl(node);
      }
      else if (type == ELEMENT_NAME) {
        return new WODElementNameImpl(node);
      }
      else if (type == KEY_PATH) {
        return new WODKeyPathImpl(node);
      }
      else if (type == VALUE) {
        return new WODValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
