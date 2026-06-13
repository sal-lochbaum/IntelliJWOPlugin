package org.wocommunity.plugins.intellij.wod.psi;

import com.intellij.psi.tree.TokenSet;

public interface WODTokenSets {
    TokenSet COMMENTS = TokenSet.create(WODTypes.COMMENT);
    TokenSet ELEMENT_NAMES = TokenSet.create(WODTypes.ELEMENT_NAME);
    TokenSet COMPONENT_NAMES = TokenSet.create(WODTypes.COMPONENT_NAME);
    TokenSet BINDING_NAMES = TokenSet.create(WODTypes.BINDING_NAME);
    TokenSet VALUES = TokenSet.create(WODTypes.VALUE);
    TokenSet LITERALS = TokenSet.create(WODTypes.STRING);
}
