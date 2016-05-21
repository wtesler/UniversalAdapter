package will.tesler.asymmetricadapter.adapter;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UniversalAdapter extends RecyclerView.Adapter<Transformer> {

    private Map<String, Section> mSections = new LinkedHashMap<>();

    /**
     * First class is the model, the second class is the transformer for that model.
     */
    private Map<Class<?>, Class<? extends Transformer>> mRegistrar = new LinkedHashMap<>();

    private Random mTagGenerator = new Random();

    @Override
    public Transformer onCreateViewHolder(ViewGroup parent, int viewType) {
        int i = 0;
        for (Class<? extends Transformer> transformer : mRegistrar.values()) {
            if (viewType == i++) {
                try {
                    Constructor<? extends Transformer> constructor =
                            transformer.getDeclaredConstructor(ViewGroup.class);
                    return constructor.newInstance(parent);
                } catch (Exception e) {
                    throw new RuntimeException( "Your custom Transformer must define a public constructor " +
                            "Transformer(ViewGroup parent) must at least call into it's super constructor. Also, " +
                            "ensure that your Transformer class is not an inner class. Here is the complete stacktrace:\n", e
                            .getCause());
                }
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(Transformer transformer, int position) {
        Pair<Object, List<Listener>> itemWithListeners = getModel(position);
        transformer.transform(itemWithListeners.first, itemWithListeners.second);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Section section : mSections.values()) {
            count += section.totalSize();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        Object model = getModel(position).first;
        int i = 0;
        for (Class<?> modelClass : mRegistrar.keySet()) {
            if (modelClass == model.getClass()) {
                return i;
            }
            i++;
        }
        throw new ModelNotRegisteredException("View model: " + model.getClass() + " has not been registered");
    }

    public <T> void register(Class<T> model, Class<? extends Transformer<T>> transformer) {
        mRegistrar.put(model, transformer);
    }

    @SafeVarargs
    public final <T> AddStatus add(T object, Listener<T>... listeners) {
        Section section = new Section();
        section.add(object, listeners);
        return add(section);
    }

    @SafeVarargs
    public final <T> AddStatus add(String tag, T object, Listener<T>... listeners) {
        Section section = new Section();
        section.add(object, listeners);
        return add(tag, section);
    }

    @SafeVarargs
    public final <T> void add(int adapterPosition, T item, Listener<T>... listeners) {
        int count = 0;
        for (Section section : mSections.values()) {
            count += section.totalSize();
            if (adapterPosition <= count) {
                section.add(count - (count - adapterPosition), item, listeners);
                notifyItemInserted(adapterPosition);
                return;
            }
        }
        throw new IndexOutOfBoundsException("Adapter position " + adapterPosition + " was out of bounds on an adapter" +
                " " +
                "of size " + getItemCount());
    }

    public AddStatus add(Section section) {
        // Produce a tag that is not likely to ever be generated again.
        String tag = Long.toString(mTagGenerator.nextLong());
        return add(tag, section);
    }

    public AddStatus add(String tag, Section section) {
        verify(section);
        AddStatus addStatus;
        addStatus = new AddStatus(tag, mSections.get(tag) != null);
        mSections.put(tag, section);
        notifyDataSetChanged();
        return addStatus;
    }

    public void clear(boolean shouldNotify) {
        int count = getItemCount();
        mSections.clear();
        if (shouldNotify) {
            notifyItemRangeRemoved(0, count);
        }
    }

    public Section retrieve(String tag) {
        return mSections.get(tag);
    }

    public Object remove(int adapterPosition) {
        int count = 0;
        for (Section section : mSections.values()) {
            count += section.totalSize();
            if (adapterPosition < count) {
                Object item = section.remove(count - (count - adapterPosition));
                notifyItemRemoved(adapterPosition);
                return item;
            }
        }
        throw new IndexOutOfBoundsException("Adapter position " + adapterPosition + " was out of bounds on an adapter" +
                " " +
                "of size " + getItemCount());
    }

    public Object remove(String tag) {
        int count = 0;
        for (String sectionTag : mSections.keySet()) {
            if (sectionTag.equals(tag)) {
                Section section = mSections.remove(sectionTag);
                notifyItemRangeRemoved(count, section.totalSize());
                return section;
            }
            count += mSections.get(sectionTag).totalSize();
        }
        throw new SectionNotFoundException("Section with tag " + tag + " was not found.");
    }

    private void verify(Section section) throws ModelNotRegisteredException {
        for (Object object : section.getItems()) {
            if (mRegistrar.get(object.getClass()) == null) {
                throw new ModelNotRegisteredException(String.format("%s has not " +
                        "been registered.", object.getClass().getName()));
            }
        }
    }

    /**
     * Gets the model at a particular visual position.
     * O(n)
     *
     * @param position The position.
     * @return The model at a particular visual position.
     */
    private Pair<Object, List<Listener>> getModel(int position) {
        int sectionEnd = 0;
        for (Section section : mSections.values()) {
            int sectionStart = sectionEnd;
            sectionEnd += section.totalSize();
            if (position < sectionEnd) {
                int itemPosition = position - sectionStart;
                return new Pair<>(section.getItem(itemPosition), section.getListeners(itemPosition));
            }
        }
        throw new IndexOutOfBoundsException(Integer.toString(position));
    }
}
