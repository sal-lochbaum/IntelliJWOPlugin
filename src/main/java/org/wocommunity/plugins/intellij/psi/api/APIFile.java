package org.wocommunity.plugins.intellij.psi.api;

import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Technical specification for the WebObjects/WOLips Component API (.api) file format.
 * * <p>The configuration file adheres to an XML structure used by the WOLips IDE
 * plugins to validate bindings and structural constraints of a {@code WOComponent}.</p>
 * * <h3>1. Document Structure & Elements</h3>
 * * <p><b>&lt;wodefinitions&gt;</b></p>
 * <ul>
 * <li><b>Context:</b> Root element of the XML document.</li>
 * <li><b>Attributes:</b> None.</li>
 * <li><b>Child Elements:</b> Contains one {@code <wo>} element.</li>
 * </ul>
 * * <p><b>&lt;wo&gt;</b></p>
 * <ul>
 * <li><b>Context:</b> Represents the target Java component class mapping.</li>
 * <li><b>Attributes:</b>
 * <ul>
 * <li>{@code class} (String, Required) - The fully qualified or simple name of the Java class.</li>
 * <li>{@code wocomponentcontent} (Boolean, Optional, Default: "false") - Indicates if the component processes nested HTML template content.</li>
 * </ul>
 * </li>
 * <li><b>Child Elements:</b> Zero or more {@code <binding>} and {@code <validation>} elements in an interleaved sequence.</li>
 * </ul>
 * * <p><b>&lt;binding&gt;</b></p>
 * <ul>
 * <li><b>Context:</b> Declares a component parameter.
 * <i>Note: Mandatory bindings do not use a "required" attribute here; instead, they are enforced via a separate {@code <validation>} block.</i></li>
 * <li><b>Attributes:</b>
 * <ul>
 * <li>{@code name} (String, Required) - Identifier of the binding.</li>
 * <li>{@code defaults} (Enum, Optional) - Defines the allowed values for IDE auto-completion. <i>See Section 2 for values.</i></li>
 * </ul>
 * </li>
 * <li><b>Child Elements:</b> Must be an empty element.</li>
 * </ul>
 * * <p><b>&lt;validation&gt;</b></p>
 * <ul>
 * <li><b>Context:</b> Defines a conditional rule tree. If the inner logical expression evaluates to <b>true</b>, the validation constraint fails.</li>
 * <li><b>Attributes:</b>
 * <ul>
 * <li>{@code message} (String, Required) - The compiler warning or error string displayed in the IDE when the condition triggers.</li>
 * </ul>
 * </li>
 * <li><b>Child Elements:</b> Requires exactly one root conditional/logical element ({@code <and>}, {@code <or>}, {@code <not>}, {@code <bound>}, {@code <unbound>}, or {@code <unsettable>}).</li>
 * </ul>
 * * <h3>2. The 'defaults' Attribute Enumeration</h3>
 * <p>The {@code defaults} attribute guides IDE auto-completion with the following enumerated string values:</p>
 * <ul>
 * <li>{@code "Boolean"} / {@code "YES/NO"} - Toggles literal values: {@code true}/{@code false} or {@code YES}/{@code NO}.</li>
 * <li>{@code "Actions"} - Resolves to public methods returning {@code WOActionResults}.</li>
 * <li>{@code "Resources"} - Resolves to internal project resource file paths (e.g., images, stylesheets).</li>
 * <li>{@code "Page Names"} - Resolves to valid {@code WOComponent} names visible within the scope.</li>
 * <li>{@code "Frameworks"} - Resolves to a list of linked framework names.</li>
 * <li>{@code "Date Format Strings"} - Resolves to standard POSIX date formatting patterns (e.g., {@code "%Y/%m/%d"}).</li>
 * <li>{@code "Number Format Strings"} - Resolves to numeric localized formatting patterns.</li>
 * <li>{@code "MIME Types"} - Resolves to official internet media types (e.g., {@code "text/html"}).</li>
 * </ul>
 * * <h3>3. Logical Evaluation Grammar (Conditions)</h3>
 * <p>Validation conditions are processed as a boolean tree. If the expression evaluates to <b>true</b>, the {@code message} is triggered in the IDE:</p>
 * <ul>
 * <li>{@code <bound name="[bindingName]"/>} - Evaluates to {@code true} if the specified binding is explicitly declared on the component instance.</li>
 * <li>{@code <unbound name="[bindingName]"/>} - Evaluates to {@code true} if the specified binding is missing from the component instance (used to enforce "Required" fields).</li>
 * <li>{@code <unsettable name="[bindingName]"/>} - Evaluates to {@code true} if the bound variable/key-path cannot receive a value (used to enforce "Will Set" constraints where a setter/writable field is required).</li>
 * <li>{@code <and>} - Structural container. Evaluates to {@code true} if <b>all</b> child expressions are {@code true}.</li>
 * <li>{@code <or>} - Structural container. Evaluates to {@code true} if <b>at least one</b> child expression is {@code true}.</li>
 * <li>{@code <not>} - Structural container. Wraps exactly <b>one</b> child expression and inverts its evaluation result.</li>
 * </ul>
 * * <h3>4. Reference Structural Template</h3>
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <wodefinitions>
 *   <wo class="TheComponentName" wocomponentcontent="false">
 *     <binding defaults="Actions" name="onCloseCallback"/>
 *     <binding name="input"/>
 *     <binding name="result"/>
 *     <validation message="'input' is a required binding.">
 *       <unbound name="input"/>
 *     </validation>
 *     <validation message="'result' must be bound to a settable value">
 *       <unsettable name="result"/>
 *     </validation>
 *      <validation message = "'id' is a required binding">
 *        <unbound name = "id"/>
 *      </validation>
 *      <validation message="'listItemIDKeyPath' must be bound when 'list' is bound">
 *        <and>
 *          <bound name = "list"/>
 *          <unbound name = "listItemIDKeyPath"/>
 *        </and>
 *      </validation>
 *   </wo>
 * </wodefinitions>
 * }</pre>
 */
