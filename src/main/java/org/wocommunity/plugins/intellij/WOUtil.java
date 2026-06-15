package org.wocommunity.plugins.intellij;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class WOUtil {

    public static boolean fileIsComponent(@NotNull VirtualFile file) {
        return file.getName().endsWith(".wo");
    }

    public static boolean fileIsPartOfComponent(@NotNull VirtualFile file) {
        return fileIsComponentTemplate(file) || fileIsComponentDeclaration(file) || fileIsComponentApi(file);
    }

    public static boolean fileIsComponentTemplate(@NotNull VirtualFile file) {
        return file.getName().endsWith(".html") && fileIsComponent(file.getParent());
    }

    public static boolean fileIsComponentDeclaration(@NotNull VirtualFile file) {
        return file.getName().endsWith(".wod") && fileIsComponent(file.getParent());
    }

    public static boolean fileIsComponentApi(@NotNull VirtualFile file) {
        return file.getName().endsWith(".api") && fileIsComponent(file.getParent());
    }

}
