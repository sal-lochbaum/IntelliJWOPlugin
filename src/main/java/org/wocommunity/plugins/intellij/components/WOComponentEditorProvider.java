package org.wocommunity.plugins.intellij.components;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.tools.WOFileUtil;

import java.io.IOException;

public class WOComponentEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        // Accept .wo folders or files inside a .wo folder
        return WOFileUtil.fileIsComponent(file) || WOFileUtil.fileIsInComponent(file);
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        try {
            VirtualFile component = WOFileUtil.getComponent(file);
            if (component != null) {
                return new WOComponentEditor(project, component, file);
            }
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification(
                    "WOComponent", "File Not Found",
                    file.getName() + " cannot be openend: " + e.getMessage(),
                    NotificationType.WARNING
            ));
        }

        return TextEditorProvider.getInstance().createEditor(project, file);
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return "WOFolderEditor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
