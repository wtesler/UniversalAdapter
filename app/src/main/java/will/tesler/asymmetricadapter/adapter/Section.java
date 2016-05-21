package will.tesler.asymmetricadapter.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import will.tesler.asymmetricadapter.adapter.UniversalAdapter.Listener;

/**
 * A Section groups models together and associates each model with it's listeners. A section is for use with the
 * {@link UniversalAdapter}. An optional header can be added to a section with {code setHeader} and it will be
 * treated separately from the other items.
 */
public class Section {

    private List<Object> mModels = new ArrayList<>();
    private List<List<Listener>> mListeners = new ArrayList<>();

    private boolean mHasHeader;

    /**
     * Constructs an empty Section.
     */
    public Section() { }

    /**
     * Constructs a Section given a header.
     *
     * @param headerModel A header model.
     * @param listeners A variable amount of listeners to attach to the header.
     */
    @SafeVarargs
    public <T> Section(@NonNull T headerModel, Listener<T>... listeners) {
        mModels.add(headerModel);
        mListeners.add(Arrays.<Listener>asList(listeners));
        mHasHeader = true;
    }

    /**
     * Adds a model to the end of the section.
     *
     * @param model The model.
     * @param listeners A variable amount of listeners to attach to the model.
     */
    @SafeVarargs
    public final <T> void add(T model, Listener<T>... listeners) {
        mModels.add(model);
        mListeners.add(Arrays.<Listener>asList(listeners));
    }

    /**
     * Adds a model to the section at the specified position. Does not take the header into account.
     *
     * @param position The position where the model will be placed.
     * @param model The model.
     * @param listeners A variable amount of listeners to attach to the item.
     */
    @SafeVarargs
    public final <T> void add(int position, Object model, Listener<T>... listeners) {
        int absolutePosition = hasHeader() ? position + 1 : position;
        mModels.add(absolutePosition, model);
        mListeners.add(absolutePosition, Arrays.<Listener>asList(listeners));
    }

    /**
     * Removes and returns the model at the given position in the section. Includes the header.
     *
     * @param position The position of the model in the section.
     */
    public Object remove(int position) {
        if (hasHeader() && position == 0) {
            mHasHeader = false;
        }

        mListeners.remove(position);
        return mModels.remove(position);
    }

    /**
     * Get the model at the given position.
     *
     * @param position The position of the item in the section.
     * @return The model at the given position in the section.
     */
    @Nullable
    public Object getModel(int position) {
        try {
            return mModels.get(position);
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    /**
     * Get all the model in the section excluding the header.
     *
     * @return All the items in the section excluding the header.
     */
    public List<Object> getModels() {
        if (hasHeader()) {
            return mModels.subList(1, mModels.size());
        } else {
            return mModels;
        }
    }

    /**
     * Removes all existing models in the section and adds all the given models. The header will remain the same.
     *
     * @param models the models.
     */
    public void setModels(List<Object> models) {
        clearModels();
        mModels.addAll(models);
        for (int i = 0; i < models.size(); i++) {
            mListeners.add(new ArrayList<Listener>());
        }
    }

    /**
     * Removes all the items but leaves the header.
     */
    public void clearModels() {
        if (hasHeader()) {
            mModels.subList(1, mModels.size()).clear();
            mListeners.subList(1, mModels.size()).clear();
        } else {
            clearSection();
        }
    }

    /**
     * Removes all the items including the header.
     */
    public void clearSection() {
        mModels.clear();
        mHasHeader = false;
    }

    /**
     * The listeners attached to the item at the given position.
     *
     * @param position The position of the item in the section.
     * @return The listeners at the given position in the section.
     */
    public List<Listener> getListeners(int position) {
        return mListeners.get(position);
    }

    /**
     * The item at the header position or null if there is no header.
     *
     * @return The item at the header position in the section or null.
     */
    @Nullable
    public Object getHeader() {
        return hasHeader() ? mModels.get(0) : null;
    }

    /**
     * Adds a header to the section.
     *
     * @param header A header item.
     * @param listeners A variable amount of listeners to attach to the item.
     * @throws IllegalStateException thrown if a header already exists.
     */
    @SafeVarargs
    public final <T> void setHeader(T header, Listener<T>... listeners) {
        clearHeader();
        mModels.add(0, header);
        mListeners.add(0, Arrays.<Listener>asList(listeners));
        mHasHeader = true;
    }

    /**
     * Removes and returns the header if it exists.
     */
    @Nullable
    public Object clearHeader() {
        if (hasHeader()) {
            mHasHeader = false;
            mListeners.remove(0);
            return mModels.remove(0);
        }
        return null;
    }

    /**
     * Checks whether the section contains a header.
     *
     * @return {@code true} if the section contains a header.
     */
    public boolean hasHeader() {
        return mHasHeader;
    }

    /**
     * The number of items in the section including the header.
     *
     * @return The number of total items in the section.
     */
    public int totalSize() {
        return mModels.size();
    }

    /**
     * The number of items in the section excluding the header.
     *
     * @return The number of content items in the section.
     */
    public int size() {
        return hasHeader() ? mModels.size() - 1 : mModels.size();
    }
}
