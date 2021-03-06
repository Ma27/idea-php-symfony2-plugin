package fr.adrienbrault.idea.symfony2plugin.config.yaml;

import com.intellij.patterns.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.yaml.YAMLElementTypes;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLCompoundValue;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLKeyValue;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class YamlElementPatternHelper {

    /**
     * auto complete on
     *
     * keyName: refer|
     *
     * @param keyName
     */
    public static ElementPattern<PsiElement> getOrmSingleLineScalarKey(String... keyName) {
        return getKeyPattern(keyName).inFile(getOrmFilePattern()).withLanguage(YAMLLanguage.INSTANCE);
    }

    public static ElementPattern<PsiElement> getSingleLineScalarKey(String... keyName) {
        // key: | and key: "quote" is valid here
        // getKeyPattern
        return PlatformPatterns.or(
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withName(
                        PlatformPatterns.string().oneOf(keyName)
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE)
            ,
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_DSTRING)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withName(
                        PlatformPatterns.string().oneOf(keyName)
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_STRING)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withName(
                        PlatformPatterns.string().oneOf(keyName)
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    /**
     * provides auto complete on
     *
     * keyName:
     *   refer|
     *   refer|: xxx
     *   refer|
     *
     * @param keyName key name
     */
    public static ElementPattern<PsiElement> getOrmParentLookup(String keyName) {
        return PlatformPatterns.or(
            // match
            //
            // keyName:
            //   refer|: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_KEY)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLKeyValue.class)
                            .withName(
                                PlatformPatterns.string().equalTo(keyName)
                            )
                        )
                    )
                )
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // keyName:
            //   xxx: xxx
            //   refer|
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withName(
                            PlatformPatterns.string().equalTo(keyName)
                        )
                    )
                )
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // keyName:
            //   refer|
            //   xxx: xxx
            getKeyPattern(keyName)
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    /**
     * provides auto complete on
     *
     * keyName:
     *   refer|
     *   refer|: xxx
     *   refer|
     *
     * @param keyName key name
     */
    public static ElementPattern<PsiElement> getParentKeyName(String keyName) {
        return PlatformPatterns.or(
            // match
            //
            // keyName:
            //   refer|: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_KEY)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLKeyValue.class)
                            .withName(
                                PlatformPatterns.string().equalTo(keyName)
                            )
                        )
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // keyName:
            //   xxx: xxx
            //   refer|
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withName(
                            PlatformPatterns.string().equalTo(keyName)
                        )
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // keyName:
            //   refer|
            //   xxx: xxx
            getKeyPattern(keyName)
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    /**
     * provides auto complete on
     *
     * xxx:
     *   refer|
     *   refer|: xxx
     *   refer|
     */
    public static ElementPattern<PsiElement> getOrmRoot() {
        return PlatformPatterns.or(

            // match
            //
            // xxx:
            //   refer|: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_KEY)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLKeyValue.class)
                            .withParent(PlatformPatterns
                                .psiElement(YAMLDocument.class)
                            )
                        )
                    )
                )
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // xxx:
            //   xxx: xxx
            //   refer|
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLDocument.class)
                        )
                    )
                )
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // xxx:
            //   refer|
            //   xxx: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLDocument.class)
                    )
                )
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    public static ElementPattern<PsiElement> getWithFirstRootKey() {
        return PlatformPatterns.or(

            // match
            //
            // xxx:
            //   refer|: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_KEY)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLKeyValue.class)
                            .withParent(PlatformPatterns
                                .psiElement(YAMLDocument.class)
                            )
                        )
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // xxx:
            //   xxx: xxx
            //   refer|
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLDocument.class)
                        )
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // xxx:
            //   refer|
            //   xxx: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLDocument.class)
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    /**
     * provides auto complete on
     *
     * tree:
     *   xxx:
     *     refer|
     *     refer|: xxx
     *     refer|
     */
    public static ElementPattern<PsiElement> getFilterOnPrevParent(String... tree) {
        return PlatformPatterns.or(

            // match
            //
            // tree:
            //   xxx:
            //     refer|: xxx
            PlatformPatterns
            .psiElement(YAMLTokenTypes.SCALAR_KEY)
            .withParent(PlatformPatterns
                .psiElement(YAMLKeyValue.class)
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                            .withParent(PlatformPatterns
                                .psiElement(YAMLKeyValue.class)
                                .withName(PlatformPatterns
                                    .string().oneOfIgnoreCase(tree)
                                )
                            )
                        )
                    )
                )
            )
            .inFile(getOrmFilePattern())
            .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // tree:
            //   xxx:
            //     xxx: xxx
            //     refer|
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                            .withParent(PlatformPatterns
                                .psiElement(YAMLKeyValue.class)
                                .withName(PlatformPatterns
                                    .string().oneOfIgnoreCase(tree)
                                )
                            )
                        )
                    )
                )
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // tree:
            //   xxx:
            //     refer|
            //     xxx: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLKeyValue.class)
                            .withName(PlatformPatterns
                                .string().oneOfIgnoreCase(tree)
                            )
                        )
                    )
                )
                .inFile(getOrmFilePattern())
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    /**
     * simplified getFilterOnPrevParent :)
    * 
     * services:
     *   foo.name:
     *     "complete": foo
     */
    public static ElementPattern<PsiElement> getSuperParentArrayKey(String... tree) {
        return PlatformPatterns.or(

            // match
            //
            // tree:
            //   xxx:
            //     refer|: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_KEY)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLKeyValue.class)
                            .withParent(PlatformPatterns
                                .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                                .withParent(PlatformPatterns
                                    .psiElement(YAMLKeyValue.class)
                                    .withName(PlatformPatterns
                                        .string().oneOfIgnoreCase(tree)
                                    )
                                )
                            )
                        )
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // tree:
            //   xxx:
            //     xxx: xxx
            //     refer|
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                            .withParent(PlatformPatterns
                                .psiElement(YAMLKeyValue.class)
                                .withName(PlatformPatterns
                                    .string().oneOfIgnoreCase(tree)
                                )
                            )
                        )
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE),

            // match
            //
            // tree:
            //   xxx:
            //     refer|
            //     xxx: xxx
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withParent(PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                        .withParent(PlatformPatterns
                            .psiElement(YAMLKeyValue.class)
                            .withName(PlatformPatterns
                                .string().oneOfIgnoreCase(tree)
                            )
                        )
                    )
                )
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    /**
     * find common services
     */
    public static ElementPattern<PsiElement> getServiceDefinition() {

        return PlatformPatterns.or(
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withText(
                    StandardPatterns.string().startsWith("@")
                )
                .withLanguage(YAMLLanguage.INSTANCE)
            ,
            // @TODO: cleanup on PhpStorm8 support only
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_STRING)
                .withText(
                    StandardPatterns.string().startsWith("'@")
                )
                .withLanguage(YAMLLanguage.INSTANCE),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_STRING)
                .withText(
                    StandardPatterns.string().startsWith("\"@")
                )
                .withLanguage(YAMLLanguage.INSTANCE),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_DSTRING)
                .withText(
                    StandardPatterns.string().startsWith("'@")
                )
                .withLanguage(YAMLLanguage.INSTANCE),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_DSTRING)
                .withText(
                    StandardPatterns.string().startsWith("\"@")
                )
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    /**
     * find common service parameter
     */
    public static ElementPattern<PsiElement> getServiceParameterDefinition() {
        return PlatformPatterns.or(
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .withText(
                    StandardPatterns.string().startsWith("%")
                )
                .withLanguage(YAMLLanguage.INSTANCE)
            ,
            // @TODO: cleanup on PhpStorm8 support only
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_STRING)
                .withText(
                    StandardPatterns.string().startsWith("'%")
                )
                .withLanguage(YAMLLanguage.INSTANCE),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_STRING)
                .withText(
                    StandardPatterns.string().startsWith("\"%")
                )
                .withLanguage(YAMLLanguage.INSTANCE),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_DSTRING)
                .withText(
                    StandardPatterns.string().startsWith("'%")
                )
                .withLanguage(YAMLLanguage.INSTANCE),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_DSTRING)
                .withText(
                    StandardPatterns.string().startsWith("\"%")
                )
                .withLanguage(YAMLLanguage.INSTANCE)
        );
    }

    private static ElementPattern<? extends PsiFile> getOrmFilePattern() {
        return PlatformPatterns.psiFile().withName(PlatformPatterns.string().andOr(
            PlatformPatterns.string().endsWith("orm.yml"),
            PlatformPatterns.string().endsWith("couchdb.yml"),
            PlatformPatterns.string().endsWith("odm.yml"),
            PlatformPatterns.string().endsWith("mongodb.yml"),
            PlatformPatterns.string().endsWith("document.yml")
        ));
    }

    private static PsiElementPattern.Capture<PsiElement> getKeyPattern(String... keyName) {
        return PlatformPatterns
            .psiElement(YAMLTokenTypes.TEXT)
            .withParent(PlatformPatterns
                .psiElement(YAMLKeyValue.class)
                .withName(
                    PlatformPatterns.string().oneOfIgnoreCase(keyName)
                )
            );
    }

    /**
     * Possible config key completion
     * In document root or key value context
     */
    public static ElementPattern<PsiElement> getConfigKeyPattern() {
        return PlatformPatterns.psiElement().withParent(PlatformPatterns.or(
            PlatformPatterns.psiElement(YAMLDocument.class),
            PlatformPatterns.psiElement(YAMLCompoundValue.class),
            PlatformPatterns.psiElement(YAMLKeyValue.class)
        )).inFile(
            // not should fire this in all yaml files
            PlatformPatterns.psiFile().withName(PlatformPatterns.string().matches("[security|config].*\\.yml"))
        );
    }

    /**
     * Get service before comma
     *
     * ["@service', createNewsletterManager|]
     * [@service, createNewsletterManager|]
     * ['@service', createNewsletterManager|]
     */
    public static ElementPattern<PsiElement> getAfterCommaPattern() {

        return PlatformPatterns.or(
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .afterLeafSkipping(
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement().withText(",")
                ),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_STRING)
                .afterLeafSkipping(
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement().withText(",")
                ),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_DSTRING)
                .afterLeafSkipping(
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement().withText(",")
                )
        );

    }

    /**
     * Get service before comma
     *
     * ["@service', createNewsletterManager|]
     * [@service, createNewsletterManager|]
     * ['@service', createNewsletterManager|]
     */
    public static ElementPattern<PsiElement> getPreviousCommaSibling() {

        return PlatformPatterns.or(
            PlatformPatterns
                .psiElement(YAMLTokenTypes.TEXT)
                .beforeLeafSkipping(
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement().withText(",")
                ),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_STRING)
                .beforeLeafSkipping(
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement().withText(",")
                ),
            PlatformPatterns
                .psiElement(YAMLTokenTypes.SCALAR_DSTRING)
                .beforeLeafSkipping(
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement().withText(",")
                )
        );

    }

    /**
     * parameters:
     *    foo.example.class: |
     *
     */
    static PsiElementPattern.Capture<PsiElement> getParameterClassPattern() {
        return PlatformPatterns
            .psiElement(YAMLTokenTypes.TEXT)
            .withParent(PlatformPatterns
                .psiElement(YAMLKeyValue.class)
                .withName(
                    PlatformPatterns.string().endsWith(".class")
                )
                .withParent(PlatformPatterns
                    .psiElement(YAMLElementTypes.COMPOUND_VALUE)
                    .withParent(PlatformPatterns
                        .psiElement(YAMLKeyValue.class)
                        .withName("parameters")
                    )
                )
            );

    }

    public static PsiElementPattern.Capture<PsiElement> getInsideServiceKeyPattern() {
        return getInsideKeyValue("services", "parameters");
    }

    public static PsiElementPattern.Capture<PsiElement> getInsideKeyValue(String... keys) {
        return PlatformPatterns
            .psiElement()
            .inside(
                PlatformPatterns
                    .psiElement(YAMLKeyValue.class)
                    .withName(
                        PlatformPatterns.string().oneOf(keys)
                    )
            );
    }

    /**
     * services:
     *   i<caret>d: []
     */
    public static PsiElementPattern.Capture<PsiElement> getServiceIdKeyPattern() {
        return PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_KEY)
             .withParent(PlatformPatterns.psiElement(YAMLKeyValue.class)
                 .withParent(PlatformPatterns.psiElement(YAMLCompoundValue.class)
                     .withParent(
                          PlatformPatterns.psiElement(YAMLKeyValue.class)
                              .withName(PlatformPatterns.string().oneOfIgnoreCase("services"))
                     )
                 )
             );
    }
}
