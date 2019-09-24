package com.gorgexec.mvvmcore.compiler;

import com.gorgexec.mvvmcore.annotations.ActivityResultHandler;
import com.gorgexec.mvvmcore.annotations.ViewModelOwner;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;


@SupportedAnnotationTypes({
        "com.gorgexec.mvvmcore.annotations.MvvmCoreApp",
        "com.gorgexec.mvvmcore.annotations.ViewModelOwner",
        "com.gorgexec.mvvmcore.annotations.ActivityResultHandler"
})

public class CoreBindingsProcessor extends AbstractProcessor {
    private static final String MODULE_PACKAGE_OPTION_KEY = "android.databinding.modulePackage";
    private static final String DEFAULT_PACKAGE_NAME = "com.gorgexec.mvvmcore";
    private static final String IMPLEMENTATION_CLASSNAME = "CoreBindingsModule";

    private static final String ActivityCoreClassName = "ActivityCore";
    private static final String FragmentCoreClassName = "FragmentCore";


    private static final String INotificationHandlerName = "com.gorgexec.mvvmcore.notification.INotificationHandler";
    private static final String IActivityResultHandlerName = "com.gorgexec.mvvmcore.activity.IActivityResultHandler";

    private static final String ViewModelStubName = "com.gorgexec.mvvmcore.stub.ViewModelStub";
    private static final String ViewNotificationStubName = "com.gorgexec.mvvmcore.stub.ViewNotificationStub";
    private static final String NotificationHandlerStubName = "com.gorgexec.mvvmcore.stub.NotificationHandlerStub";
    private static final String ActivityResultHandlerStubName = "com.gorgexec.mvvmcore.stub.ActivityResultHandlerStub";

    private static ClassName includeModuleClass = ClassName.get("com.gorgexec.mvvmcore.dagger", "BindingsModule");
    private static ClassName annotationModuleClass = ClassName.get("dagger", "Module");
    private static ClassName annotationBinds = ClassName.get("dagger", "Binds");
    private static ClassName annotationIntoMap = ClassName.get("dagger.multibindings", "IntoMap");
    private static ClassName annotationViewModelKey = ClassName.get("com.gorgexec.mvvmcore.dagger", "ViewModelKey");
    private static ClassName annotationClassKey = ClassName.get("dagger.multibindings", "ClassKey");
    private static ClassName annotationIntKey = ClassName.get("dagger.multibindings", "IntKey");
    private static ClassName viewModelClassName = ClassName.get("androidx.lifecycle", "ViewModel");

    private List<TypeMirror> models = new ArrayList<>();
    private Map<String, TypeMirror> notificationHandlers = new HashMap<>();
    private Map<Integer, TypeMirror> activityResultHandlers = new HashMap<>();

    private int methodCount;

