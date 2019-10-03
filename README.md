# MvvmCore
This library provides minimal infrastructure for android application building based on Model-View-ViewModel pattern and Google recommendations concerning 
[Android application architecture](https://developer.android.com/jetpack/docs/guide), but with some improvements and less boilerplate that usually takes place, when you start MVVM app from scratch. 
MvvmCore will help to reduce some usual infrastructure routines dealing with:

* [ViewModel](#viewmodel) 
  * instantiation by dagger2
  * sharing between views
  * lifecycle management
  * model-to-view notifications
 
* [Databinding](https://developer.android.com/topic/libraries/data-binding)
* [Navigation](https://developer.android.com/guide/navigation)

## Usage
First of all, MvvmCore is about interaction between view model and view. Let's have a look at how the library simplifies Activity/Fragment-ViewModel usage.

Generally, you should extend corresponding MvvmCore class when implementing `Activity`, `Fragment` or `ViewModel`. Classes with `Bindable *` prefix are used for databinding capabilities.

### Activity

To go with `Activity`:

1. _Extend `ActivityCore` or `BindableActivityCore` for databinding capabilities._ 

Note, that the first type-parameter of `BindableActivityCore` generic is `ViewModel` and the second - `ViewDataBinding` class autogenerated by Android Data binding engine for your `Activity`.

2. _Call MvvmCore `setContentView()` or bind()` method (in case of `BindableActivityCore`) right from the start of the `onCreate` callback._ 

These methods and their overloads commonly setup the following:
 * layout resource id 
 * `NavHost` implementation id (optional)
 * `ViewModel` class

```java
public class MainActivity extends BindableActivityCore<MainViewModel, ActivityMainBinding> {
 @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     bind(R.layout.activity_main, R.id.navHostFragment, MainViewModel.class);
    }
}
```

That's it. 

With the code above you'll get:

* `ViewModel` instance created with necessary dependencies, injected to the `Activity` and accessible through `model()` method of the corresponding `Activity`.
* Layout elements accessible through `binding()` `Activity` method.
* Initialized `NavController` accessible through `nav()` method of `Activity`.
* Option to subscribe and respond to any `ViewModel-issued event` with the help of `subscribeNotification()` method.

Thus, in more complex case the code may be something like that:

```java
public class MainActivity extends BindableActivityCore<MainViewModel, ActivityMainBinding> {
 @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     bind(R.layout.activity_main, R.id.navHostFragment, MainViewModel.class);
     
     //initializing recycler view adapter
     binding().recyclerView.setAdapter(new Adapter());
    
     //filling up view model with data from intent
     String someId = getIntent().getStringExtra("someId");
     model().setId(someId);
     
     //subscribing to some events issued by view model
     subscribeNotification(MainViewModel.MainViewModel.class, 
                            notification -> nav().navigate(R.id.somethingFragment));
     subscribeNotification(Finish.class, notification -> finish());
     .....
    }
}
```

#### Activity result handlers
What if its necessary to process `onActivityResult` callback by a `ViewModel`? MvvmCore allows that by simply implementing `IActivityResultHandler` with no code bloated the `Activity` itself :

```java
@ActivityResultHandler(100)
public class OpenDocumentHandler implements IActivityResultHandler {

    @Inject
    public OpenDocumentHandler() {}

    @Override
    public void onActivityResult(ActivityCore activity, int resultCode, @Nullable Intent data) {
        IOpenDocumentTarget target = (IOpenDocumentTarget) activity.findImplementationOf(IOpenDocumentTarget.class);
        if (target != null) {
            if (resultCode == RESULT_OK) {
                target.onDocumentReady(data.getData());
            } else if (resultCode == RESULT_CANCELED) {
                target.onDocumentCanceled();
            }
        }
    }

    public interface IOpenDocumentTarget {
        void onDocumentReady(Uri uri);
        void onDocumentCanceled();
    }
}
```
The example above shows, how to pass the result of document selection processed by `Activity` to a `ViewModel`: 

1. The handler should be annotated with `@ActivityResultHandler` annotation and unique `Integer` request code  as a parameter.

2. A `ViewModel` should implement custom interface (`IOpenDocumentTarget` in example case).

3. The handling code is placed in `onActivityResult()` callback of the `ActivityResultHandler`.

4. In order to get the necessary `ViewModel`, the utility method `findImplementationOf()` can be used. If target `ViewModel` is owned by `Fragment`, it will also be found by the method.

### Fragment
The library provides the same abilities for Fragments as for Activities. But there are slightly differences in preparation:

1. _Extend the one of two MvvmCore base classes - `FragmentCore` or `BindableFragmentCore`._ 

As with Activity, `BindableFragmentCore` generic class expects two type-params: 

* `ViewModel` type corresponding to the Fragment
* `*Binding` class generated by Android databinding library.

2. _Call parent constructor with corresponding parameters:_

* layout resource id 
* `NavigationFragment` id (optional)
* `ViewModel` class

like in the example below:
```java
@ViewModelOwner
public class MyFragment extends BindableFragmentCore<MyViewModel, FragmentMyBinding> {

    public MyFragment() {
        super(R.layout.fragment_my, R.id.myFragment, MyViewModel.class);
    }

    @Override
    protected void onBindingReady() {
        ...
    }
}
```
If your `ViewModel` lifecycle is controlled by a `Fragment`, it's always required to use `@ViewModelOwner` annotation. Otherwise, if `ViewModel` owner is an `Activity` that should share it's own `ViewModel` with the `Fragment` - skip the annotaion.

Methods like `model()`, `nav()` and `subscribeNotification()` become accessable with `onActivityCreated` Fragment lifecycle callback.

`binding()` method can be used starting from `onBindingReady` lifecycle callback, that is invoked between`onActivityCreated` and `onStart` lifecycle callbacks in Fragments extended from `BindableFragmentCore`.

### ViewModel
MvvmCore `ViewModel` implements [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) from Android architecture components. To enable MvvmCore powered ViewModel, `ViewModelCore` base class must be extended. 

As MvvmCore uses `Dagger2` to provide ViewModels instances, annotate `ViewModel` constructor with `@Inject`:

```java
public class MyViewModel extends ViewModelCore {
    @Inject
    public MyViewModel(...) {
      ...
    }
}
```
`ViewModel` constructor may have no arguments or declare any number of necessary dependencies, except `Context` or any `View-specific objects references`, because of architecture principles violation. 

#### Data binding properties

Being extended from `ViewModelCore`, your `ViewModel` becomes to be a subtype of `androidx.lifecycle.ViewModel`. It supports all `androidx.lifecycle.ViewModel` features and also implements `androidx.databinding.Observable` out of the box, so it is ready to provide [Data bindable properties](https://developer.android.com/reference/android/databinding/Bindable) for its `View` like the following:

```java
public class MyViewModel extends ViewModelCore {
   ...
   private String login;

   @Bindable
   public String getLogin() {
       return login;
   }

   public void setLogin(String login) {
       if (!login.equals(this.login)) {
           this.login = login;
           notifyPropertyChanged(BR.login);
       }
   }
   ...
}
```
and corresponding layout (some usual xml code is omitted for brevity):

```xml
<layout>
    <data>
        <variable
            name="model"
            type="com.example.view.MyViewModel" />
    </data>
 ...
  <EditText
      android:id="@+id/login"
      android:text="@={model.login}" />
 ...
 
</layout
```    
#### ViewModel-to-View notifications
MvvmCore provides additional way to broadcast notifications outside of `ViewModel` and handle them either by `Activity/Fragment` or by special `NotificationHandler` (usually, in case of global notifications).

For example, as `ViewModel` shouldn't have direct reference to `Context`, the one of the ways to finish `Activity` from `ViewModel` is to send corresponding notification to it. In terms of Android architecture components recomendations, this is usually done by introducing `LiveData` object as `ViewModel` public property, that is subscribed by `Activity` or `Fragment`. But when app grows, such implementation becomes boring and code - bloated. 

So, it can be done easier with the help of MvvmCore ViewModel's `notifyView()` method, that accepts parameter of any type as notification content:

```java
public class MyViewModel extends ViewModelCore {
   ...
   public void onClose() {
      ...
      notifyView(new Finish());
   }
   ...
   public static class Finish {
    ...
   }
}
```
and the `Activity` subscribed to the model notification:

```java
public class MyActivity extends ActivityCore<MyViewModel> {
 @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_my, MyViewModel.class);
     ...
     subscribeNotification(MyViewModel.Finish.class, notification -> finish());
    }
}
```
In the example above, `MyActivity` will fininsh itself as soon as `MyViewModel` object performs `notifyView()` method call, and in accordance with `Activity` lifecycle, `MyViewModel` will be similarly disposed.

Note, that `subscribeNotification()` method is also dependent upon `Activity` lifecycle. It's alive from `onResume` till `onPause` states of `Activity`. During other states it is automatically unsubscribed by the library and resubscribed again when `onResume` occurs. So, you shouldn't care about it by yourself. Just do `subscribeNotification()` at the moment of `View` creation, but after `ViewModel` initialization (for `Activity` it is `onCreate()` method, in case of `Fragment` - `onActivityCreated()` or `onBindingReady()`).

#### ViewModel global notifications
Sometimes, it's required to issue similar notifications by different ViewModels and handle them equally over all Views. Usually that is the case for common tasks like showing Dialog/Toast, open Document or quit app by `ViewModel` command. And that's a deal for custom `NotificationHandler`. All you have to do, is to implement `INotificationHandler` interface as the following: 

```java
public class QuitAppHandler implements INotificationHandler<QuitApp> {
  private final Context context;
  
