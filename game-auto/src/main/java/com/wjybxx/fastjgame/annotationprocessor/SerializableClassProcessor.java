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
import com.squareup.javapoet.*;
import com.wjybxx.fastjgame.utils.AutoUtils;
import com.wjybxx.fastjgame.utils.BeanUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 1. 对于普通类：必须包含无参构造方法，且field注解的number必须在 0-65535之间
 * 2. 对于枚举：必须实现 NumericalEnum 接口，且提供非private的forNumber方法
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/23
 * github - https://github.com/hl845740757
 */
@AutoService(Processor.class)
public class SerializableClassProcessor extends AbstractProcessor {

    // 使用这种方式可以脱离对utils，net包的依赖
    private static final String SERIALIZABLE_CLASS_CANONICAL_NAME = "com.wjybxx.fastjgame.annotation.SerializableClass";
    private static final String SERIALIZABLE_FIELD_CANONICAL_NAME = "com.wjybxx.fastjgame.annotation.SerializableField";
    private static final String NUMBER_ENUM_CANONICAL_NAME = "com.wjybxx.fastjgame.enummapper.NumericalEnum";

    private static final String WIRETYPE_CANONICAL_NAME = "com.wjybxx.fastjgame.misc.WireType";
    private static final String SERIALIZER_CANONICAL_NAME = "com.wjybxx.fastjgame.misc.BeanSerializer";
    private static final String OUTPUT_STREAM_CANONICAL_NAME = "com.wjybxx.fastjgame.misc.BeanOutputStream";
    private static final String INPUT_STREAM_CANONICAL_NAME = "com.wjybxx.fastjgame.misc.BeanInputStream";
    private static final String CLONE_UTIL_CANONICAL_NAME = "com.wjybxx.fastjgame.misc.BeanCloneUtil";

    private static final String NUMBER_METHOD_NAME = "number";

    private static final String FOR_NUMBER_METHOD_NAME = "forNumber";
    private static final String GET_NUMBER_METHOD_NAME = "getNumber";

    private static final String WRITE_METHOD_NAME = "write";
    private static final String READ_METHOD_NAME = "read";
    private static final String CLONE_METHOD_NAME = "clone";
    private static final String FINDTYPE_METHOD_NAME = "findType";
    private static final String WIRE_TYPE_INT = "INT";

    // 工具类
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    private TypeElement serializableClassElement;
    private DeclaredType serializableFieldDeclaredType;
    private DeclaredType numericalEnumDeclaredType;

