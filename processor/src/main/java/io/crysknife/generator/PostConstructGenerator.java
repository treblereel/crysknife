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

package io.crysknife.generator;

import javax.annotation.PostConstruct;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.annotation.Generator;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.definition.ExecutableDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
@Generator(priority = 100000)
public class PostConstructGenerator extends IOCGenerator {

  public PostConstructGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(PostConstruct.class, WiringElementType.METHOD_DECORATOR, this);
  }

  public void generateBeanFactory(ClassBuilder builder, Definition definition) {
    if (definition instanceof ExecutableDefinition) {
      ExecutableDefinition postConstruct = checkPostConstruct(definition);
      if (isPrivate(definition)) {
        if (iocContext.getGenerationContext().isJre()) {
          TryStmt ts = new TryStmt();
          BlockStmt blockStmt = new BlockStmt();
          ts.setTryBlock(blockStmt);

          CatchClause catchClause = new CatchClause().setParameter(new Parameter()
              .setType(new ClassOrInterfaceType().setName("Exception")).setName("e"));
          ts.getCatchClauses().add(catchClause);

          blockStmt.addAndGetStatement(
              new MethodCallExpr(new NameExpr("org.apache.commons.lang3.reflect.MethodUtils"),
                  "invokeMethod").addArgument("instance").addArgument("true")
                      .addArgument(new StringLiteralExpr(
                          postConstruct.getExecutableElement().getSimpleName().toString())));
          builder.getGetMethodDeclaration().getBody().get().addAndGetStatement(ts);
        }
      } else {
        FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
        MethodCallExpr method = new MethodCallExpr(instance,
            postConstruct.getExecutableElement().getSimpleName().toString());
        builder.getGetMethodDeclaration().getBody().get().addAndGetStatement(method);
      }
    }
  }

  private ExecutableDefinition checkPostConstruct(Definition definition) {
    ExecutableDefinition postConstruct = (ExecutableDefinition) definition;

    if (!postConstruct.getExecutableElement().getReturnType().getKind().equals(TypeKind.VOID)) {
      throw new GenerationException("@PostConstruct method ["
          + postConstruct.getExecutableElement().getSimpleName() + "] at ["
          + postConstruct.getExecutableElement().getEnclosingElement() + "] must be void.");
    }

    if (!postConstruct.getExecutableElement().getParameters().isEmpty()) {
      throw new GenerationException(
          "@PostConstruct method [" + postConstruct.getExecutableElement().getSimpleName()
              + "] at [" + postConstruct.getExecutableElement().getEnclosingElement()
              + "] shouldn't have parameters");
    }

    return postConstruct;
  }

  private boolean isPrivate(Definition definition) {
    return ((ExecutableDefinition) definition).getExecutableElement().getModifiers()
        .contains(Modifier.PRIVATE);
  }

}
