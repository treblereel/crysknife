/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.treblereel.gwt.crysknife;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Provider;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.client.Instance;
import org.treblereel.gwt.crysknife.client.internal.AbstractBeanManager;
import org.treblereel.gwt.crysknife.exception.GenerationException;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
public class BeanManagerGenerator {

  private final IOCContext iocContext;

  private final GenerationContext generationContext;

  BeanManagerGenerator(IOCContext iocContext, GenerationContext generationContext) {
    this.iocContext = iocContext;
    this.generationContext = generationContext;
  }

  void generate() {
    try {
      build();
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private void build() throws IOException {
    JavaFileObject builderFile = generationContext.getProcessingEnvironment().getFiler()
        .createSourceFile(BeanManager.class.getCanonicalName() + "Impl");
    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      out.append(new BeanManagerGeneratorBuilder().build().toString());
    }
  }

  public class BeanManagerGeneratorBuilder {

    private CompilationUnit clazz = new CompilationUnit();

    private ClassOrInterfaceDeclaration classDeclaration;

    private MethodDeclaration getMethodDeclaration;

    public CompilationUnit build() {
      initClass();
      addFields();
      initInitMethod();
      addGetInstanceMethod();
      return clazz;
    }

    private void initClass() {
      clazz.setPackageDeclaration(BeanManager.class.getPackage().getName());
      classDeclaration = clazz.addClass(BeanManager.class.getSimpleName() + "Impl");
      clazz.addImport(Provider.class);
      clazz.addImport(Map.class);
      clazz.addImport(HashMap.class);
      clazz.addImport(Annotation.class);
      clazz.addImport(Instance.class);
      clazz.addImport(AbstractBeanManager.class);

      ClassOrInterfaceType factory = new ClassOrInterfaceType();
      factory.setName("AbstractBeanManager");

      classDeclaration.getExtendedTypes().add(factory);
    }

    private void addFields() {
      addBeanInstance();
    }

    private void initInitMethod() {
      MethodDeclaration init = classDeclaration.addMethod("init", Modifier.Keyword.PRIVATE);

      TypeElement beanManager =
          generationContext.getElements().getTypeElement(BeanManager.class.getCanonicalName());
      generateInitEntry(init, beanManager);

      iocContext.getOrderedBeans().stream()
          .filter(field -> (field.getAnnotation(Application.class) == null))
          .filter(field -> field.getKind().equals(ElementKind.CLASS)).collect(Collectors.toSet())
          .forEach(field -> generateInitEntry(init, field));

      iocContext.getQualifiers()
          .forEach((type, beans) -> beans.forEach((annotation, definition) -> {
            if (definition.getType().getAnnotation(Named.class) == null) {
              generateInitEntry(init, type, definition.getType(), annotation);
            }
          }));
    }

    private void addGetInstanceMethod() {
      getMethodDeclaration =
          classDeclaration.addMethod("get", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
      getMethodDeclaration.setType(BeanManager.class.getSimpleName());
      addGetBody();
    }

    private void addBeanInstance() {
      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(BeanManager.class.getSimpleName() + "Impl");
      classDeclaration.addField(type, "instance", Modifier.Keyword.STATIC,
          Modifier.Keyword.PRIVATE);
    }

    private void generateInitEntry(MethodDeclaration init, TypeElement field) {
      generateInitEntry(init, field, field, null);
    }

    private void generateInitEntry(MethodDeclaration init, TypeElement field, TypeElement factory,
        String annotation) {
      if (!iocContext.getBlacklist().contains(field.getQualifiedName().toString())) {
        MethodCallExpr call = new MethodCallExpr(new ThisExpr(), "register")
            .addArgument(
                new FieldAccessExpr(new NameExpr(field.getQualifiedName().toString()), "class"))
            .addArgument(
                new MethodCallExpr(new NameExpr(Utils.getQualifiedFactoryName(factory)), "create"));
        if (annotation != null) {
          call.addArgument(annotation + ".class");
        }
        init.getBody().ifPresent(body -> body.addAndGetStatement(call));
      }
    }

    private void addGetBody() {
      NameExpr instance = new NameExpr("instance");
      IfStmt ifStmt = new IfStmt().setCondition(
          new BinaryExpr(instance, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
      BlockStmt blockStmt = new BlockStmt();
      blockStmt.addAndGetStatement(generateInstanceInitializer());
      blockStmt.addAndGetStatement(new MethodCallExpr(instance, "init"));
      ifStmt.setThenStmt(blockStmt);
      getMethodDeclaration.getBody().ifPresent(body -> body.addAndGetStatement(ifStmt));

      getMethodDeclaration.getBody()
          .ifPresent(body -> body.getStatements().add(new ReturnStmt(instance)));
    }

    protected Expression generateInstanceInitializer() {
      ObjectCreationExpr newInstance = new ObjectCreationExpr();
      newInstance
          .setType(new ClassOrInterfaceType().setName(BeanManager.class.getSimpleName() + "Impl"));
      return new AssignExpr().setTarget(new NameExpr("instance")).setValue(newInstance);
    }
  }
}
