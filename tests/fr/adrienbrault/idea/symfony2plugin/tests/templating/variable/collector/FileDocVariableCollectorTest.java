package fr.adrienbrault.idea.symfony2plugin.tests.templating.variable.collector;

import com.jetbrains.twig.TwigFileType;
import fr.adrienbrault.idea.symfony2plugin.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see fr.adrienbrault.idea.symfony2plugin.templating.variable.collector.FileDocVariableCollector
 * @see fr.adrienbrault.idea.symfony2plugin.templating.util.TwigTypeResolveUtil#findFileVariableDocBlock
 */
public class FileDocVariableCollectorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();

        myFixture.configureByText("classes.php", "<?php\n" +
            "namespace Bar;\n" +
            "class FooClass {\n" +
            "  public function getFooBar() {}\n" +
            "  public function setMyBar() {}\n" +
            "  public function isMyCar() {}\n" +
            "  public function cool() {}\n" +
            "  public function Hot() {}\n" +
            "  protected function protectedBar() {}\n" +
            "  private function privateBar() {}\n" +
            "  /** @return FooClass[] */\n" +
            "  public function getNested() {}\n" +
            "}"
        );

    }

    public void testFileBasedVarDocPhpTypes() {
        assertCompletionContains(TwigFileType.INSTANCE, "{# @var bar \\Bar\\FooClass #} {{ <caret> }}", "bar");
        assertCompletionContains(TwigFileType.INSTANCE, "{# @var bar \\Bar\\FooClass #} {{ bar.<caret> }}", "fooBar", "myCar", "cool", "Hot");

        assertCompletionNotContains(TwigFileType.INSTANCE, "{# @var bar \\Bar\\FooClass #} {{ bar.<caret> }}", "myBar");
        assertCompletionNotContains(TwigFileType.INSTANCE, "{# @var bar \\Bar\\FooClass #} {{ bar.<caret> }}", "protectedBar");
        assertCompletionNotContains(TwigFileType.INSTANCE, "{# @var bar \\Bar\\FooClass #} {{ bar.<caret> }}", "privateBar");
    }

    public void testFileBasedVarDocPhpTypesAsDeprecated() {
        // remove on dropped feature
        assertCompletionContains(TwigFileType.INSTANCE, "{# bar \\Bar\\FooClass #} {{ <caret> }}", "bar");
    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.templating.util.TwigTypeResolveUtil#collectForArrayScopeVariables
     * @see fr.adrienbrault.idea.symfony2plugin.templating.util.TwigTypeResolveUtil#getForTagIdentifierAsString
     */
    public void testVarArrayIteration() {

        assertCompletionContains(TwigFileType.INSTANCE, "" +
            "{# @var bars \\Bar\\FooClass[] #}\n" +
            "{% for bar in bars %}\n" +
            "  {{ <caret> }}\n" +
            "{% endfor %}\n"
            , "bar"
        );

        assertCompletionContains(TwigFileType.INSTANCE, "" +
            "{# @var bars \\Bar\\FooClass[] #}\n" +
            "{% for bar in bars %}\n" +
            "  {{ bar.<caret> }}\n" +
            "{% endfor %}\n"
            , "fooBar"
        );

        assertCompletionContains(TwigFileType.INSTANCE, "" +
                "{# @var bars \\Bar\\FooClass[] #}\n" +
                "{% for bar in bars|foo %}\n" +
                "  {{ bar.<caret> }}\n" +
                "{% endfor %}\n"
            , "fooBar"
        );

        assertCompletionContains(TwigFileType.INSTANCE, "" +
                "{# @var bars \\Bar\\FooClass[] #}\n" +
                "{% for bar in bars | foo %}\n" +
                "  {{ bar.<caret> }}\n" +
                "{% endfor %}\n"
            , "fooBar"
        );

    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.templating.util.TwigTypeResolveUtil#collectForArrayScopeVariables
     */
    public void testVarChainArrayIteration() {

        assertCompletionContains(TwigFileType.INSTANCE, "" +
                "{# @var bars \\Bar\\FooClass #}\n" +
                "{% for bar in bars.nested %}\n" +
                "  {{ bar.<caret> }}\n" +
                "{% endfor %}\n"
            , "fooBar"
        );

        assertCompletionContains(TwigFileType.INSTANCE, "" +
                "{# @var bars \\Bar\\FooClass #}\n" +
                "{% for bar in bars.nested | foo %}\n" +
                "  {{ bar.<caret> }}\n" +
                "{% endfor %}\n"
            , "fooBar"
        );

    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.templating.util.TwigTypeResolveUtil#collectForArrayScopeVariables
     */
    public void testThatDuplicateScopeVariablesAreMerged() {

        assertCompletionContains(TwigFileType.INSTANCE, "" +
                "{# @var bars \\Bar\\FooClass #}\n" +
                "{% for bar in bars.fooBar %}\n" +
                "  {# @var bar \\Bar\\FooClass #}\n" +
                "  {{ bar.<caret> }}\n" +
                "{% endfor %}\n"
            , "fooBar"
        );

    }

}
