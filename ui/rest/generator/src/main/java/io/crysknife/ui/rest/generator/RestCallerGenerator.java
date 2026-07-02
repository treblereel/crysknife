/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.ui.rest.generator;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.TypeUtils;
import org.treblereel.gwt.rest.client.Caller;
import org.treblereel.gwt.rest.client.RestConfig;

@Generator(priority = 100001)
public class RestCallerGenerator extends IOCGenerator<BeanDefinition> {

    public RestCallerGenerator(TreeLogger treeLogger, IOCContext iocContext) {
        super(treeLogger, iocContext);
    }

    @Override
    public void register() {
        iocContext.register(Inject.class, Caller.class, WiringElementType.FIELD_TYPE, this);
        iocContext.getBuildIn().add(Caller.class.getCanonicalName());
    }

    @Override
    public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
        DeclaredType declaredType = (DeclaredType) fieldPoint.getVariableElement().asType();
        TypeMirror typeArg = declaredType.getTypeArguments().get(0);
        String callerImplQualifiedName = typeArg.toString() + "_RestCaller";

        MethodCallExpr lookupCall = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                .addArgument(new FieldAccessExpr(
                        new NameExpr(RestConfig.class.getCanonicalName()), "class"));

        List<AnnotationMirror> qualifiers = new ArrayList<>(
                TypeUtils.getAllElementQualifierAnnotations(iocContext,
                        fieldPoint.getVariableElement()));
        for (AnnotationMirror qualifier : qualifiers) {
            lookupCall.addArgument(generationUtils.createQualifierExpression(qualifier));
        }

        Named named = fieldPoint.getVariableElement().getAnnotation(Named.class);
        if (named != null) {
            lookupCall.addArgument(new MethodCallExpr(
                    new NameExpr("io.crysknife.client.internal.QualifierUtil"), "createNamed")
                    .addArgument(new StringLiteralExpr(named.value())));
        }

        MethodCallExpr lookupRestConfig = new MethodCallExpr(lookupCall, "getInstance");

        ObjectCreationExpr newCaller = new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType(null, callerImplQualifiedName))
                .addArgument(lookupRestConfig);

        return generationUtils.wrapCallInstanceImpl(newCaller).toString();
    }
}
