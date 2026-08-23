package org.wocommunity.plugins.intellij.wod;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.wocommunity.plugins.intellij.tools.KeyValueCodingUtil;
import org.wocommunity.plugins.intellij.tools.WOPsiUtil;
import org.wocommunity.plugins.intellij.psi.api.APIFile;
import org.wocommunity.plugins.intellij.psi.wod.*;
import org.wocommunity.plugins.intellij.wotemplate.WOSystemBindingDefinitions;

import java.util.Set;

public class WODCompletionContributor extends CompletionContributor {
    public WODCompletionContributor() {
        // Add autocompletion for WOD tags
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        // parameters: position, originalfile, completiontype, editor
                        // parameters.position = WODTokenType.IDENTIFIER
                        // parameters.position: parent CompositeElement mit type z.B. binding
                        // context: ? singleKye, singleValue, myMap, mySharedContext
                        // result: prefixMatcher, ...

                        PsiElement position = parameters.getPosition();
                        if (position instanceof LeafPsiElement leaf && leaf.getElementType() == WODTypes.IDENTIFIER) {
                            PsiElement parent = position.getParent();
                            switch (parent) {
                                case WODElement element -> completeIdentifier4Element(element, parameters, context, result);
                                case WODComponent component -> completeIdentifier4Component(component, parameters, context, result);
                                case WODBinding binding -> completeIdentifier4Binding(binding, parameters, context, result);
                                case WODKeyPath keyPath -> completeIdentifier4KeyPath(keyPath, parameters, context, result);
                                case WODKeyPathElement keyPath -> completeIdentifier4KeyPath((WODKeyPath)keyPath.getParent(), parameters, context, result);
                                default -> {System.out.println("Unknown parent type: " + parent.getClass().getName());}
                            }
                        }
                    }
                }
        );
    }

    protected void completeIdentifier4Element(@NotNull WODElement element,
                                              @NotNull CompletionParameters parameters,
                                              @NotNull ProcessingContext context,
                                              @NotNull CompletionResultSet result) {

    }

    protected void completeIdentifier4Component(@NotNull WODComponent component,
                                                @NotNull CompletionParameters parameters,
                                                @NotNull ProcessingContext context,
                                                @NotNull CompletionResultSet result) {
        result.addElement(LookupElementBuilder.create("ERXConditional"));
        result.addElement(LookupElementBuilder.create("ERXElse"));
    }

    protected void completeIdentifier4Binding(@NotNull WODBinding binding,
                                              @NotNull CompletionParameters parameters,
                                              @NotNull ProcessingContext context,
                                              @NotNull CompletionResultSet result) {
        // Determine Classname
        WODAssignment wodAss = (WODAssignment) binding.getParent();
        WODAssignmentList wodList = (WODAssignmentList) wodAss.getParent();
        WODDeclaration wodDeclaration = (WODDeclaration) wodList.getParent();
        WODComponent wodComponent = wodDeclaration.getWODComponent();
        if (wodComponent == null) {
            return;
        }

        // WO Core
        Set<WOSystemBindingDefinitions.Binding> systemBinding = WOSystemBindingDefinitions.getBindingsForShortClassName(wodComponent.getIdentifier().getText());
        if (!systemBinding.isEmpty()) {
            systemBinding.forEach(sysBinding -> result.addElement(LookupElementBuilder.create(sysBinding.name)));
        }
        else {
            // Component with .api file
            PsiDirectory componentFolder = WOPsiUtil.getComponentFolder(wodComponent);
            if (componentFolder == null) {
                return;
            }
            APIFile api = WOPsiUtil.getApiFile(componentFolder);
            if (api != null) {
                api.getBindingNames().forEach(name -> result.addElement(LookupElementBuilder.create(name)));
            }
        }
    }

    protected void completeIdentifier4KeyPath(@NotNull WODKeyPath keyPath,
                                              @NotNull CompletionParameters parameters,
                                              @NotNull ProcessingContext context,
                                              @NotNull CompletionResultSet result) {
        PsiClass baseClass = WOPsiUtil.getPsiClass(keyPath);
        PsiClass lastClass = KeyValueCodingUtil.resolveWODKeyPath(baseClass, keyPath, true);
        if (lastClass == null) {
            return;
        }
        KeyValueCodingUtil.addCompletionSuggestions(result, lastClass);
    }
}
