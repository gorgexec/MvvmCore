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
Extend `ActivityCore` or `BindableActivityCore` for databinding capabilities. Note, that the first type-parameter of `BindableActivityCore` generic is binding class `ActivityMainBinding` autogenerated by android databinding engine for your `MainActivity`. Then call MvvmCore `bind()` method right from the start of the `onCreate` callback. In addition to layout resource id `R.layout.activity_main` and view model class `MainViewModel.class` the method below also accepts the id of `NavHost` implementation `R.id.navHostFragment` that provides navigation capabilities out of the box. `bind()` method has some overloads for different needs. E.g. if navigation is not used in your case, `bind(R.layout.activity_main, MainViewModel.class)` can be used:

```java
public class MainActivity extends BindableActivityCore<ActivityMainBinding, MainViewModel> {
 @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     bind(R.layout.activity_main, R.id.navHostFragment, MainViewModel.class);
    }
}
```
That's it. With this code you'll get:

*  ViewModel instance created with with necessary dependencies and injected to the activity. ViewModel object is accessible through `model` property of the corresponding activity.
* 

### Fragment
There are MvvmCore base classes  `FragmentCore`, `BindableFragmentCore` that should be extended.

### ViewModel
When implementing ViewModel `ViewModelCore` base class should be extended.
That's it. And nothing about ViewModelFactory, ViewModelProvider, Dagger2 MultiBindingModules, DataBindingUtil and other stuff concerning ViewModel creation and databinding. All under the hood! 

For example, in order to create an Activity with ViewModel injected and bound to the layout the code should be like the following:

## Features
* ViewModel automated instantiation through Dagger2 out of the box.
The well known ViewModelFactory with Dagger2 Multibindings approach is used, but with this library you should't think of its implementation and maintenance (e.g. viewModels bindings modules etc.).
