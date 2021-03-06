/*
 *  Copyright 2019 wjybxx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to iBn writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.wjybxx.fastjgame.annotationprocessor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wjybxx.fastjgame.utils.AutoUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 为带有{@code Subscribe}注解的方法生成代理。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/23
 * github - https://github.com/hl845740757
 */
@AutoService(Processor.class)
public class EventSubscribeProcessor extends AbstractProcessor {

    private static final String REGISTRY_CANONICAL_NAME = "com.wjybxx.fastjgame.eventbus.EventHandlerRegistry";
    private static final String SUBSCRIBE_CANONICAL_NAME = "com.wjybxx.fastjgame.eventbus.Subscribe";
    private static final String GENERIC_EVENT_CANONICAL_NAME = "com.wjybxx.fastjgame.eventbus.GenericEvent";

    private static final String SUB_EVENTS_METHOD_NAME = "subEvents";
    private static final String ONLY_SUB_EVENTS_METHOD_NAME = "onlySubEvents";

    // 工具类
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    private AnnotationSpec processorInfoAnnotation;

    private TypeElement subscribeTypeElement;
    private DeclaredType subscribeDeclaredType;
    private DeclaredType genericEventDeclaredType;

    private TypeName registryTypeName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();

        processorInfoAnnotation = AutoUtils.newProcessorInfoAnnotation(getClass());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(SUBSCRIBE_CANONICAL_NAME);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return AutoUtils.SOURCE_VERSION;
    }

    /**
     * 尝试初始化环境，也就是依赖的类都已经出现
     */
    private void ensureInited() {
        if (subscribeTypeElement != null) {
            // 已初始化
            return;
        }

        subscribeTypeElement = elementUtils.getTypeElement(SUBSCRIBE_CANONICAL_NAME);
        subscribeDeclaredType = typeUtils.getDeclaredType(subscribeTypeElement);
        genericEventDeclaredType = typeUtils.getDeclaredType(elementUtils.getTypeElement(GENERIC_EVENT_CANONICAL_NAME));

        registryTypeName = TypeName.get(elementUtils.getTypeElement(REGISTRY_CANONICAL_NAME).asType());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ensureInited();

        // 只有方法可以带有该注解 METHOD只有普通方法，不包含构造方法， 按照外部类进行分类
        final Map<Element, ? extends List<? extends Element>> class2MethodsMap = roundEnv.getElementsAnnotatedWith(subscribeTypeElement).stream()
                .collect(Collectors.groupingBy(Element::getEnclosingElement));

        class2MethodsMap.forEach((element, object) -> {
            try {
                genProxyClass((TypeElement) element, object);
            } catch (Throwable e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.toString(), element);
            }
        });
        return true;
    }

    private void genProxyClass(TypeElement typeElement, List<? extends Element> methodList) {
        final TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(getProxyClassName(typeElement))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AutoUtils.SUPPRESS_UNCHECKED_ANNOTATION)
                .addAnnotation(processorInfoAnnotation)
                .addMethod(genRegisterMethod(typeElement, methodList));

        // 写入文件
        AutoUtils.writeToFile(typeElement, typeBuilder, elementUtils, messager, filer);
    }

    private static String getProxyClassName(TypeElement typeElement) {
        return typeElement.getSimpleName().toString() + "BusRegister";
    }

    private MethodSpec genRegisterMethod(TypeElement typeElement, List<? extends Element> methodList) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(registryTypeName, "registry")
                .addParameter(TypeName.get(typeElement.asType()), "instance");

        for (Element element : methodList) {
            final ExecutableElement method = (ExecutableElement) element;

            if (method.getModifiers().contains(Modifier.STATIC)) {
                // 不可以是静态方法
                messager.printMessage(Diagnostic.Kind.ERROR, "Subscribe method can't be static！", method);
                continue;
            }
            if (method.getModifiers().contains(Modifier.PRIVATE)) {
                // 访问权限不可以是private - 由于生成的类和该类属于同一个包，因此不必public，只要不是private即可
                messager.printMessage(Diagnostic.Kind.ERROR, "Subscribe method can't be private！", method);
                continue;
            }

            if (method.getParameters().size() != 1) {
                // 保证 1个参数
                messager.printMessage(Diagnostic.Kind.ERROR, "Subscribe method must have one and only one parameter!", method);
                continue;
            }

            final VariableElement eventParameter = method.getParameters().get(0);
            if (!isClassOrInterface(eventParameter.asType())) {
                // 事件参数必须是类或接口 (就不会是基本类型或泛型参数了，也排除了数组类型)
                messager.printMessage(Diagnostic.Kind.ERROR, "EventType must be class or interface!", method);
                continue;
            }

            if (isGenericEvent(eventParameter)) {
                registerGenericHandlers(builder, method);
            } else {
                registerNormalHandlers(builder, method);
            }
        }

        return builder.build();
    }

    /**
     * 判断是否是接口或类型
     */
    private static boolean isClassOrInterface(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.DECLARED;
    }

    /**
     * 判断监听的是否是泛型事件类型
     */
    private boolean isGenericEvent(VariableElement eventParameter) {
        return AutoUtils.isTargetDeclaredType(eventParameter, declaredType -> typeUtils.isSubtype(declaredType, genericEventDeclaredType));
    }

    /**
     * 注册泛型事件处理器
     */
    private void registerGenericHandlers(MethodSpec.Builder builder, ExecutableElement method) {
        final VariableElement eventParameter = method.getParameters().get(0);
        final TypeName parentEventRawTypeName = TypeName.get(getEventParameterRawTypeMirror(eventParameter));
        final Set<TypeMirror> eventTypes = collectEventTypes(method, getGenericEventTypeArgument(eventParameter));

        for (TypeMirror typeMirror : eventTypes) {
            builder.addStatement("registry.register($T.class, $T.class, event -> instance.$L(event))",
                    parentEventRawTypeName,
                    TypeName.get(typeMirror),
                    method.getSimpleName().toString());
        }
    }

    /**
     * 注册普通事件处理器
     */
    private void registerNormalHandlers(MethodSpec.Builder builder, ExecutableElement method) {
        final VariableElement eventParameter = method.getParameters().get(0);
        final TypeName parentEventRawTypeName = TypeName.get(getEventParameterRawTypeMirror(eventParameter));
        final Set<TypeMirror> eventTypes = collectEventTypes(method, eventParameter.asType());

        for (TypeMirror typeMirror : eventTypes) {
            if (isSameTypeIgnoreTypeParameter(typeMirror, eventParameter.asType())) {
                // 声明类型
                builder.addStatement("registry.register($T.class, event -> instance.$L(event))",
                        parentEventRawTypeName,
                        method.getSimpleName().toString());
            } else {
                // 子类型需要显示转为超类型 - 否则可能导致重载问题
                builder.addStatement("registry.register($T.class, event -> instance.$L(($T) event))",
                        TypeName.get(typeMirror),
                        method.getSimpleName().toString(),
                        parentEventRawTypeName);
            }
        }
    }

    /**
     * 获取事件参数的声明类型
     */
    private TypeMirror getEventParameterRawTypeMirror(VariableElement eventParameter) {
        return eventParameter.asType().accept(new SimpleTypeVisitor8<>() {
            @Override
            public TypeMirror visitDeclared(DeclaredType t, Object o) {
                return typeUtils.erasure(t);
            }
        }, null);
    }

    /**
     * 查询是否只监听子类型参数
     */
    private Boolean isOnlySubEvents(AnnotationMirror annotationMirror) {
        return AutoUtils.getAnnotationValueWithDefaults(elementUtils, annotationMirror, ONLY_SUB_EVENTS_METHOD_NAME);
    }

    /**
     * 搜集types属性对应的事件类型
     * 注意查看{@link AnnotationValue}的类文档
     */
    private Set<TypeMirror> collectEventTypes(final ExecutableElement method, final TypeMirror parentTypeMirror) {
        final AnnotationMirror annotationMirror = AutoUtils.findFirstAnnotationWithoutInheritance(typeUtils, method, subscribeDeclaredType).orElse(null);
        assert annotationMirror != null;

        final Set<TypeMirror> result = new HashSet<>();
        if (!isOnlySubEvents(annotationMirror)) {
            result.add(getEventRawType(parentTypeMirror));
        }

        final List<? extends AnnotationValue> subEventsList = AutoUtils.getAnnotationValueNotDefault(annotationMirror, SUB_EVENTS_METHOD_NAME);
        if (null == subEventsList) {
            return result;
        }

        for (final AnnotationValue annotationValue : subEventsList) {
            final TypeMirror subEventTypeMirror = getSubEventTypeMirror(annotationValue);
            if (null == subEventTypeMirror) {
                // 无法获取参数
                messager.printMessage(Diagnostic.Kind.ERROR, "Unsupported type " + annotationValue, method);
                continue;
            }

            if (!isSubTypeIgnoreTypeParameter(subEventTypeMirror, parentTypeMirror)) {
                // 不是监听参数的子类型 - 带有泛型参数的 isSubType为false
                messager.printMessage(Diagnostic.Kind.ERROR, subEventTypeMirror.toString() + " is not " + getEventRawType(parentTypeMirror).toString() + "'s subType", method);
                continue;
            }

            if (result.stream().anyMatch(typeMirror -> isSameTypeIgnoreTypeParameter(typeMirror, subEventTypeMirror))) {
                // 重复
                messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate type " + subEventTypeMirror.toString(), method);
                continue;
            }

            result.add(typeUtils.erasure(subEventTypeMirror));
        }
        return result;
    }

    private TypeMirror getEventRawType(TypeMirror eventTypeMirror) {
        return typeUtils.erasure(eventTypeMirror);
    }

    private static TypeMirror getSubEventTypeMirror(AnnotationValue annotationValue) {
        return annotationValue.accept(new SimpleAnnotationValueVisitor8<TypeMirror, Object>() {
            @Override
            public TypeMirror visitType(TypeMirror t, Object o) {
                return t;
            }
        }, null);
    }

    private boolean isSubTypeIgnoreTypeParameter(TypeMirror a, TypeMirror b) {
        return typeUtils.isSubtype(typeUtils.erasure(a), typeUtils.erasure(b));
    }

    private boolean isSameTypeIgnoreTypeParameter(TypeMirror a, TypeMirror b) {
        return typeUtils.isSameType(typeUtils.erasure(a), typeUtils.erasure(b));
    }

    /**
     * 获取泛型事件的泛型参数
     */
    private TypeMirror getGenericEventTypeArgument(final VariableElement genericEventVariableElement) {
        final TypeMirror result = AutoUtils.getFirstParameterActualType(genericEventVariableElement.asType());
        if (result == null || result.getKind() != TypeKind.DECLARED) {
            messager.printMessage(Diagnostic.Kind.ERROR, "GenericEvent has a bad type parameter!", genericEventVariableElement);
        }
        return result;
    }
}
