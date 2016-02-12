package will.tesler.asymmetricadapter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class UniversalAdapter extends RecyclerView.Adapter<Binder> {

    private Map<String, Section> mSections = new LinkedHashMap<>();

    /**
     * First class is the model, the second class is the transformer for that model.
     */
    private Map<Class<?>, Class<? extends Binder>> mRegistrar = new LinkedHashMap<>();

    private Random mRandom = new Random();

    @Override
    public Binder onCreateViewHolder(ViewGroup parent, int viewType) {
        int i = 0;
        for (Class<? extends Binder> transformer : mRegistrar.values()) {
            if (viewType == i++) {
                try {
                    Constructor<? extends Binder> constructor =
                            transformer.getDeclaredConstructor(ViewGroup.class);
                    return constructor.newInstance(parent);
                } catch (Exception e) {
                    throw new RuntimeException( "Your custom Binder must define a public constructor " +
                            "Binder(ViewGroup parent) must at least call into it's super constructor. Also, " +
                            "ensure that your Binder class is not an inner class. Here is the complete stacktrace:\n", e
                            .getCause());
                }
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(Binder binder, int position) {
        binder.bind(getModel(position));
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Section section : mSections.values()) {
            count += section.size();
        }
        return count;
    }

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
        throw new ModelNotRegisteredException("View model: " + model.getClass() + " has not been registered");
    }

    public <T> void register(Class<T> model, Class<? extends Binder<T>> transformer) {
        mRegistrar.put(model, transformer);
    }

    public AddStatus add(Object object) {
        return add(new Section(object));
    }

    public AddStatus add(String tag, Object object) {
        return add(tag, new Section(object));
    }

    public AddStatus add(Section section) {
        // Produce a tag that is not likely to ever be generated again.
        String tag = Long.toString(mRandom.nextLong());
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

    public void add(int adapterPosition, Object item) {
        int count = 0;
        for (Section section : mSections.values()) {
            count += section.size();
            if (adapterPosition <= count) {
                section.add(count - (count - adapterPosition), item);
                notifyItemInserted(adapterPosition);
                return;
            }
        }
        throw new IndexOutOfBoundsException("Adapter position " + adapterPosition + " was out of bounds on an adapter" +
                " " +
                "of size " + getItemCount());
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
            count += section.size();
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
                notifyItemRangeRemoved(count, section.size());
                return section;
            }
            count += mSections.get(sectionTag).size();
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
     *
     * @param position The position.
     * @return The model at a particular visual position.
     */
    private Object getModel(int position) {
        int sectionEnd = 0;
        for (Section section : mSections.values()) {
            int sectionStart = sectionEnd;
            sectionEnd += section.size();
            if (position < sectionEnd) {
                return section.get(position - sectionStart);
            }
        }
        throw new IndexOutOfBoundsException(Integer.toString(position));
    }
}
