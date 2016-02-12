package will.tesler.asymmetricadapter.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A model representing a list of section items. If the model contains a header, it is held in the first position of
 * the list.
 */
public class Section {

    private List<Object> mItems = new ArrayList<>();
    private List<List<Listener>> mListeners = new ArrayList<>();

    private boolean mHasHeader;

    /**
     * Constructs an empty Section.
     */
    public Section() { }

    /**
     * Constructs a Section with the given header. Useful when clustering items in list.
     *
     * @param header A header item.
     * @param listeners A variable amount of listeners to attach to the item.
     */
    @SafeVarargs
    public <T> Section(@NonNull T header, Listener<T>... listeners) {
        mItems.add(header);
        mListeners.add(Arrays.<Listener>asList(listeners));
        mHasHeader = true;
    }

    /**
     * Adds a header to the section.
     *
     * @param header A header item.
     * @param listeners A variable amount of listeners to attach to the item.
     * @throws IllegalStateException thrown if a header already exists.
     */
    @SafeVarargs
    public final <T> void addHeader(T header, Listener<T>... listeners) {
        if (mHasHeader) {
            throw new IllegalStateException("Section already has a header.");
        } else {
            mItems.add(0, header);
            mListeners.add(0, Arrays.<Listener>asList(listeners));
            mHasHeader = true;
        }
    }

    /**
     * Removes the header from the section.
     *
     * @throws IllegalStateException thrown if no header exists.
     */
    public void removeHeader() {
        if (hasHeader()) {
            mItems.remove(0);
            mListeners.remove(0);
            mHasHeader = false;
        } else {
            throw new IllegalStateException("Section does not contain a header,");
        }
    }

    /**
     * Adds an item to the section. The header will remain the same.
     *
     * @param item The item.
     * @param listeners A variable amount of listeners to attach to the item.
     */
    @SafeVarargs
    public final <T> void add(T item, Listener<T>... listeners) {
        mItems.add(item);
        mListeners.add(Arrays.<Listener>asList(listeners));
    }

    /**
     * Adds an item to the section at the specified position.
     *
     * @param position The position of the item in the section.
     * @param item The item.
     * @param listeners A variable amount of listeners to attach to the item.
     */
    @SafeVarargs
    public final <T> void add(int position, Object item, Listener<T>... listeners) {
        mItems.add(position, item);
        mListeners.add(position, Arrays.<Listener>asList(listeners));
    }

    /**
     * Removes and returns the item at the given position in the section.
     * @param position The position of the item in the section.
     * @return
     */
    public Object remove(int position) {
        mListeners.remove(position);
        return mItems.remove(position);
    }

    /**
     * Sets the items for the section. The header will remain the same.
     *
     * @param items the items.
     */
    public void setItems(List<Object> items) {
        clear();
        mItems.addAll(items);
        for (int i = 0; i < items.size(); i++) {
            mListeners.add(new ArrayList<Listener>());
        }
    }

    /**
     * Removes all the items but leaves the header.
     */
    public void clear() {
        if (hasHeader()) {
            mItems.subList(1, mItems.size()).clear();
            mListeners.subList(1, mItems.size()).clear();
        } else {
            reset();
        }
    }

    /**
     * Removes all the items including the header.
     */
    public void reset() {
        mItems.clear();
        mHasHeader = false;
    }

    /**
     * Get all the items in the section including the header.
     *
     * @return All the items in the section including the header.
     */
    public List<Object> getItems() {
        return mItems;
    }

    /**
     * The item at the given position.
     *
     * @param position The position of the item in the section.
     * @return The item at the given position in the section.
     */
    public Object getItem(int position) {
        return mItems.get(position);
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
     * Checks whether the section contains a header item.
     *
     * @return {@code true} if the section contains a header.
     */
    public boolean hasHeader() {
        return mHasHeader;
    }

    /**
     * The item at the header position or null if there is no header.
     *
     * @return The item at the header position in the section or null.
     */
    @Nullable
    public Object getHeader() {
        return hasHeader() ? mItems.get(0) : null;
    }

    /**
     * The number of items in the section including the header.
     *
     * @return The number of total items in the section.
     */
    public int totalSize() {
        return mItems.size();
    }

    /**
     * The number of items in the section excluding the header.
     *
     * @return The number of content items in the section.
     */
    public int size() {
        return hasHeader() ? mItems.size() - 1 : mItems.size();
    }
}

