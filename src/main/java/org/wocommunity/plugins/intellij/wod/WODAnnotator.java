package org.wocommunity.plugins.intellij.wod;

import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.WOPsiUtil;
import org.wocommunity.plugins.intellij.psi.api.APIFile;
import org.wocommunity.plugins.intellij.psi.wod.*;
import org.wocommunity.plugins.intellij.wotemplate.WOSystemBindingDefinitions;

import java.util.*;

/*
 * An Annotator helps highlight and annotate any code based on specific rules.
 *
 * Annotate the different usages of IDENTIFIER tokens
 */
final class WODAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        switch (psiElement) {
            case WODElement element -> annotateElement(element, holder);
            case WODComponent component -> annotateComponent(component, holder);
            case WODBinding binding -> annotateBinding(binding, holder);
            case WODKeyPath keyPath -> annotateKeyPath(keyPath, holder);
            default -> {}
        }
    }

    private WODFile getWODFile(PsiElement element) {
        PsiElement iterElement = element;
        while (iterElement != null && !(iterElement instanceof WODFile)) {
            iterElement = iterElement.getParent();
        }
        return (WODFile) iterElement;
    }

    private WODDeclaration getDeclaration(PsiElement element) {
        if (element.getParent() instanceof WODDeclaration) {
            return (WODDeclaration) element.getParent();
        }
        return null;
    }
    private WODComponent getComponent(PsiElement element) {
        if (element.getParent() instanceof WODDeclaration) {
            return ((WODDeclaration) element.getParent()).getWODComponent();
        }
        return null;
    }

    private void annotateElement(@NotNull WODElement element, @NotNull AnnotationHolder holder) {
        WODDeclaration declaration = getDeclaration(element);
        WODComponent component = getComponent(element);
        if (declaration == null || component == null) {
            return;
        }

        // Normal color
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element)
                .textAttributes(TextAttributesKey.find("HTML_ATTRIBUTE_NAME"))
                .create();

        // Find out which elements are named in the HTML file and match against the identifier of this element
        //       -> Identifier is not used => weak warning
        List<String> namesFromHtml = getNamesFromHTMLTemplate(element.getContainingFile());
        if (namesFromHtml.stream().noneMatch(element.getIdentifier().getText()::equals)) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "There is no element named '" + element.getIdentifier().getText() + "' in your component HTML file")
                    .range(element)
                    .create();
        }

        // Check duplicate identifiers => error
        PsiElement previousDeclaration = declaration.getPrevSibling();
        while (previousDeclaration != null) {
            if (previousDeclaration instanceof WODDeclaration && ((WODDeclaration) previousDeclaration).getWODElement().getIdentifier().getText().equals(element.getIdentifier().getText())) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Element '" + element.getIdentifier().getText() + "' is already defined above")
                        .range(element)
                        .create();
                break;
            }
            previousDeclaration = previousDeclaration.getPrevSibling();
        }

        // Check if all required bindings are provided
        Set<WOSystemBindingDefinitions.Binding> bindings = WOSystemBindingDefinitions.getBindingsForShortClassName(component.getIdentifier().getText());
        WODAssignmentList assignmentList = declaration.getWODAssignmentList();
        if (assignmentList != null) {
            Set<String> assignedBindings = new HashSet<>();
            for (@NotNull PsiElement psiElement : assignmentList.getChildren()) {
                if (psiElement instanceof WODAssignment assignment) {
                    assignedBindings.add(assignment.getWODBinding().getIdentifier().getText());
                }
            }
            for (WOSystemBindingDefinitions.Binding b : bindings) {
                if (b.required && !assignedBindings.contains(b.name)) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Missing required binding '" + b.name + "'")
                            .range(element)
                            .create();
                }
            }
        }
    }

    // FIXME: This should probably be somewhere central or close to the HTML-File? :o
    private List<String> getNamesFromHTMLTemplate(PsiFile wodFile) {
        List<String> elementNames = new ArrayList<>();

        PsiFile htmlFile = WOPsiUtil.getTemplateFile(wodFile);
        if (htmlFile == null) {
            return elementNames;
        }

        Collection<XmlTag> tags = PsiTreeUtil.findChildrenOfType(htmlFile, XmlTag.class);
        if (tags.isEmpty()) {
            return elementNames;
        }

        for (XmlTag tag : tags) {
            if (tag.getName().equals("webobject")) {
                @Nullable XmlAttribute name = tag.getAttribute("name");
                if (name != null) {
                    elementNames.add(name.getValue());
                }
            }
        }

        return elementNames;
    }

    private void annotateComponent(@NotNull WODComponent component, @NotNull AnnotationHolder holder) {
        // Validate component name against WebObjects java classes
        //       -> Valid => normal color
        //       -> Invalid => error annotation
        // normal color
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(component)
                .textAttributes(TextAttributesKey.find("HTML_TAG_NAME"))
                .create();

        String className = component.getIdentifier().getText();
        if (className.isEmpty()) {
            return;
        }

        try {
            WOPsiUtil.getPsiClassForComponentName(className, component.getProject());
        } catch (Exception e) {
            holder.newAnnotation(HighlightSeverity.ERROR, e.getMessage())
                    .range(component)
                    .create();
        }
    }

    private void annotateBinding(@NotNull WODBinding binding, @NotNull AnnotationHolder holder) {
        // TODO: Validate against known bindings? Does that make sense i.e. with components that take all  data-xxx bindings
        // normal color
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(binding)
                .textAttributes(JavaHighlightingColors.INSTANCE_FIELD_ATTRIBUTES)
                .create();


        // binding -> assignment -> assignmentList -> declaration
        PsiElement declaration = binding.getParent().getParent().getParent();
        if (!(declaration instanceof WODDeclaration)) {
            return;
        }

        WODComponent component = ((WODDeclaration) declaration).getWODComponent();
        if (component == null) {
            return;
        }

        String componentName = component.getIdentifier().getText();
        String bindingName = binding.getIdentifier().getText();

        // Check target components
        boolean found = false;
        // Check System components first
        Set<WOSystemBindingDefinitions.Binding> bindings = WOSystemBindingDefinitions.getBindingsForShortClassName(componentName);
        for (WOSystemBindingDefinitions.Binding b : bindings) {
            if (b.name.equals(bindingName)) {
                // binding found
                found = true;
                break;
            }
        }
        // Check custom components
        if (!found) {
            PsiDirectory componentFolder = WOPsiUtil.getComponentFolder(component);
            if (componentFolder != null) {
                APIFile apiFile = WOPsiUtil.getApiFile(componentFolder);
                if (apiFile != null) {
                    found = apiFile.getBindingNames().contains(bindingName);
                }
            }
        }
        // binding not found
        if (!found) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Binding '" + binding.getIdentifier().getText() + "' is not defined for component '" + component.getIdentifier().getText() + "'")
                    .range(binding)
                    .create();
        }
    }

    private void annotateKeyPath(@NotNull WODKeyPath keyPath, @NotNull AnnotationHolder holder) {
        // TODO: Validate against java fields or methods or getters
        //       If value starts with ^ it should be specified in the API file -> warning
        // In the WOD, The key 'WOComponentName' uses a value that is deprecated.
        // In the WOD, Unable to verify key 'meldung' because the keypath 'iterUpload.fehler' in LPModernMediaUpload passes through a collection
        holder.newAnnotation(HighlightSeverity.WARNING, "Not Yet Implemented :-O")
                .range(keyPath)
                .create();

//        try {
//            WODFile wodFile = getWODFile(keyPath);
//            String className = WOPsiUtil.getComponentName(wodFile);
//            if (className == null) {
//                // Should we show something if we can't find the class?
//                return;
//            }
//            PsiClass woClass = WOPsiUtil.getPsiClass(className, keyPath.getProject());
//            PsiField[] fields = woClass.getAllFields();
//            woClass.getAllMethods();
//            // TODO: Kann man vmtl. von WOXmlElementDescriptor kopieren/benutzen
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
