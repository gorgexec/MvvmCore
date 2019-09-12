# MvvmCore
This library provides minimal infrastructure for android application building based on Model-View-ViewModel pattern and corresponding Google recommendations concerning [Android application architecture](https://developer.android.com/jetpack/docs/guide), but with some improvements and less boilerplate that usually takes place when you start MVVM app from scratch. So, the aim of the MvvmCore is not to showcase the usage of "another modern" feature or lib, but to reduce some usual infrastructure routines dealing with:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) (instantiation, sharing and lifecycle controlling)
* [Databinding](https://developer.android.com/topic/libraries/data-binding)
* [Navigation](https://developer.android.com/guide/navigation)

## How does it look in real life?
The library has some base classes that should be extended by developer:

* for `Activity`  - `ActivityCore`, `BindableActivityCore`
* for `Fragment`  - `FragmentCore`, `BindableFragmentCore`
* for `ViewModel` - `ViewModelCore`

In order to get an Activity with injected ViewModel mapped to bound layout the code should be like the following

```java
@ViewModelOwner
public class Activity extends BindableActivityCore<ActivityMainBinding, Model> {
}

```

## Features
* ViewModel automated instantiation through Dagger2 out of the box.
The well known ViewModelFactory with Dagger2 Multibindings approach is used, but with this library you should't think of its implementation and maintenance (e.g. viewModels bindings modules etc.).
