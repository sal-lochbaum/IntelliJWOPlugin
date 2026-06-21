package org.wocommunity.plugins.intellij.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.WOPsiUtil;
import org.wocommunity.plugins.intellij.psi.wod.WODComponent;

public class JavaClassReference extends PsiReferenceBase<PsiElement> {
    public JavaClassReference(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    public @Nullable PsiElement resolve() {
        if (myElement instanceof WODComponent myComponent) {
            String className = myComponent.getIdentifier().getText();
            if (className.isEmpty()) {
                return null;
            }

            try {
                return WOPsiUtil.getPsiClassForComponentName(className, myComponent.getProject());
            } catch (Exception e) {
                // TODO: Muss man die Fangen, weiter werfen o.ä.?
            }
        }

        return null;
    }
}
