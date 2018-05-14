package com.hzc.process;

import com.hzc.annotation.Autoware;
import com.hzc.annotation.Service;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;


/**
 * Created by huang zong cheng on 2017/12/12.
 * 328854225@qq.com
 */
@SupportedAnnotationTypes({"com.hzc.annotation.Service", "com.hzc.annotation.Autoware"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class HzcInjectProcess extends AbstractProcessor {


    ProcessingEnvironment processingEnvironment;

    public static String PACKAGE_NAME = "com.hzc.inject";
    public static final String CLASS_NAME = "HzcInject";
    public static String projectName = "";


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.processingEnvironment = processingEnvironment;
    }

    private String StringFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}-]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            if (projectName.isEmpty()) {
                List<Element> originatingElements = new ArrayList<>();
                Random rm = new Random();
                JavaFileObject filerSourceFile = processingEnv.getFiler().createSourceFile("com.a.a" + rm.nextInt(10000), originatingElements.toArray(new Element[originatingElements.size()]));
                projectName = filerSourceFile.toUri().getPath();
                projectName = projectName.substring(0, projectName.indexOf("build/generated/source") - 1);
                projectName = projectName.substring(projectName.lastIndexOf("/") + 1);
                PACKAGE_NAME += "." + StringFilter(projectName);
            }
        } catch (Exception e) {

        }
        List<JavaFile> javaFiles = new ArrayList<>();
        List<String> serviceList = new ArrayList<>();
        for (Element p : roundEnvironment.getElementsAnnotatedWith(Service.class)) {
            try {
                serviceList.add(p.toString());
            } catch (Exception e) {

            }
        }
        TypeSpec hzcInject = TypeSpec.classBuilder(CLASS_NAME)
                .superclass(ParameterizedTypeName.get(LinkedHashMap.class, String.class, String.class))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(BuildServiceCode.createConstructor())
                .addMethod(BuildServiceCode.creaqteInject())
                .addMethod(BuildServiceCode.createStaticInstance())
                .addType(BuildServiceCode.createInnerClass())
                .addMethod(BuildServiceCode.createInitService(serviceList)).build();


        javaFiles.add(JavaFile.builder(PACKAGE_NAME, hzcInject).build());


        Map<String, List<Element>> activitys = new HashMap<>();
        for (Element p : roundEnvironment.getElementsAnnotatedWith(Autoware.class)) {
            String key = p.getEnclosingElement().toString();
            List<Element> elementList = activitys.get(key);
            if (elementList == null)
                elementList = new ArrayList<>();
            elementList.add(p);
            activitys.put(key, elementList);
        }

        for (String key : activitys.keySet()) {
            List<Element> elementList = activitys.get(key);
            javaFiles.add(JavaFile.builder(PACKAGE_NAME + ".model", BuildInjectModuleCode.createInjectClass(key, elementList)).build());
        }

        int size = javaFiles.size();
        for (int i = 0; i < size; i++) {
            try {
                javaFiles.get(i).writeTo(processingEnv.getFiler());
            } catch (IOException e) {

            }
        }
        return true;
    }


}
