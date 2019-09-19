# MvvmCore
This library provides minimal infrastructure for android application building based on Model-View-ViewModel pattern and corresponding Google recommendations concerning [Android application architecture](https://developer.android.com/jetpack/docs/guide), but with some improvements and less boilerplate that usually takes place when you start MVVM app from scratch. So, the aim of the MvvmCore is not to showcase the usage of "another modern" feature or lib, but to reduce some usual infrastructure routines dealing with:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) 
  * instantiation by dagger2
  * sharing between views
  * lifecycle management
  * model-to-view notifications
 
* [Databinding](https://developer.android.com/topic/libraries/data-binding)
* [Navigation](https://developer.android.com/guide/navigation)

## Usage
First of all, MvvmCore is about interaction between view model and view. Let's see, how it is simplified by the library in case of using Activity/Fragment and ViewModel.
The library has some base classes that should be extended when implementing `Activity`, `Fragment` or `ViewModel`. Classes with `Bindable *` prefix are used for databinding capabilities.

### Activity
Extend `ActivityCore` or `BindableActivityCore` for databinding capabilities. Note, that the first type-parameter of `BindableActivityCore` generic is binding class `ActivityMainBinding` autogenerated by android databinding engine for your `MainActivity`. 

Then call MvvmCore `bind()` method right from the start of the `onCreate` callback. Note, in addition to layout resource id `R.layout.activity_main` and view model class `MainViewModel.class` the method below also accepts the id of `NavHost` implementation `R.id.navHostFragment` that provides navigation capabilities out of the box:

```java
public class MainActivity extends BindableActivityCore<ActivityMainBinding, MainViewModel> {
 @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     bind(R.layout.activity_main, R.id.navHostFragment, MainViewModel.class);
    }
}
```

`bind()` method has some overloads for different needs. E.g. if navigation is not your case, `bind(R.layout.activity_main, MainViewModel.class)` can be used instead.

That's it. 

With this code you'll get:

* ViewModel instance created with necessary dependencies, injected to the activity and accessible through `model()` method of the corresponding activity.
* Layout elements accessible through `binding()` activity method.
* Initialized `NavController` accessible through `nav()`activity method.
* Option to subscribe and respond to any view model-issued event with the help of `subscribeNotification()` method.

Thus, in more complex case the code may be something like that:

```java
public class MainActivity extends BindableActivityCore<ActivityMainBinding, MainViewModel> {
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
     subscribeNotification(MainViewModel.MainViewModel.class, notification -> nav().navigate(R.id.somethingFragment));
     subscribeNotification(Finish.class, notification -> finish());
     .....
    }
}
```

#### Activity result handlers
TBD

### Fragment
The library provides the same abilities for Fragments as for Activities. But there are slightly differences in preparation.

1. Extend the one of two MvvmCore base classes - `FragmentCore` or `BindableFragmentCore`. As with Activity case `BindableFragmentCore` generic class expects two type-params: the `* Binding` class generated by Android databinding library (e.g. `FragmentMyBinding`) and the ViewModel type corresponding to this Fragment (e.g. `MyViewModel`).

2. Call parent constructor with corresponding parameters: layout id, navigation fragment id (optional) and ViewModel class, like in the example below:

```java
@ViewModelOwner
public class MyFragment extends BindableFragmentCore<FragmentMyBinding, MyViewModel> {

    public MyFragment() {
        super(R.layout.fragment_my, R.id.myFragment, MyViewModel.class);
    }

    @Override
    protected void onBindingReady() {
        ...
    }
}
```
3. If ViewModel lifecycle is controlled by Fragment, it's always required to use `@ViewModelOwner` annotation. Otherwise, if ViewModel owner is Activity that should share own ViewModel with the Fragment - skip the annotaion.

4. All MvvmCore methods like `model()`, `nav()` and `subscribeNotification()` become accessable with `onActivityCreated` Fragment lifecycle callback. `binding()` method can be used starting from `onBindingReady` lifecycle callback, that is invoked between`onActivityCreated` and `onStart` lifecycle callbacks in Fragments extended from `BindableFragmentCore`.

### ViewModel
When implementing ViewModel, `ViewModelCore` base class should be extended. MvvmCore uses Dagger2 to provide ViewModels instances, thus ViewModel constructor should be annotated with `@Inject`:

```java
public class MyViewModel extends ViewModelCore {
    @Inject
    public MyViewModel(...) {
      ...
    }
}
```
It may be empty constructor or constructor declaring any number of necessary dependencies, except Context or any View-specific objects references, because of architecture principles violation. 

#### Data binding properties

As extended from `ViewModelCore`, ViewModel becomes to be a subtype of `androidx.lifecycle.ViewModel`. It supports all `androidx.lifecycle.ViewModel` features and also implements `androidx.databinding.Observable` out of the box, so it is ready to provide Data binding properties to its View like the following:

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
MvvmCore provides additional way to broadcast notifications outside `ViewModel` and handle them either by `Activity/Fragment` or by special `NotificationHandler` (in case of global notifications).

For example, as `ViewModel` shouldn't have direct reference to `Context`, the one of the ways to finish `Activity` from `ViewModel` is to send corresponding notification to it. In terms of Android architecture components recomendations this is usually done by introducing `LiveData` object as ViewModel public property, that is subscribed by `Activity` or `Fragment`. But when app grows, such implementation becomes boring and code - bloated. 

So, it can be done easier with the help of MvvmCore ViewModel `notifyView()` method, that accepts parameter of any type as notification content:
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
In the example above, `MyActivity` will fininsh itself as soon as `MyViewModel` object performs `notifyView()` method call, and in accordance with `Activity` lifecycle `MyViewModel` will be similarly disposed.

Note, that `subscribeNotification()` method is also dependent upon `Activity` lifecycle. It's alive from `onResume` till `onPause` states of `Activity`. And during other states it is automatically unsubscribed by the library and resubscribed again when `onResume` occurs. So, you shouldn't care about it by youself. Just do `subscribeNotification()` at the moment of `View` creation, but after `ViewModel` initialization (for `Activity` it is `onCreate()` method, in case of `Fragment` - `onActivityCreated()` or `onBindingReady()`).

#### ViewModel global notifications
Sometimes, it's required to issue similar notifications by different ViewModels and handle them equally over all Views. And that's deal for custom `NotificationHandler`.
