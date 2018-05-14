package com.hzc.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * Created by huang zong cheng on 2017/12/13.
 * 328854225@qq.com
 */

public class BuildServiceCode {
    public static final TypeSpec createInnerClass() {
        FieldSpec hzcInject = FieldSpec.builder(ClassName.get(HzcInjectProcess.PACKAGE_NAME, HzcInjectProcess.CLASS_NAME), "hzcInject", Modifier.STATIC)
                .initializer("new $T()", ClassName.get(HzcInjectProcess.PACKAGE_NAME, HzcInjectProcess.CLASS_NAME))
                .build();
        return TypeSpec.classBuilder("InnerClass").addModifiers(Modifier.STATIC).addField(hzcInject).build();
    }

    public static final MethodSpec createConstructor() {
        return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
    }


    public static final MethodSpec createStaticInstance() {
        return MethodSpec.methodBuilder("getInstance").addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(ClassName.get(HzcInjectProcess.PACKAGE_NAME, HzcInjectProcess.CLASS_NAME)).addStatement("return InnerClass.hzcInject").build();
    }


    public static final MethodSpec createInitService(List<String> serviceList) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("infoService");
        builder.addModifiers().addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.returns(ParameterizedTypeName.get(LinkedHashMap.class, String.class, String.class));
        builder.addCode("$T<String> serviceList = new $T();", List.class, ArrayList.class);
        for (String serviceStr : serviceList) {
            builder.addCode("serviceList.add($S);", serviceStr);
        }
        builder.addCode("for(String impcls : serviceList){");
        builder.addCode("try{");
        builder.addCode("Class[] clses = Class.forName(impcls).getInterfaces();");
        builder.addCode("for(Class cls : clses){" +
                "String key = cls.getName().replace($S, \".\");" +
                "String value = getInstance().get(key);" +
                "if(value == null){value = \"\";}else{value += \",\";}value += impcls;" +
                "getInstance().put(key,value);" +
                "}", "$");
        builder.addCode("} catch ($T e) {e.printStackTrace();}", Exception.class);
        builder.addCode("}");
        builder.addCode("return getInstance();");
        return builder.build();
    }

    public static final MethodSpec creaqteInject() {
        //"com.hzc.inject.model." + context.getClass().getName() + ".Inject$" + context.getClass().getSimpleName()


        return MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(void.class)
                .addParameter(Object.class, "context")
                .addCode("try{")
                .addCode("String name = $S + context.getClass().getName().replace(\".\",$S);", HzcInjectProcess.PACKAGE_NAME + ".model.Inject$", "$")
                .addCode("Thread.currentThread().getContextClassLoader().loadClass(name).getConstructors()[0].newInstance(context);", HzcInjectProcess.PACKAGE_NAME)
                .addCode("} catch ($T e) {e.printStackTrace();} catch ($T e) {e.printStackTrace();} catch ($T e) {e.printStackTrace();} catch ($T e) {e.printStackTrace();}", InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class, ClassNotFoundException.class)
                .build();
    }
}
