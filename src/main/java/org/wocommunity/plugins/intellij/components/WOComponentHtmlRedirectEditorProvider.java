package org.wocommunity.plugins.intellij.components;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Redirects opening {@code *.wo/*.html} to the {@link WOComponentEditor} of the parent {@code .wo} folder.
 *
 * This ensures that opening a search result inside a WO component template uses the custom component editor
 * instead of the plain HTML editor.
 */
public final class WOComponentHtmlRedirectEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if (file.isDirectory()) {
            return false;
        }
        if (!file.getName().endsWith(".html")) {
            return false;
        }
        VirtualFile parent = file.getParent();
        return parent != null && parent.isDirectory() && parent.getName().endsWith(".wo");
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        VirtualFile folder = file.getParent();
        if (folder == null) {
            Notifications.Bus.notify(new Notification(
                    "WOComponent", "WO Component not found",
                    "Cannot open WO component editor because the parent folder is missing.",
                    NotificationType.WARNING
            ));
            return null;
        }
        try {
            return new WOComponentEditor(project, folder);
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification(
                    "WOComponent", "Cannot open WO Component",
                    folder.getName() + " cannot be opened: " + e.getMessage(),
                    NotificationType.WARNING
            ));
            return null;
        }
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return "WOComponentHtmlRedirectEditor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}

