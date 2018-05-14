package com.hzc.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by huang zong cheng on 2017/12/13.
 * 328854225@qq.com
 */

public class BuildInjectModuleCode {

//    private static final MethodSpec createDestroy(String fullClassName, List<Element> fields) {
//        String packageName = fullClassName.substring(0, fullClassName.lastIndexOf("."));
//        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1, fullClassName.length());
//        MethodSpec.Builder builder = MethodSpec.methodBuilder("destroy");
//        builder.returns(void.class);
//        builder.addModifiers(Modifier.PUBLIC);
//        builder.addParameter(ClassName.get(packageName, className), "activity");
//        for (Element e : fields) {
//            builder.addStatement("activity.$N.destroy()", e.getSimpleName());
//        }
//        return builder.build();
//    }

    private static final MethodSpec createConstructor(String fullClassName, List<Element> fields) {
        String packageName = fullClassName.substring(0, fullClassName.lastIndexOf("."));
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1, fullClassName.length());
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addParameter(ClassName.get(packageName, className), "activity");
        for (int k = 0; k < fields.size(); k++) {

            builder.addCode("{");

            Element e = fields.get(k);
            Map map = e.getAnnotationMirrors().get(0).getElementValues();

            builder.addStatement("if($T.getInstance().get($S).contains(\",\")) throw new NullPointerException(\"There are multiple implementations of the $N's Service, and please specify the current implementation class . like @Service(Class)\")"
                    , ClassName.get(HzcInjectProcess.PACKAGE_NAME, HzcInjectProcess.CLASS_NAME), e.asType().toString(), e.asType().toString());


            builder.addStatement("Class cls = Thread.currentThread().getContextClassLoader().loadClass($T.getInstance().get($S))", ClassName.get(HzcInjectProcess.PACKAGE_NAME, HzcInjectProcess.CLASS_NAME), e.asType().toString());

            builder.addStatement("$N[] constructors = cls.getConstructors()", Constructor.class.getName());
            builder.addCode("if(constructors == null){");
            builder.addStatement("throw new NullPointerException(String.format(\"you must have a default public Constructor with %s \",activity.getClass().getName()))");
            builder.addCode("}");


            if (map.size() > 0) {
                builder.addCode("for (int i = 0; i < constructors.length; i++) {");
                builder.addStatement("$N constructor = constructors[i]", Constructor.class.getName());
                int paramsSize = 0;
                StringBuffer temp = new StringBuffer();
                temp.append("activity.$N = ($N)constructor.newInstance(");
                boolean doSub = false;
                for (Object key : map.keySet()) {
                    if (key.toString().equalsIgnoreCase("params()")) {
                        String[] params = map.get(key).toString().replace("\"", "").replace("{", "").replace("}", "").split(",");
                        paramsSize = params.length;
                        for (int i = 0; i < params.length; i++) {
                            if (params[i] != null && !params[i].isEmpty()) {
                                temp.append(String.format("activity.%s,", params[i]));
                                doSub = true;
                            }
                        }
                    }
                }
                if (doSub) {
                    temp = new StringBuffer(temp.substring(0, temp.length() - 1));
                }
                temp.append(")");
                builder.addCode(String.format("if(constructor.getParameterTypes().length == %d){", paramsSize));
                builder.addStatement(temp.toString(), e.getSimpleName(), e.asType().toString());
                builder.addStatement("return");
                builder.addCode("}");
                builder.addCode("}");
            }

            builder.addCode("int index = -1;");
            builder.addCode("for (int i = 0; i < constructors.length; i++) {");
            builder.addCode("if(constructors[i].getParameterTypes().length == 0){");
            builder.addCode("index = i;break;");
            builder.addCode("}");
            builder.addCode("}");
            builder.addCode("if(index == -1){throw new NullPointerException(String.format(\"you must have a default public Constructor with %s \", activity.getClass().getName()));}");

            builder.addStatement("activity.$N = ($N)cls.getConstructors()[0].newInstance()",
                    e.getSimpleName(), e.asType().toString());


            builder.addCode("}");
        }
        return builder.build();
    }

    public static final TypeSpec createInjectClass(String fullClassName, List<Element> fields) {
        return TypeSpec.classBuilder("Inject$" + fullClassName.replace(".", "$"))
                .addMethod(createConstructor(fullClassName, fields))
//                .addMethod(createDestroy(fullClassName, fields))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();
    }
}