   @Inject
   public QuitAppHandler(Context context) {
        this.context = context;
   }
   @Override
   public void handle(ActivityCore activity, QuitApp notification) {
    context.stopService(new Intent(context, AppService.class));
    activity.finishAffinity();
   }
}
```

Note:
* `QuitApp` is a `ViewModel` notification that will may be called by any `ViewModel` with the help of `notifyView()` method.
* As your handler implementation is resolved by `Dagger2`, it should either be decalared in corresponding `Dagger2` module or have a constructor denoted with `@Inject` annotation like in the example above.

That's it. The rest is done automatically by prebuild processing.

## Installation

### Gradle

1. Check repository and include library dependencies:

```gradle
repositories {
    jcenter()
}

dependencies {
  implementation 'com.gorgexec.mvvmcore:mvvmcore:1.0.7'
  annotationProcessor 'com.gorgexec.mvvmcore:compiler:1.0.7'
}
```

2. Include Dagger2 dependency to your project.

3. Check additional gradle options in your app's module `build.gradle` file:

```gradle
 compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    dataBinding {
        enabled = true
    }
```


### Setting up project

In order MvvmCore to be properly used with your app, some settings must be fulfilled.

#### AppConfig

Your app should contain implementation of `AppCoreConfig` interface, that is, first of all, used to pass to MvvmCore references to BR resources generated by the Data binding library for your project. You can freely use this class for additional custom config data, if required. 

Note, that not all of the listed BR resources may be used in project, so in case they are not engaged, zero may be used as return, but at least `getDefaultModelBR()` must return actual value.

The config class is available through `appConfig()` method of MvvmCore `Activity`.

#### Dagger2

1. Your app must have at least one Dagger2 component.

2. Top-level Dagger2 component must be extended from `AppCoreComponent` and contain component `Factory` method accepting at least `Context` and `AppCoreConfig` as parameters.

3. The top-level Dagger2 component (if only one) or subcomponent (if there are multiple components are used) that corresponds to `Activity` scope must be also extended from `ActivityCoreComponent` interface and include `CoreBindingsModule`. Note, that `CoreBindingsModule` is composed during compile time, thus at the first build it would not be found.  

So, totally the component code may be like that:

```java
@Component(modules = {CoreBindingsModule.class})
public interface AppComponent extends AppCoreComponent, ActivityCoreComponent {
    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Context context, @BindsInstance AppCoreConfig appCoreConfig);
    }
}
```

#### Application class

1. Your app should have `Application` class extended from `AppCore` with top-level Dagger2 component as the type-parameter.

2. `Application` class must be annotated with `@MvvmCoreApp`.

2. Your `Application` class must be registered in `AndroidManifest.xml` under the `android:name` field of `<application/>` tag.

3. Overrided `onCreate` calback of the extended `Application` class must invoke `setAppComponent()` method, that accepts initialized Dagger2 root component.

Generally, the code will be like the following:

```java
@MvvmCoreApp
public class App extends AppCore<AppComponent> {
    @Override
    public void onCreate() {
        super.onCreate();
        setAppComponent(DaggerAppComponent.factory().create(this, new AppConfig()));
    }
}
```

4. If your Dagger2 configuration consists of several components (subcomponents), it is required to additionally override `getActivityComponent()` method of extended `Application` class, so that MvvmCore will know, how to get Activity scope related component:

```java
@MvvmCoreApp
public class App extends AppCore<AppComponent> {
    @Override
    public ActivityCoreComponent getActivityComponent() {
        return getAppComponent().activityComponentFactory().create();
    }
}
```

## Addons

### RxJava shortcuts
TBD