    private String packageName;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        packageName = processingEnvironment.getOptions().getOrDefault(MODULE_PACKAGE_OPTION_KEY, DEFAULT_PACKAGE_NAME)+".dagger";
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getRootElements()) {
            TypeElement typeElement = (TypeElement) element;
            if(isViewModelOwner(element)){
                //fill view models list
                TypeMirror typeMirror = typeElement.getSuperclass();
                DeclaredType declaredType = (DeclaredType) typeMirror;
                models.add(declaredType.getTypeArguments().get(0));

            }
            else {
                //fill notification handlers list
                TypeMirror notificationHandlerInterface = extractInterface(typeElement.getInterfaces(), INotificationHandlerName);
                if (notificationHandlerInterface != null) {
                    DeclaredType declaredType = (DeclaredType) notificationHandlerInterface;
                    List<? extends TypeMirror> arguments = declaredType.getTypeArguments();
                    if (arguments != null && arguments.size() == 1) {
                        notificationHandlers.put(arguments.get(0).toString(), typeElement.asType());
                    }
                }
            }
        }

        //fill activity result handlers list
        for (Element element : roundEnvironment.getElementsAnnotatedWith(ActivityResultHandler.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement activityResultHandlerElement = (TypeElement) element;
                TypeMirror activityResultHandlerInterface = extractInterface(activityResultHandlerElement.getInterfaces(), IActivityResultHandlerName);
                if (activityResultHandlerInterface != null) {
                    activityResultHandlers.put(element.getAnnotation(ActivityResultHandler.class).value(), activityResultHandlerElement.asType());
                }
            }
        }

        //add stubs, if required
        if(models.isEmpty()){
           models.add(processingEnv.getElementUtils().getTypeElement(ViewModelStubName).asType());
        }

        if(notificationHandlers.isEmpty()){
            TypeElement element = processingEnv.getElementUtils().getTypeElement(NotificationHandlerStubName);
            notificationHandlers.put(ViewNotificationStubName, element.asType());
        }

        if(activityResultHandlers.isEmpty()){
            TypeElement element = processingEnv.getElementUtils().getTypeElement(ActivityResultHandlerStubName);
            activityResultHandlers.put(-1, element.asType());
        }

        //implementation
        implement();

        return false;
    }

    private void implement() {

        AnnotationSpec includeModuleAnnotation = AnnotationSpec.builder(annotationModuleClass)
                .addMember("includes", "$L", includeModuleClass.toString() + ".class").build();

        TypeSpec.Builder classBuilder = TypeSpec
                .classBuilder(IMPLEMENTATION_CLASSNAME)
                .addAnnotation(includeModuleAnnotation)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        for (TypeMirror type : models) {
            classBuilder.addMethod(getMethodBuilder(
                    AnnotationSpec.builder(annotationViewModelKey)
                            .addMember("value", type.toString() + ".class")
                            .build(),
                    ParameterSpec.builder(ParameterizedTypeName.get(type), "viewModel")
                            .build()).returns(viewModelClassName)
                    .build());
        }

        for (Map.Entry<String, TypeMirror> entry : notificationHandlers.entrySet()) {
            classBuilder.addMethod(getMethodBuilder(
                    AnnotationSpec.builder(annotationClassKey)
                            .addMember("value", entry.getKey() + ".class")
                            .build(),
                    ParameterSpec.builder(ParameterizedTypeName.get(entry.getValue()), "handler")
                            .build()).returns(
                    ParameterizedTypeName.get(ClassName.bestGuess(INotificationHandlerName), WildcardTypeName.subtypeOf(Object.class)))
                    .build());
        }

        for (Map.Entry<Integer, TypeMirror> entry : activityResultHandlers.entrySet()) {
            classBuilder.addMethod(getMethodBuilder(
                    AnnotationSpec.builder(annotationIntKey)
                            .addMember("value", "$L", entry.getKey())
                            .build(),
                    ParameterSpec.builder(ParameterizedTypeName.get(entry.getValue()), "handler")
                            .build())
                    .returns(ClassName.bestGuess(IActivityResultHandlerName))
                    .build());
        }

        try {
            JavaFile.builder(packageName, classBuilder.build())
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private MethodSpec.Builder getMethodBuilder(AnnotationSpec annotationSpec, ParameterSpec parameterSpec) {
        String methodName = "bind" + methodCount;
        methodCount++;
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(annotationBinds)
                .addAnnotation(annotationIntoMap)
                .addAnnotation(annotationSpec)
                .addParameter(parameterSpec);
    }

    private static TypeMirror extractInterface(List<? extends TypeMirror> interfaces, String iface) {
        TypeMirror res = null;
        for (TypeMirror typeMirror : interfaces) {
            if (typeMirror.toString().contains(iface)) {
                res = typeMirror;
                break;
            }
        }

        return res;
    }

    private static boolean isSubTypeOf(Element element, String superTypeSimpleName){
        boolean res = false;
        TypeElement typeElement = (TypeElement) element;
        if(typeElement.getSimpleName().toString().equals(superTypeSimpleName)){
            res = true;
        }
        else {
            TypeMirror typeMirror = typeElement.getSuperclass();
            if(typeMirror != null && typeMirror.getKind() != TypeKind.NONE && typeMirror.getKind() != TypeKind.NULL) {
                DeclaredType declaredType = (DeclaredType) typeMirror;
                res = isSubTypeOf(declaredType.asElement(), superTypeSimpleName);
            }
        }

        return res;
    }

    private static boolean isViewModelOwner(Element element){
        return (isSubTypeOf(element, FragmentCoreClassName) && element.getAnnotation(ViewModelOwner.class) != null)
                || isSubTypeOf(element, ActivityCoreClassName);
    }

}
