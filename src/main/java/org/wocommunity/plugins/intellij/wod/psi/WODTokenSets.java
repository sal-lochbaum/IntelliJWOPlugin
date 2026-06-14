package org.wocommunity.plugins.intellij.wod.psi;

import com.intellij.psi.tree.TokenSet;

public interface WODTokenSets {
    TokenSet COMMENTS = TokenSet.create(WODTypes.COMMENT);
    TokenSet LITERALS = TokenSet.create(WODTypes.STRING);
}
