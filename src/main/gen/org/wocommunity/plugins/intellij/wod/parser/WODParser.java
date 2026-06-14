// This is a generated file. Not intended for manual editing.
package org.wocommunity.plugins.intellij.wod.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.wocommunity.plugins.intellij.wod.psi.WODTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class WODParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return wodFile(b, l + 1);
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean WODBinding(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WODBinding")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, WOD_BINDING, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean WODComponent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WODComponent")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, WOD_COMPONENT, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean WODElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WODElement")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, WOD_ELEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER (DOT IDENTIFIER)*
  public static boolean WODKeyPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WODKeyPath")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && WODKeyPath_1(b, l + 1);
    exit_section_(b, m, WOD_KEY_PATH, r);
    return r;
  }

  // (DOT IDENTIFIER)*
  private static boolean WODKeyPath_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WODKeyPath_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!WODKeyPath_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "WODKeyPath_1", c)) break;
    }
    return true;
  }

  // DOT IDENTIFIER
  private static boolean WODKeyPath_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WODKeyPath_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STRING | WODKeyPath | NUMBER
  public static boolean WODValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WODValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WOD_VALUE, "<wod value>");
    r = consumeToken(b, STRING);
    if (!r) r = WODKeyPath(b, l + 1);
    if (!r) r = consumeToken(b, NUMBER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // WODBinding ASSIGN WODValue SEMI
  public static boolean assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT, null);
    r = WODBinding(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, ASSIGN));
    r = p && report_error_(b, WODValue(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // assignment *
  public static boolean assignment_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_list")) return false;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT_LIST, "<assignment list>");
    while (true) {
      int c = current_position_(b);
      if (!assignment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "assignment_list", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // WODElement COLON WODComponent LBRACE assignment_list RBRACE
  public static boolean declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DECLARATION, null);
    r = WODElement(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, COLON));
    r = p && report_error_(b, WODComponent(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, LBRACE)) && r;
    r = p && report_error_(b, assignment_list(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // declaration *
  static boolean wodFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "wodFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!declaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "wodFile", c)) break;
    }
    return true;
  }

}
