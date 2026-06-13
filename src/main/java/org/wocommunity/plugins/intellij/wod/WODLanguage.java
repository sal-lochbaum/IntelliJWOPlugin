package org.wocommunity.plugins.intellij.wod;

import com.intellij.lang.Language;

public class WODLanguage extends Language {
    public static final String ID = "WOD";

    public static final WODLanguage INSTANCE = new WODLanguage();

    public WODLanguage() {
        super(ID);
    }
}
