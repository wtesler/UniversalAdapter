# UniversalAdapter
The first reusable non-abstract RecyclerAdapter for Android.

Usage:

1. Have a model you want to represent in a list (any `Object`).
2. Construct a `UniversalAdapter` and assign it to your `RecyclerView`.
2. Create a `Binder` which binds the model to a `View`.
3. Register the binder with the adapter.

  After that, you can add models to the adapter with `add(model)` or `add(section)`. 
  It will automatically notify the RecyclerView about which items were added so you 
  never need to notify about the data set.
  
  You can tag models and sections for easy retrieval. You can also get models by position.

  If you want to add and remove many models at the same time, you simply create a 
  Section and call `add(section, tag)` or `remove(tag)` respectively.
  
  You can add listeners to individual models or to entire model classes.
