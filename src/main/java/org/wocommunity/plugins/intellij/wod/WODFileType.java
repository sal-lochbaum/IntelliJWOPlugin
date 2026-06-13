package org.wocommunity.plugins.intellij.wod;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.tools.WOIcons;

import javax.swing.*;

public class WODFileType extends LanguageFileType {
    public static final WODFileType INSTANCE = new WODFileType();

    public WODFileType() {
        super(WODLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Web Object Declaration";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "WebObjects file declaring Web Objects used in the templates and declaring their bindings.";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "wod";
    }

    @Override
    public @Nullable Icon getIcon() {
        return WOIcons.WOCOMPONENT_ICON;
    }
}
