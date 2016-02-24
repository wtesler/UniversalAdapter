# UniversalAdapter
The first reusable non-abstract RecyclerAdapter for Android.

Usage:

1. Have a model you want to represent in a list (any `Object`).
2. Construct a `UniversalAdapter` and assign it to your `RecyclerView`.
2. Create a `Transformer` which binds the model to a `View`.
3. Register the binder with the adapter.

Now you can add models to the adapter with `add(model)` or `add(section)`. 
The RecyclerView will automatically be notified of changes to the dataset.
  
You can tag models and sections for easy retrieval. You can also get models by position.

If you want to add and remove many models at the same time, you simply create a 
`Section` and call `add(section, tag)` or `remove(tag)` respectively.
  
Any class wishing to receive events from the list items can implement Listener.

See an example [here](app/src/main/java/will/tesler/asymmetricadapter/UniversalAdapterActivity.java) of it's simple use in an Activity.

-Will Tesler
