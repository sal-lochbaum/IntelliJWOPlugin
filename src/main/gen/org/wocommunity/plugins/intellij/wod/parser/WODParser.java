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
  // binding_name ASSIGN value SEMI
  public static boolean assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT, "<assignment>");
    r = binding_name(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, ASSIGN));
    r = p && report_error_(b, value(b, l + 1)) && r;
    r = p && consumeToken(b, SEMI) && r;
    exit_section_(b, l, m, r, p, WODParser::recover_assignment);
    return r || p;
  }

  /* ********************************************************** */
  // assignment*
  static boolean assignment_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_list")) return false;
    while (true) {
      int c = current_position_(b);
      if (!assignment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "assignment_list", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean binding_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binding_name")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, BINDING_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean component_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component_name")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, COMPONENT_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // element_name COLON component_name LBRACE assignment_list RBRACE
  public static boolean declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DECLARATION, "<declaration>");
    r = element_name(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, COLON));
    r = p && report_error_(b, component_name(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, LBRACE)) && r;
    r = p && report_error_(b, assignment_list(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, WODParser::recover_declaration);
    return r || p;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean element_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "element_name")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, ELEMENT_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER (DOT IDENTIFIER)*
  public static boolean key_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key_path")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && key_path_1(b, l + 1);
    exit_section_(b, m, KEY_PATH, r);
    return r;
  }

  // (DOT IDENTIFIER)*
  private static boolean key_path_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key_path_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!key_path_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "key_path_1", c)) break;
    }
    return true;
  }

  // DOT IDENTIFIER
  private static boolean key_path_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key_path_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // !(IDENTIFIER | RBRACE | SEMI)
  static boolean recover_assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_assignment")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recover_assignment_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // IDENTIFIER | RBRACE | SEMI
  private static boolean recover_assignment_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_assignment_0")) return false;
    boolean r;
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, SEMI);
    return r;
  }

  /* ********************************************************** */
  // !(IDENTIFIER | COLON | SEMI)
  static boolean recover_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recover_declaration_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // IDENTIFIER | COLON | SEMI
  private static boolean recover_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_declaration_0")) return false;
    boolean r;
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, COLON);
    if (!r) r = consumeToken(b, SEMI);
    return r;
  }

  /* ********************************************************** */
  // STRING | key_path | NUMBER
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE, "<value>");
    r = consumeToken(b, STRING);
    if (!r) r = key_path(b, l + 1);
    if (!r) r = consumeToken(b, NUMBER);
    exit_section_(b, l, m, r, false, null);
    return r;
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
