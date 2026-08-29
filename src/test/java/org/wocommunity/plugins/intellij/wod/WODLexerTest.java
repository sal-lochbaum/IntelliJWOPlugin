package org.wocommunity.plugins.intellij.wod;

import com.intellij.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.wocommunity.plugins.intellij.psi.wod.WODTypes.BOOLEAN;

class WODLexerTest {
    @Test
    void lexesBooleanLiterals() {
        Lexer lexer = new WODLexerAdapter();
        lexer.start("true;false");

        assertEquals(BOOLEAN, lexer.getTokenType());
        assertEquals("true", lexer.getTokenText());
        lexer.advance();
        assertEquals(";", lexer.getTokenText());
        lexer.advance();
        assertEquals(BOOLEAN, lexer.getTokenType());
        assertEquals("false", lexer.getTokenText());
    }

    @Test
    void keepsBooleanPrefixesInIdentifiers() {
        Lexer lexer = new WODLexerAdapter();
        lexer.start("trueValue");

        assertToken(lexer, "trueValue");
        assertNull(lexer.getTokenType());
    }

    private static void assertToken(Lexer lexer, String text) {
        assertEquals(text, lexer.getTokenText());
        lexer.advance();
    }
}