    private AnnotationSpec processorInfoAnnotation;
    private TypeName wireTypeTypeName;
    private TypeElement serializerTypeElement;
    private TypeElement outputStreamTypeElement;
    private TypeElement inputStreamTypeElement;
    private TypeElement cloneUtilTypeElement;

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
        return Collections.singleton(SERIALIZABLE_CLASS_CANONICAL_NAME);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return AutoUtils.SOURCE_VERSION;
    }

    private void ensureInited() {
        if (serializableClassElement != null) {
            return;
        }
        serializableClassElement = elementUtils.getTypeElement(SERIALIZABLE_CLASS_CANONICAL_NAME);
        serializableFieldDeclaredType = typeUtils.getDeclaredType(elementUtils.getTypeElement(SERIALIZABLE_FIELD_CANONICAL_NAME));
        numericalEnumDeclaredType = typeUtils.getDeclaredType(elementUtils.getTypeElement(NUMBER_ENUM_CANONICAL_NAME));

        wireTypeTypeName = TypeName.get(elementUtils.getTypeElement(WIRETYPE_CANONICAL_NAME).asType());
        serializerTypeElement = elementUtils.getTypeElement(SERIALIZER_CANONICAL_NAME);
        outputStreamTypeElement = elementUtils.getTypeElement(OUTPUT_STREAM_CANONICAL_NAME);
        inputStreamTypeElement = elementUtils.getTypeElement(INPUT_STREAM_CANONICAL_NAME);
        cloneUtilTypeElement = elementUtils.getTypeElement(CLONE_UTIL_CANONICAL_NAME);

        serializableClassElement = elementUtils.getTypeElement(SERIALIZABLE_CLASS_CANONICAL_NAME);
        serializableFieldDeclaredType = typeUtils.getDeclaredType(elementUtils.getTypeElement(SERIALIZABLE_FIELD_CANONICAL_NAME));
        numericalEnumDeclaredType = typeUtils.getDeclaredType(elementUtils.getTypeElement(NUMBER_ENUM_CANONICAL_NAME));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ensureInited();
        // 该注解只有类可以使用
        @SuppressWarnings("unchecked") final Set<TypeElement> allTypeElements = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(serializableClassElement);
        Set<TypeElement> sourceFileTypeElementSet = AutoUtils.selectSourceFile(allTypeElements, elementUtils);

        for (TypeElement typeElement : sourceFileTypeElementSet) {
            try {
                checkBase(typeElement);
                generateSerializer(typeElement);
            } catch (Throwable e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.toString(), typeElement);
            }
        }
        return true;
    }

    /**
     * 基础信息检查
     */
    private void checkBase(TypeElement typeElement) {
        if (typeElement.getKind() == ElementKind.ENUM) {
            // 检查真正的枚举
            checkEnum(typeElement);
        } else if (typeElement.getKind() == ElementKind.CLASS) {
            if (isNumericalEnum(typeElement)) {
                // 检查伪枚举
                checkForNumberMethod(typeElement);
            } else {
                // 检查普通类型
                checkClass(typeElement);
            }
        } else {
            // 只允许枚举和类使用，其它类型抛出编译错误
            messager.printMessage(Diagnostic.Kind.ERROR, "serializable class does not allow here", typeElement);
        }
    }

    private boolean isNumericalEnum(TypeElement typeElement) {
        return typeUtils.isSubtype(typeUtils.getDeclaredType(typeElement), numericalEnumDeclaredType);
    }

    private void checkClass(TypeElement typeElement) {
        final Set<Integer> numberSet = new HashSet<>(typeElement.getEnclosedElements().size());
        for (Element element : typeElement.getEnclosedElements()) {
            // 非成员属性
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            // 该注解只有Field可以使用
            final VariableElement variableElement = (VariableElement) element;
            // 查找该字段上的注解
            final Optional<? extends AnnotationMirror> fieldAnnotation = getFieldAnnotation(variableElement);
            // 该成员属性没有serializableField注解
            if (!fieldAnnotation.isPresent()) {
                continue;
            }
            // 不能是static
            if (variableElement.getModifiers().contains(Modifier.STATIC)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "serializable field can't be static", variableElement);
                continue;
            }
            // value中，基本类型会被封装为包装类型，number是int类型
            final Integer number = AutoUtils.getAnnotationValueNotDefault(fieldAnnotation.get(), NUMBER_METHOD_NAME);
            // 取值范围检测
            if (number == null || number < 0 || number > 65535) {
                messager.printMessage(Diagnostic.Kind.ERROR, "number " + number + " must between [0, 65535]", variableElement);
                continue;
            }
            // 重复检测
            if (!numberSet.add(number)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "number " + number + " is duplicate!", variableElement);
            }
        }

        // 无参构造方法检测
        if (!BeanUtils.containsNoArgsConstructor(typeElement)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "SerializableClass " + typeElement.getSimpleName() + " must contains no-args constructor, private is ok!",
                    typeElement);
        }
    }

    private Optional<? extends AnnotationMirror> getFieldAnnotation(VariableElement variableElement) {
        return AutoUtils.findFirstAnnotationWithoutInheritance(typeUtils, variableElement, serializableFieldDeclaredType);
    }

    /**
     * 检查带注解的枚举类是否有 forNumber 方法 和 是否实现了数值枚举接口
     */
    private void checkEnum(TypeElement typeElement) {
        // 要序列化的枚举必须实现 NumericalEnum 接口
        if (!isNumericalEnum(typeElement)) {
            messager.printMessage(Diagnostic.Kind.ERROR, typeElement.getSimpleName() + " must implements NumericalEnum!", typeElement);
        }
        checkForNumberMethod(typeElement);
    }

    /**
     * 检查forNumber静态方法
     */
    private void checkForNumberMethod(TypeElement typeElement) {
        if (!isContainStaticForNumberMethod(typeElement)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s must contains a not private 'static %s forNumber(int)' method!", typeElement.getSimpleName(), typeElement.getSimpleName()),
                    typeElement);
        }
    }

    /**
     * 是否包含静态的forNumber方法 - static T forNumber(int)
     */
    private boolean isContainStaticForNumberMethod(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> (ExecutableElement) e)
                .filter(method -> !method.getModifiers().contains(Modifier.PRIVATE))
                .filter(method -> method.getModifiers().contains(Modifier.STATIC))
                .filter(method -> method.getSimpleName().toString().equals(FOR_NUMBER_METHOD_NAME))
                .filter(method -> method.getParameters().size() == 1)
                .anyMatch(method -> method.getParameters().get(0).asType().getKind() == TypeKind.INT);
    }

    // ----------------------------------------------- 辅助类生成 -------------------------------------------

    private void generateSerializer(TypeElement typeElement) {
        if (typeElement.getKind() == ElementKind.CLASS) {
            if (isNumericalEnum(typeElement)) {
                genEnumSerializer(typeElement);
            } else {
                tryGenClassSerializer(typeElement);
            }
        } else if (typeElement.getKind() == ElementKind.ENUM) {
            genEnumSerializer(typeElement);
        }
    }

    private void tryGenClassSerializer(final TypeElement typeElement) {
        if (isJavaBean(typeElement)) {
            genClassSerializerImp(typeElement);
        }
    }

    /**
     * 是否标准的javaBean类
     * 1. 必须非Private有无参构造方法
     * 2. 所有要序列化字段必须有对应的非private的 getter setter (必须满足命名规范)
     */
    private boolean isJavaBean(TypeElement typeElement) {
        final ExecutableElement noArgsConstructor = BeanUtils.getNoArgsConstructor(typeElement);
        if (null == noArgsConstructor || noArgsConstructor.getModifiers().contains(Modifier.PRIVATE)) {
            return false;
        }

        for (Element element : typeElement.getEnclosedElements()) {
            // 非成员属性
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            final VariableElement variableElement = (VariableElement) element;
            final Optional<? extends AnnotationMirror> fieldAnnotation = getFieldAnnotation(variableElement);

            // 该成员属性没有serializableField注解
            if (!fieldAnnotation.isPresent()) {
                continue;
            }

            TypeName fieldTypeName = TypeName.get(variableElement.asType());
            if (fieldTypeName.isPrimitive()) {
                fieldTypeName.box();
            }

            final String getterMethodName = BeanUtils.getterMethodName(variableElement.getSimpleName().toString(), BeanUtils.isBoolean(fieldTypeName));
            final ExecutableElement getterMethod = AutoUtils.findMethodByName(typeElement, getterMethodName);
            if (null == getterMethod || getterMethod.getModifiers().contains(Modifier.PRIVATE)) {
                return false;
            }

            final String setterMethodName = BeanUtils.setterMethodName(variableElement.getSimpleName().toString(), BeanUtils.isBoolean(fieldTypeName));
            final ExecutableElement setterMethod = AutoUtils.findMethodByName(typeElement, setterMethodName);
            if (null == setterMethod || setterMethod.getModifiers().contains(Modifier.PRIVATE)) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------- javaBean代码生成 ---------------------------------------------------

    private void genClassSerializerImp(TypeElement typeElement) {
        final TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(getSerializerClassName(typeElement));
        final CodeBlock.Builder wireTypeStaticCodeBlock = CodeBlock.builder();

        final TypeName instanceRawTypeName = TypeName.get(typeUtils.erasure(typeElement.asType()));
        final MethodSpec.Builder writeMethodBuilder = newWriteMethodBuilder(instanceRawTypeName);

        final MethodSpec.Builder readMethodBuilder = newReadMethodBuilder(instanceRawTypeName);
        readMethodBuilder.addStatement("$T instance = new $T()", instanceRawTypeName, instanceRawTypeName);

        final MethodSpec.Builder cloneMethodBuilder = newCloneMethodBuilder(instanceRawTypeName);
        cloneMethodBuilder.addStatement("$T result = new $T()", instanceRawTypeName, instanceRawTypeName);

        for (Element element : typeElement.getEnclosedElements()) {
            // 非成员属性
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            // 该注解只有Field可以使用
            final VariableElement variableElement = (VariableElement) element;
            // 查找该字段上的注解
            final Optional<? extends AnnotationMirror> fieldAnnotation = getFieldAnnotation(variableElement);
            // 该成员属性没有serializableField注解
            if (!fieldAnnotation.isPresent()) {
                continue;
            }

            // value中，基本类型会被封装为包装类型，number是int类型
            final Integer number = AutoUtils.getAnnotationValueNotDefault(fieldAnnotation.get(), SerializableClassProcessor.NUMBER_METHOD_NAME);

            TypeName fieldRawTypeName = ParameterizedTypeName.get(typeUtils.erasure(variableElement.asType()));
            if (fieldRawTypeName.isPrimitive()) {
                fieldRawTypeName = fieldRawTypeName.box();
            }

            typeBuilder.addField(byte.class, "wireType" + number, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            wireTypeStaticCodeBlock.addStatement("wireType$L = $T.$L($T.class)", number, wireTypeTypeName, FINDTYPE_METHOD_NAME, fieldRawTypeName);

            final String getterName = BeanUtils.getterMethodName(variableElement.getSimpleName().toString(), BeanUtils.isBoolean(fieldRawTypeName));
            final String setterName = BeanUtils.setterMethodName(variableElement.getSimpleName().toString(), BeanUtils.isBoolean(fieldRawTypeName));

            writeMethodBuilder.addStatement("outputStream.writeObject(wireType$L, instance.$L())", number, getterName);
            readMethodBuilder.addStatement("instance.$L(inputStream.readObject(wireType$L))", setterName, number);
            cloneMethodBuilder.addStatement("result.$L(util.clone(wireType$L, instance.$L()))", setterName, number, getterName);
        }

        readMethodBuilder.addStatement("return instance");
        cloneMethodBuilder.addStatement("return result");

        typeBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AutoUtils.SUPPRESS_UNCHECKED_ANNOTATION)
                .addAnnotation(processorInfoAnnotation)
                .addSuperinterface(TypeName.get(typeUtils.getDeclaredType(serializerTypeElement, typeUtils.erasure(typeElement.asType()))))
                .addStaticBlock(wireTypeStaticCodeBlock.build())
                .addMethod(writeMethodBuilder.build())
                .addMethod(readMethodBuilder.build())
                .addMethod(cloneMethodBuilder.build());

        // 写入文件
        AutoUtils.writeToFile(typeElement, typeBuilder, elementUtils, messager, filer);
    }

    private MethodSpec.Builder newWriteMethodBuilder(TypeName instanceRawTypeName) {
        return MethodSpec.methodBuilder(WRITE_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addException(IOException.class)
                .addParameter(instanceRawTypeName, "instance")
                .addParameter(TypeName.get(outputStreamTypeElement.asType()), "outputStream");
    }

    private MethodSpec.Builder newReadMethodBuilder(TypeName instanceRawTypeName) {
        return MethodSpec.methodBuilder(READ_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addException(IOException.class)
                .returns(instanceRawTypeName)
                .addParameter(TypeName.get(inputStreamTypeElement.asType()), "inputStream");
    }

    private MethodSpec.Builder newCloneMethodBuilder(TypeName instanceRawTypeName) {
        return MethodSpec.methodBuilder(CLONE_METHOD_NAME)
                .returns(instanceRawTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addException(IOException.class)
                .addParameter(instanceRawTypeName, "instance")
                .addParameter(TypeName.get(cloneUtilTypeElement.asType()), "util");
    }

    /**
     * 获取class对应的序列化工具类的类名
     */
    private String getSerializerClassName(TypeElement typeElement) {
        return getSerializerName(typeElement.getSimpleName().toString());
    }

    private static String getSerializerName(String className) {
        return className + "Serializer";
    }

    // ---------------------------------------------------------- 枚举序列化生成 ---------------------------------------------------

    private void genEnumSerializer(TypeElement typeElement) {
        final TypeName instanceRawTypeName = TypeName.get(typeUtils.erasure(typeElement.asType()));

        // 写入number即可 outputStream.writeObject(WireType.INT, instance.getNumber())
        final MethodSpec.Builder writeMethodBuilder = newWriteMethodBuilder(instanceRawTypeName);
        writeMethodBuilder.addStatement("outputStream.writeObject($T.$L, instance.$L())", wireTypeTypeName, WIRE_TYPE_INT, GET_NUMBER_METHOD_NAME);

        // 读取number即可 return A.forNumber(inputStream.readObject(WireType.INT))
        final MethodSpec.Builder readMethodBuilder = newReadMethodBuilder(instanceRawTypeName);
        readMethodBuilder.addStatement("return $T.$L(inputStream.readObject($T.$L))", instanceRawTypeName, FOR_NUMBER_METHOD_NAME, wireTypeTypeName, WIRE_TYPE_INT);

        // 枚举，直接返回对象
        final MethodSpec.Builder cloneMethodBuilder = newCloneMethodBuilder(instanceRawTypeName);
        cloneMethodBuilder.addStatement("return instance");

        final TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(getSerializerClassName(typeElement));
        typeBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AutoUtils.SUPPRESS_UNCHECKED_ANNOTATION)
                .addAnnotation(processorInfoAnnotation)
                .addSuperinterface(TypeName.get(typeUtils.getDeclaredType(serializerTypeElement, typeUtils.erasure(typeElement.asType()))))
                .addMethod(writeMethodBuilder.build())
                .addMethod(readMethodBuilder.build())
                .addMethod(cloneMethodBuilder.build());

        // 写入文件
        AutoUtils.writeToFile(typeElement, typeBuilder, elementUtils, messager, filer);
    }

    // ---------------------------------------------------------- 分割线 ---------------------------------------------------

    /**
     * 获取序列化工具类的完整类型
     * 暴露给应用层的接口
     */
    public static String getSerializerBinaryName(Class<?> messageClass) {
        return messageClass.getPackageName() + "." + getSerializerName(messageClass.getSimpleName());
    }

}
