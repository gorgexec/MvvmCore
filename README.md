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
     subscribeNotification(Model.ShowSomething.class, notification -> nav().navigate(R.id.somethingFragment));
     subscribeNotification(Finish.class, notification -> finish());
     .....
    }
}
```

### Fragment
The library provides the same abilities for Fragments as for Activities. But the are slightly differences in preparation.

1. Extend the one of two MvvmCore base classes - `FragmentCore` or `BindableFragmentCore`. As with Activity case `BindableFragmentCore` expects two type-params: the `* Binding` class generated by databinding library (e.g. `FragmentMyBinding`) and the ViewModel type corresponding to this Fragment (e.g. `MyViewModel`).

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

4. All library methods like `model()`, `nav()` and `subscribeNotification()` become accessable with `onActivityCreated` Fragment lifecycle callback. `binding()` method can be used starting from `onBindingReady` lifecycle callback, that is invoked between`onActivityCreated` and `onStart` lifecycle callbacks in Fragments extended from `BindableFragmentCore`.

### ViewModel
When implementing ViewModel `ViewModelCore` base class should be extended.


And nothing about ViewModelFactory, ViewModelProvider, Dagger2 MultiBindingModules, DataBindingUtil and other stuff concerning ViewModel creation and databinding. All under the hood! 

## Features
* ViewModel automated instantiation through Dagger2 out of the box.
The well known ViewModelFactory with Dagger2 Multibindings approach is used, but with this library you should't think of its implementation and maintenance (e.g. viewModels bindings modules etc.).

* In some cases it's necessary to inform activity about some event has occured in view model. E.g. as the `ViewModel` doesn't have direct `Context` reference, one of the ways to finish `Activity` from `ViewModel` is to send corresponding notification to it. In terms of Android architecture components recomendations usually this is done by introducing somekind of `LiveData` view model property, that is subscribed by `Activity`. But sometimes that becomes boring when multiple cases in several views take place. 
