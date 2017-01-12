package will.tesler.asymmetricadapter.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * The UniversalAdapter is a composable non-abstract {@code RecyclerAdapter} which can be used as-is without needing to
 * be extended. Define and register {@code Transformers} which tell the adapter how to bind a model to a view. A
 * model can be any {@code Object}, meaning that the UniversalAdapter can handle multiple models and views inside the
 * same adapter.
 * <br/><br/>
 * <b>Usage</b>
 * <br/>
 * Have a model {@code Object} you want to represent in the adapter.
 * Construct a UniversalAdapter and assign it to your RecyclerView.
 * Create a {@link Presenter} which binds the model to a View.
 * Register the transformer with the adapter.
 * The adapter has a number of add operations such as {@code add(model)} or {@code add(section)}. The RecyclerView will
 * automatically be notified of changes to the dataset.
 * <br/><br/>
 * You can tag models and sections for easy retrieval.
 * <br/><br/>
 * If you want to add and remove many models at the same time, you simply create a {@link Section} and call add(section,
 * tag) or remove(tag) respectively.
 */
public class UniversalAdapter extends RecyclerView.Adapter<Presenter> {

    /**
     * Maps tags to corresponding sections. Insertion order is maintained because the sections must be iterable in
     * order.
     */
    private Map<String, Section> mSections = new LinkedHashMap<>();

    /**
     * Maps model classes to corresponding transformer classes. Insertion order is maintained in order to determine
     * view types.
     */
    private Map<Class<?>, Class<? extends Presenter>> mRegistrar = new LinkedHashMap<>();

    /**
     * Use this to relay emissions out of {@link Presenter presenters}.
     */
    private UniversalRelay mUniversalRelay = new UniversalRelay();

