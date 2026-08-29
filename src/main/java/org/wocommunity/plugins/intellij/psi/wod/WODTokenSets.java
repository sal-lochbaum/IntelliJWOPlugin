package org.wocommunity.plugins.intellij.psi.wod;

import com.intellij.psi.tree.TokenSet;

public interface WODTokenSets {
    TokenSet COMMENTS = TokenSet.create(WODTypes.COMMENT);
    TokenSet LITERALS = TokenSet.create(WODTypes.STRING, WODTypes.BOOLEAN);
}
