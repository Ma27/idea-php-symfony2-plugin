package fr.adrienbrault.idea.symfony2plugin.dic.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import fr.adrienbrault.idea.symfony2plugin.Symfony2InterfacesUtil;
import fr.adrienbrault.idea.symfony2plugin.config.xml.XmlHelper;
import fr.adrienbrault.idea.symfony2plugin.config.yaml.YamlElementPatternHelper;
import fr.adrienbrault.idea.symfony2plugin.util.PsiElementUtils;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLKeyValue;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class CaseSensitivityServiceInspection extends LocalInspectionTool {

    public static final String SYMFONY_LOWERCASE_LETTERS_FOR_SERVICE = "Symfony: lowercase letters for service and parameter";

    @NotNull
    public PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {

        PsiFile psiFile = holder.getFile();
        if(psiFile.getFileType() == PhpFileType.INSTANCE) {
            phpVisitor(holder);
        } else if(psiFile.getFileType() == YAMLFileType.YML) {
            yamlVisitor(holder);
        } else if(psiFile.getFileType() == XmlFileType.INSTANCE) {
            xmlVisitor(holder);
        }

        return super.buildVisitor(holder, isOnTheFly);
    }

    private void yamlVisitor(final @NotNull ProblemsHolder holder) {

        PsiFile file = holder.getFile();

        // usage in service arguments or every other service condition
        file.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement psiElement) {

                // @TODO: support key itself
                if (YamlElementPatternHelper.getServiceDefinition().accepts(psiElement) && YamlElementPatternHelper.getInsideServiceKeyPattern().accepts(psiElement)) {
                    String serviceName = YamlHelper.trimSpecialSyntaxServiceName(psiElement.getText());
                    // dont mark "@", "@?", "@@" escaping and expressions
                    if (isValidService(serviceName)) {
                        holder.registerProblem(psiElement, SYMFONY_LOWERCASE_LETTERS_FOR_SERVICE, ProblemHighlightType.WEAK_WARNING);
                    }
                }

                super.visitElement(psiElement);
            }
        });

        // services and parameter
        YamlHelper.processKeysAfterRoot(file, new Processor<YAMLKeyValue>() {
            @Override
            public boolean process(YAMLKeyValue yamlKeyValue) {
                String keyText = yamlKeyValue.getKeyText();
                if(StringUtils.isNotBlank(keyText) && !keyText.equals(keyText.toLowerCase())) {
                    PsiElement firstChild = yamlKeyValue.getFirstChild();
                    if(firstChild != null) {
                        holder.registerProblem(firstChild, SYMFONY_LOWERCASE_LETTERS_FOR_SERVICE, ProblemHighlightType.WEAK_WARNING);
                    }
                }

                return false;
            }
        }, "services", "parameters");
    }

    private void phpVisitor(final @NotNull ProblemsHolder holder) {

        holder.getFile().acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                MethodReference methodReference = PsiElementUtils.getMethodReferenceWithFirstStringParameter(element);
                if (methodReference != null && new Symfony2InterfacesUtil().isContainerGetCall(methodReference)) {
                    String serviceName = Symfony2InterfacesUtil.getFirstArgumentStringValue(methodReference);
                    if(serviceName != null && !serviceName.equals(serviceName.toLowerCase())) {
                        holder.registerProblem(element, SYMFONY_LOWERCASE_LETTERS_FOR_SERVICE, ProblemHighlightType.WEAK_WARNING);
                    }
                }

                super.visitElement(element);
            }
        });
    }

    private void xmlVisitor(final @NotNull ProblemsHolder holder) {
        holder.getFile().acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement psiElement) {
                if(psiElement instanceof XmlAttributeValue && (XmlHelper.getArgumentServiceIdPattern().accepts(psiElement) || XmlHelper.getServiceIdNamePattern().accepts(psiElement))) {
                    String serviceName = ((XmlAttributeValue) psiElement).getValue();
                    if(StringUtils.isNotBlank(serviceName) && !serviceName.equals(serviceName.toLowerCase())) {
                        holder.registerProblem(psiElement, SYMFONY_LOWERCASE_LETTERS_FOR_SERVICE, ProblemHighlightType.WEAK_WARNING);
                    }
                }

                super.visitElement(psiElement);
            }
        });
    }

    private boolean isValidService(@NotNull String serviceName) {
        if(serviceName.length() < 2 || !serviceName.startsWith("@")) {
            return false;
        }

        // expression
        if(serviceName.startsWith("@=")) {
            return false;
        }

        return true;
    }

}
