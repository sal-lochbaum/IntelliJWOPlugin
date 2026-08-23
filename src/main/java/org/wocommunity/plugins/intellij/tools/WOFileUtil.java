package org.wocommunity.plugins.intellij.tools;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class WOFileUtil {

    public static final String COMPONENT_EXTENSION = "wo";
    public static final String TEMPLATE_EXTENSION = "html";
    public static final String DECLARATION_EXTENSION = "wod";
    public static final String API_EXTENSION = "api";
    public static final String WOO_EXTENSION = "woo";

    public static boolean fileIsComponent(@NotNull VirtualFile file) {
        return COMPONENT_EXTENSION.equalsIgnoreCase(file.getExtension());
    }

    public static boolean fileIsInComponent(@NotNull VirtualFile file) {
        return fileIsComponentTemplate(file) || fileIsComponentDeclaration(file) || fileIsComponentApi(file) || fileIsComponentWoo(file);
    }

    public static boolean fileIsComponentTemplate(@NotNull VirtualFile file) {
        return TEMPLATE_EXTENSION.equalsIgnoreCase(file.getExtension()) && fileIsComponent(file.getParent());
    }

    public static boolean fileIsComponentDeclaration(@NotNull VirtualFile file) {
        return DECLARATION_EXTENSION.equalsIgnoreCase(file.getExtension()) && fileIsComponent(file.getParent());
    }

    public static boolean fileIsComponentApi(@NotNull VirtualFile file) {
        return API_EXTENSION.equalsIgnoreCase(file.getExtension()) && fileIsComponent(file.getParent());
    }

    public static boolean fileIsComponentWoo(@NotNull VirtualFile file) {
        return WOO_EXTENSION.equalsIgnoreCase(file.getExtension()) && fileIsComponent(file.getParent());
    }

    public static String getComponentName(@NotNull VirtualFile file) {
        VirtualFile component = getComponent(file);
        if (component != null) {
            return component.getNameWithoutExtension();
        }
        return null;
    }

    public static VirtualFile getComponent(@NotNull VirtualFile file) {
        if (fileIsComponent(file)) {
            return file;
        }
        if (fileIsInComponent(file)) {
            return file.getParent();
        }
        return null;
    }

    public static VirtualFile getTemplate(@NotNull VirtualFile file) {
        if (fileIsComponentTemplate(file)) {
            return file;
        }

        VirtualFile component = getComponent(file);
        if (component != null) {
            String componentName = component.getNameWithoutExtension();
            return component.findChild(componentName + "." + TEMPLATE_EXTENSION);
        }
        return null;
    }

    public static VirtualFile getDeclaration(@NotNull VirtualFile file) {
        if (fileIsComponentDeclaration(file)) {
            return file;
        }

        VirtualFile component = getComponent(file);
        if (component != null) {
            String componentName = component.getNameWithoutExtension();
            return component.findChild(componentName + "." + DECLARATION_EXTENSION);
        }
        return null;
    }

    public static VirtualFile getApi(@NotNull VirtualFile file) {
        if (fileIsComponentApi(file)) {
            return file;
        }

        VirtualFile component = getComponent(file);
        if (component != null) {
            String componentName = component.getNameWithoutExtension();
            return component.findChild(componentName + "." + API_EXTENSION);
        }
        return null;
    }

    public static VirtualFile getWoo(@NotNull VirtualFile file) {
        if (fileIsComponentWoo(file)) {
            return file;
        }

        VirtualFile component = getComponent(file);
        if (component != null) {
            String componentName = component.getNameWithoutExtension();
            return component.findChild(componentName + "." + WOO_EXTENSION);
        }
        return null;
    }
}
