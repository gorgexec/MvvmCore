# MvvmCore
This library provides minimal infrastructure for android application building based on Model-View-ViewModel pattern and corresponding Google recommendations concerning [Android application architecture](https://developer.android.com/jetpack/docs/guide), but with some improvemrnts and less boilerplate that usually takes place when you start MVVM app from scratch. So, the aim of the MvvmCore is not to showcase the usage of "another modern" feature or lib, but to reduce some usual infrastructure routines.

## Features
* ViewModel automated instantiation through Dagger2 out of the box.
The well known ViewModelFactory with Dagger2 Multibindings approach is used, but with this library you should't think of its implementation and maintenance (e.g. viewModels bindings modules etc.).
