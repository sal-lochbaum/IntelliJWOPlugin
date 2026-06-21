package org.wocommunity.plugins.intellij.wod;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.wod.parser.WODParser;
import org.wocommunity.plugins.intellij.psi.wod.WODFile;
import org.wocommunity.plugins.intellij.psi.wod.WODTokenSets;
import org.wocommunity.plugins.intellij.psi.wod.WODTypes;

public class WODParserDefinition implements ParserDefinition {
public static final IFileElementType FILE = new IFileElementType(WODLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new WODLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new WODParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return WODTokenSets.COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return WODTokenSets.LITERALS;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode astNode) {
        return WODTypes.Factory.createElement(astNode);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
        return new WODFile(fileViewProvider);
    }
}
