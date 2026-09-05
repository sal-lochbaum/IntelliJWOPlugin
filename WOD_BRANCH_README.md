### TODOS / Known Issues

#### Files & Folders

- FEAT Support Localized component folder
    - There can be multiple .wo folders for one component
    - jumping from .java to .wo does what?
    - The .api file lookup must be adjusted
    - (At least start with Nonlocalized.lproj, because that's where all wonder components are ;-) )
- BUG Standalone .api files for template-less components are not recognized

### Bindings / Definitions

- BUG .api files and the xml for WO do not have the full picture... e.g. some components accept arbitrary bindings like
  "data-xxx" or e.g. WOGenericContainer can have "aria-label" bindings.
  Also public variables and setter methods of the target component are good bindings.
  Or WOSwitchComponent which ... may just take any bindings...?
- Component-Content concept (setting in api file, nesting allowed in .html)

### Value (s)

- BUG values starting with "^" and using a keypath are not parsed correctly
- FEAT Validate values against .api file (types and validations)
- BUG autocomplete should autocomplete to "data" if there is a "getData" method and not getData?
- FEAT detect deprecated values → "In the WOD, The key 'WOComponentName' uses a value that is deprecated."

#### .java

- FEAT Clean up WODAnnotator: Move most of the checks into references and just use the references to annotate the wod
  file
- FEAT getters and setters should be marked as used in wo component

#### .wod

- BUG Formatting while typing is awkward ("{" should jump to new line and indent, after ";" at the end of a line the
  next
  line should have the same indent, etc.F)
- command click on Component Names should jumpt to target component

#### .html

- BUG command-click on component names does not jump to the component in the wod file
- FEAT Quick-Fix: Create definition with name after user enters <webobject name="Foo" /> -> creates "Foo : <> {}" in .wod

---

## Features

### WOD language support

- Added a dedicated `.wod` language implementation:
    - WOD lexer.
    - WOD parser/BNF grammar.
    - PSI model for WOD declarations, components, bindings, values, key paths, comments, and parent bindings.
    - WOD parser definition and file type registration.
    - Syntax highlighting for WOD files.

- Added WOD formatter support:
    - Formatting model for WOD files.
    - Basic spacing rules for declarations, assignments, braces, semicolons, and related WOD syntax.

- Added support for WOD comments in the lexer/parser.

- Added support for boolean literals in WOD values.

- Added support for parent bindings using `^bindingName`:
    - Parsing.
    - Validation against the current component `.api` file.
    - Completion support.

### WOD validation and annotations

- Added validation for WOD element declarations:
    - Warns when a WOD element is not referenced from the component HTML template.
    - Reports duplicate element declarations.
    - Reports missing required bindings for known system components.

- Added validation for WOD component names:
    - Resolves component names against Java/WebObjects component classes.
    - Reports unresolved component classes.

- Added binding validation in WOD files:
    - Validates bindings against known system component bindings.
    - Validates custom component bindings against `.api` files.
    - Reports bindings that are not defined for the target component.

- Added key path validation in WOD values:
    - Resolves key paths against the backing Java component class.
    - Reports missing Java fields/accessors.
    - Applies Java-like highlighting to resolved key path segments.
    - Warns instead of failing hard when validation cannot be completed because the target class implements dynamic
      Key-Value Coding.

- Added support for suppressing assignment validation with comments containing `valid`.

### WOD references, navigation, and completion

- Added references from WOD element declarations to matching HTML `<webobject name="...">` usages.

- Added references from WOD key path segments to Java members:
    - Fields.
    - Getter-style methods.
    - Boolean accessor methods.
    - WebObjects-style KVC variants such as underscored accessors/fields.

- Added key path completion in WOD files:
    - Suggests public zero-argument non-void Java methods.
    - Suggests public Java fields.
    - Normalizes Java/KVC accessor names into binding-style key names.
    - Shows icons and type information in completion results.

- Added WOD binding completion.

### HTML / WOD cross-navigation

- Added resolution from HTML `<webobject name="...">` attributes back to corresponding WOD declarations.

- Added reference contributor support so HTML template usages and WOD declarations can participate in IntelliJ
  navigation/reference workflows.

### Java integration

- Added Java gutter line markers for members used from WOD key paths:
    - Java fields and methods referenced by WOD files show a WebObjects icon.
    - The gutter icon navigates back to the WOD key path usages.

- WOD key path references now participate in IntelliJ reference search, helping Java members used from WOD files be
  recognized as used.

### WebObjects HTML template improvements

- Added support for legacy `<webobject>` tags:
    - Validation.
    - Attribute handling.
    - Descriptor support.

- Added inspection suppression for WebObjects HTML templates to avoid irrelevant IntelliJ HTML warnings, such as
  warnings about empty `<webobject>` tags.

### Component editor/navigation improvements

- Improved opening files inside `.wo` folders:
    - Selecting/opening a file inside a `.wo` folder opens the WO component editor.

- Improved component editor tab handling:
    - Related child files select the matching editor tab.
    - Search/navigation into child files works more reliably.
    - Navigation moves the caret to the requested child file location inside the embedded editor.

### Utility classes / infrastructure added or expanded for WOD support

- `KeyValueCodingUtil`
    - Resolves WOD key paths against Java classes using WebObjects Key-Value Coding conventions.
    - Resolves individual key path elements.
    - Converts resolved Java members to their result types for chained key path validation.
    - Provides Java-member-based completion suggestions for WOD key paths.

- `WODElementReference`
    - Resolves WOD element declarations against matching HTML template usages.

- `WODKeyPathReference`
    - Resolves WOD key path segments against Java fields/accessors.

- WOD PSI mixins:
    - `WODElementMixin`
    - `WODKeyPathElementMixin`

- WOD language/editor infrastructure:
    - `WODAnnotator`
    - `WODCompletionContributor`
    - `WODFormattingModelBuilder`
    - `WODBlock`
    - `WODSyntaxHighlighter`
    - `WODSyntaxHighlighterFactory`
    - `WODParserDefinition`
    - `WODLexerAdapter`
    - `WODFile`
    - `WODFileType`
    - `WODLanguage`
    - `WODElementType`
    - `WODTokenType`
    - `WODTokenSets`

---

## TODOS for competing with wolips ... ;-)

- Enhance "modern component" style with "<wo:" tags and no .wod file
- .api Editor
- Model Editor and generation
- OGNL support?

---

## Disclaimer

Some AI was harmed during the development of these features..