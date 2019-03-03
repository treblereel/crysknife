package org.treblereel.gwt.crysknife;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Provider;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.generator.PostConstructGenerator;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

import static org.treblereel.gwt.crysknife.util.Utils.getPackageName;

public class BootstrapperGenerator {

    private final IOCContext iocContext;

    private final GenerationContext generationContext;

    private TypeElement application;

    private ConstructorDeclaration constructorDeclaration;

    private MethodDeclaration initializeMethodDeclaration;

    private String BOOTSTRAP_EXTENSION = "Bootstrap";

    BootstrapperGenerator(IOCContext iocContext,
                          GenerationContext generationContext,
                          TypeElement application) {
        this.application = application;
        this.iocContext = iocContext;
        this.generationContext = generationContext;
    }

    void generate() {

        try {
            build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    private void build() throws IOException {
        String className = application.getSimpleName().toString();
        String classBootstrapName = className + BOOTSTRAP_EXTENSION;
        String packageName = getPackageName(application);

        String qualifiedBootstrapName = packageName + "." + classBootstrapName;

        JavaFileObject builderFile = generationContext.getProcessingEnvironment().getFiler()
                .createSourceFile(qualifiedBootstrapName);

        BootstrapperGeneratorBuilder builder = new BootstrapperGeneratorBuilder(iocContext.getBeans().get(application),
                                                                                classBootstrapName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.append(builder.build().toString());
        }
    }

    private class BootstrapperGeneratorBuilder {

        private final BeanDefinition beanDefinition;

        private CompilationUnit clazz = new CompilationUnit();

        private ClassOrInterfaceDeclaration classDeclaration;

        private String classBootstrapName;

        private BootstrapperGeneratorBuilder(BeanDefinition definition, String classBootstrapName) {
            this.beanDefinition = definition;
            this.classBootstrapName = classBootstrapName;
        }

        private CompilationUnit build() {
            initClass();
            addFields();
            initInitMethod();
            initConstructor();
            initPostConstractMethod();
            return clazz;
        }

        private void initInitMethod() {
            initializeMethodDeclaration = classDeclaration.addMethod("initialize", Modifier.Keyword.PACKAGE_PRIVATE);
        }

        private void initClass() {
            clazz.setPackageDeclaration(beanDefinition.getPackageName());
            classDeclaration = clazz.addClass(classBootstrapName);
            clazz.addImport(Provider.class);
            classDeclaration.addField(beanDefinition.getClassName(), "instance", Modifier.Keyword.PRIVATE);
        }

        private void initConstructor() {
            constructorDeclaration = classDeclaration.addConstructor(Modifier.Keyword.PACKAGE_PRIVATE);

            Parameter param = new Parameter();
            param.setName("instance");
            param.setType(new ClassOrInterfaceType().setName(application.getSimpleName().toString()));
            constructorDeclaration.addAndGetParameter(param);
            constructorDeclaration.getBody().addAndGetStatement(generateInstanceInitializer());

            for (FieldPoint fieldPoint : iocContext.getBeans().get(application).getFieldInjectionPoints()) {

                FieldAccessExpr instance = new FieldAccessExpr(new FieldAccessExpr(new ThisExpr(), "instance"), fieldPoint.getName());
                MethodCallExpr methodCallExpr = new MethodCallExpr(
                        new FieldAccessExpr(
                                new ThisExpr(),
                                Utils.toVariableName(fieldPoint.getType())), "get");
                AssignExpr assignExpr = new AssignExpr().setTarget(instance).setValue(methodCallExpr);
                initializeMethodDeclaration.getBody().get().addAndGetStatement(assignExpr);
            }
        }

        private void initPostConstractMethod() {
            beanDefinition.getExecutableDefinitions().forEach((k, v) -> {
                if (k.getClass().equals(PostConstructGenerator.class)) {
                    if (v.stream().findFirst().isPresent()) {
                        ExecutableDefinition postConstruct = v.stream().findFirst().get();
                        FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
                        MethodCallExpr method = new MethodCallExpr(instance,
                                                                   postConstruct
                                                                           .getExecutableElement()
                                                                           .getSimpleName().toString());
                        initializeMethodDeclaration.getBody().get().addAndGetStatement(method);
                    }
                }
            });
        }

        private Expression generateInstanceInitializer() {
            Expression instance = new FieldAccessExpr(new ThisExpr(), "instance");
            return new AssignExpr().setTarget(instance).setValue(new NameExpr("instance"));
        }

        private void addFields() {
            for (TypeElement field : iocContext.getOrderedBeans()) {
                if (field.getKind().equals(ElementKind.CLASS) && field.getAnnotation(Application.class) == null) {
                    BeanDefinition bean = iocContext.getBeans().get(field);
                    String varName = Utils.toVariableName(field);

                    ClassOrInterfaceType type = new ClassOrInterfaceType();
                    type.setName(Provider.class.getSimpleName());
                    type.setTypeArguments(new ClassOrInterfaceType().setName(field.getQualifiedName().toString()));
                    MethodCallExpr call = new MethodCallExpr(new NameExpr(Utils.getQualifiedFactoryName(field)), "create");
                    if (!bean.getDependsOn().isEmpty()) {
                        for (TypeElement argument : bean.getDependsOn()) {
                            String name = Utils.toVariableName(argument);
                            call.addArgument(name);
                        }
                    }
                    classDeclaration.addFieldWithInitializer(type, varName, call, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
                }
            }
        }
    }
}