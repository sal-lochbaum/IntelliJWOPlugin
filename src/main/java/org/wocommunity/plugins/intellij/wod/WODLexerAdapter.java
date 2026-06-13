package org.wocommunity.plugins.intellij.wod;

import com.intellij.lexer.FlexAdapter;

public class WODLexerAdapter extends FlexAdapter {
    public WODLexerAdapter() {
        super(new _WODLexer(null));
    }
}