public class APIFile {
    private final XmlFile xmlFile;

    private static class WO {
        public final String name;
        public final boolean wocomponentcontent;
        public final HashMap<String, Binding> bindings;
        public final Set<String> requiredBindingNames;
        // public final List<Validation> validations;

        private WO(XmlTag woTag) {
            this.name = woTag.getAttributeValue("class");
            this.wocomponentcontent = "true".equals(woTag.getAttributeValue("wocomponentcontent"));
            this.requiredBindingNames = new HashSet<>();

            // TODO actually parse validations correctly
            for (XmlTag validationTag : woTag.findSubTags("validation")) {
                validationTag.accept(new XmlRecursiveElementVisitor() {
                    @Override
                    public void visitXmlTag(@NotNull XmlTag tag) {
                        super.visitXmlTag(tag); // Crucial to continue recursion down into children

                        if (tag.getName().equals("unbound")) {
                            String bindingName = tag.getAttributeValue("name");
                            requiredBindingNames.add(bindingName);
                        }
                    }
                });
            }

            this.bindings = new HashMap<>();
            for (XmlTag bindingTag : woTag.findSubTags("binding")) {
                String name = bindingTag.getAttributeValue("name");
                if (name != null) {
                    String defaults = bindingTag.getAttributeValue("defaults");
                    String requiredAtt = bindingTag.getAttributeValue("required"); // unsure of exact xml definition...
                    boolean required = "true".equals(requiredAtt) || requiredBindingNames.contains(name);
                    this.bindings.put(name, new Binding(name, defaults, required));
                }
            }
        }
    }

    /**
     * @param defaults TODO: Should probably be an enum
     */
    public record Binding(String name, String defaults, boolean required) {
    }

    public APIFile(@NotNull XmlFile xmlFile) {
        super();
        this.xmlFile = xmlFile;
    }

    private @Nullable WO getData() {
        return CachedValuesManager.getCachedValue(xmlFile, () -> {
            XmlTag rootTag = xmlFile.getRootTag(); // <wodefinition>
            if (rootTag == null) {
                return null;
            }

            XmlTag woComponentTag = rootTag.findFirstSubTag("wo");
            if (woComponentTag == null) {
                return null;
            }

            WO component = new WO(woComponentTag);

            // Cache it until the physical XML file is modified
            return CachedValueProvider.Result.create(component, xmlFile);
        });
    }

    public Set<String> getBindingNames() {
        WO data = getData();
        return data != null ? data.bindings.keySet() : Collections.emptySet();
    }

    public Set<String> getRequiredBindings() {
        WO data = getData();
        return data != null ? data.requiredBindingNames : Collections.emptySet();
    }
}