    /**
     * Transform a model into a view. No need to check for raw type inference because it is implied
     * by the registrar's structure.
     *
     * @param presenter The presenter.
     * @param position The position of the corresponding model.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(Presenter presenter, int position) {
        Object model = getModel(position);
        presenter.present(model, mUniversalRelay);
    }

    /**
     * Create a {@link Presenter} for a given view type.
     * the view type corresponds to the position of the transformer's class in the registrar. The transformer's class
     * is expected to have a public constructor {@code Presenter(Viewgroup parent)}. Reflection is an important
     * and necessary step in this method as it is the main way that providers are avoided in this adapter. Ensure
     * that proguard does not alter the constructor.
     */
    @Nullable
    @Override
    public Presenter onCreateViewHolder(ViewGroup parent, int viewType) {
        int i = 0;
        for (Class<? extends Presenter> transformer : mRegistrar.values()) {
            if (viewType == i++) {
                try {
                    Constructor<? extends Presenter> constructor =
                            transformer.getDeclaredConstructor(ViewGroup.class);
                    return constructor.newInstance(parent);
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.getCause() + ". Ensure that " + transformer.getSimpleName()
                            + " defines a public constructor " + transformer.getSimpleName() + "(ViewGroup parent)."
                            + " Also, ensure that your Presenter class is not an inner class.", e);
                }
            }
        }
        return null;
    }

    /**
     * Gets the total item count of the adapter, including headers.
     *
     * @return The total item count of the adapter, including headers.
     */
    @Override
    public int getItemCount() {
        int count = 0;
        for (Section section : mSections.values()) {
            count += section.totalSize();
        }
        return count;
    }

    /**
     * The view type of a model is determined by its position in the registrar.
     */
    @Override
    public int getItemViewType(int position) {
        Object model = getModel(position);
        int i = 0;
        for (Class<?> modelClass : mRegistrar.keySet()) {
            if (modelClass == model.getClass()) {
                return i;
            }
            i++;
        }
        throw new IllegalStateException(model.getClass() + " model has not been registered");
    }

    /**
     * Registers a presenter for a model. This tells the adapter that a given model could
     * be represented as a view using this presenter class.
     *
     * @param presenterClass The presenter class to register.
     */
    public void register(Class<? extends Presenter> presenterClass) {
        Class modelClass =
                (Class) ((ParameterizedType) presenterClass.getGenericSuperclass()).getActualTypeArguments()[0];
        mRegistrar.put(modelClass, presenterClass);
    }

    /**
     * Adds a new model to the adapter. The model will be wrapped in a new {@link Section} before being added.
     *
     * @param model The model to add.
     * @return An AddResult containing a tag which can be used to modify the section later.
     */
    @NonNull
    public final AddResult add(Object model) {
        Section section = new Section();
        section.add(model);
        return add(section);
    }

    /**
     * Adds a model to the adapter. The model will be wrapped in a new {@link Section} before being added.
     *
     * @param model The model to add.
     * @param tag The unique tag for the section.
     *
     * @return An AddResult which contains whether the add operation caused a section to be replaced.
     */
    @NonNull
    public final AddResult add(Object model, String tag) {
        Section section = new Section();
        section.add(model);
        return add(section, tag);
    }

    /**
     * Adds a new {@link Section} to the adapter.
     *
     * @param section The section to add.
     * @return An AddResult containing a tag which can be used to modify the section later.
     */
    @NonNull
    public AddResult add(Section section) {
        String tag = UUID.randomUUID().toString();
        return add(section, tag);
    }

    /**
     * Adds a {@link Section} to the adapter, replacing any existing section that has the same tag.
     *
     * @param section The section to add.
     * @param tag The unique tag for the section.
     * @return An AddResult which contains whether the add operation caused a section to be replaced.
     */
    @NonNull
    public AddResult add(Section section, String tag) {
        verify(section);

        AddResult addResult;
        addResult = new AddResult(tag, mSections.get(tag) != null);

        mSections.put(tag, section);
        notifyDataSetChanged();

        return addResult;
    }

    /**
     * Clears all sections in the adapter.
     * @param shouldNotify {@code true} if the adapter should call {@code notifyItemRangeRemoved} after clearing.
     */
    public void clear(boolean shouldNotify) {
        int count = getItemCount();
        mSections.clear();
        if (shouldNotify) {
            notifyItemRangeRemoved(0, count);
        }
    }

    /**
     * Get a section by it's tag.
     *
     * @param tag The tag for the section.
     * @return The section or null.
     */
    @Nullable
    public Section get(String tag) {
        return mSections.get(tag);
    }

    /**
     * Get the model for a particular adapter position. Can be useful when used with
     * {@link android.support.v7.widget.RecyclerView.LayoutManager} methods.
     *
     * @param adapterPosition The adapter position.
     * @return The model associated with a particular adapter position.
     */
    @Nullable
    public Object get(int adapterPosition) {
        int sectionEnd = 0;
        for (Section section : mSections.values()) {
            int sectionStart = sectionEnd;
            sectionEnd += section.totalSize();
            if (adapterPosition < sectionEnd) {
                int sectionPosition = adapterPosition - sectionStart;
                return section.getModel(sectionPosition);
            }
        }
        return null;
    }

    /**
     * Removes a section from the adapter by tag.
     *
     * @param tag The tag for the section.
     * @return The removed section or {@code null} if the section wasn't found.
     */
    @Nullable
    public Section remove(String tag) {
        int count = 0;
        for (String sectionTag : mSections.keySet()) {
            if (sectionTag.equals(tag)) {
                Section section = mSections.remove(sectionTag);
                notifyItemRangeRemoved(count, section.totalSize());
                return section;
            }
            count += mSections.get(sectionTag).totalSize();
        }
        return null;
    }

    /**
     * Removes the model for a particular adapter position. Can be useful when used with
     * {@link android.support.v7.widget.RecyclerView.LayoutManager} methods.
     *
     * @param adapterPosition The adapter position.
     * @return The model associated with a particular adapter position.
     */
    @Nullable
    public Object remove(int adapterPosition) {
        int sectionEnd = 0;
        for (Section section : mSections.values()) {
            int sectionStart = sectionEnd;
            sectionEnd += section.totalSize();
            if (adapterPosition < sectionEnd) {
                int sectionPosition = adapterPosition - sectionStart;
                return section.remove(sectionPosition);
            }
        }
        return null;
    }

    @NonNull
    public <T> Observable<T> getObservable(final Class<T> modelClass, final String action) {
        return mUniversalRelay.filter(new Predicate<Pair<Object, String>>() {
            @Override
            public boolean test(Pair<Object, String> emission) throws Exception {
                return emission.first.getClass().equals(modelClass)
                        && emission.second.equals(action);
            }
        }).map(new Function<Pair<Object, String>, T>() {
            @Override
            public T apply(Pair<Object, String> objectStringPair) throws Exception {
                return (T) objectStringPair.first;
            }
        });
    }

    /**
     * Verify that every model in a section has been registered with the adapter.
     *
     * @param section The section.
     * @throws IllegalStateException If a model in the section has not been registered.
     */
    private void verify(Section section) throws IllegalStateException {
        for (Object object : section.getModels()) {
            if (mRegistrar.get(object.getClass()) == null) {
                throw new IllegalStateException(String.format("%s has not been registered.",
                        object.getClass().getSimpleName()));
            }
        }
    }

    /**
     * Gets the model at an adapter position.
     *
     * @param adapterPosition The position.
     * @return The model.
     */
    private Object getModel(int adapterPosition) {
        int sectionEnd = 0;
        for (Section section : mSections.values()) {
            int sectionStart = sectionEnd;
            sectionEnd += section.totalSize();
            if (adapterPosition < sectionEnd) {
                int itemPosition = adapterPosition - sectionStart;
                return section.getModel(itemPosition);
            }
        }
        throw new IllegalStateException("Could not find model at the given adapter position: " + adapterPosition);
    }
}
