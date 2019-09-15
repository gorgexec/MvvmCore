# MvvmCore
This library provides minimal infrastructure for android application building based on Model-View-ViewModel pattern and corresponding Google recommendations concerning [Android application architecture](https://developer.android.com/jetpack/docs/guide), but with some improvements and less boilerplate that usually takes place when you start MVVM app from scratch. So, the aim of the MvvmCore is not to showcase the usage of "another modern" feature or lib, but to reduce some usual infrastructure routines dealing with:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) 
  * instantiation by dagger2
  * sharing between views
  * lifecycle management
  * model-to-view notifications
 
* [Databinding](https://developer.android.com/topic/libraries/data-binding)
* [Navigation](https://developer.android.com/guide/navigation)

## How does it look in real life?
The library has some base classes that should be extended:

* for `Activity`  - `ActivityCore`, `BindableActivityCore`
* for `Fragment`  - `FragmentCore`, `BindableFragmentCore`
* for `ViewModel` - `ViewModelCore`

'Bindable*' - versions for databinding capabilities. 

For example, in order to create an Activity with ViewModel injected and bound to the layout the code should be like the following:

```java
@ViewModelOwner
public class MainActivity extends BindableActivityCore<ActivityMainBinding, Model> {
 @Override
    protected void onCreate(Bundle savedInstanceState) {
     bind(R.layout.activity_main, Model.class);
    }
}
```
That's it. And nothing about ViewModelFactory, ViewModelProvider, Dagger2 MultiBindingModules, DataBindingUtil and other stuff concerning ViewModel creation and databinding. All under the hood! 

## Features
* ViewModel automated instantiation through Dagger2 out of the box.
The well known ViewModelFactory with Dagger2 Multibindings approach is used, but with this library you should't think of its implementation and maintenance (e.g. viewModels bindings modules etc.).
