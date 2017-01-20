# UniversalAdapter
The first reusable non-abstract RecyclerAdapter for Android.

Usage:

1. Have a model you want to represent in a list (any `Object`).
2. Construct a `UniversalAdapter` and assign it to your `RecyclerView`.
2. Create a `Presenter` which presents the model as a `View`.
3. Register the presenter with the adapter.

Now you can add models to the adapter with `#add(model)` or `#add(section)`. 
The RecyclerView will automatically be notified of changes to the dataset.
  
You can tag models and sections for easy retrieval. You can also get models by position.

If you want to add and remove many models at the same time, you simply create a 
`Section` and call `#add(section, tag)` or `#remove(tag)` respectively.

Every Presenter has access to a relay which they can use to emit events out of the presenter. Observers can call `#getObservable(class, action)` to observe events of the given class filtered by the action type.

If you use proguard, add this rule so that Presenter constructors are kept accessible.

# Universal Adapter
-keepclassmembers class * extends will.tesler.asymmetricadapter.adapter.Presenter{
    public <init>(android.view.ViewGroup);
}

-Will Tesler
