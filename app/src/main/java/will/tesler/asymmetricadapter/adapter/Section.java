package will.tesler.asymmetricadapter.adapter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A model representing a list of section items. If the model contains a header, it is held in the first position of
 * the list.
 */
public class Section {

    private List<Object> mItems = new ArrayList<>();

    private boolean mHasHeader;

    public Section() { }

    public Section(@NonNull Object header) {
        mItems.add(header);
        mHasHeader = true;
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
     * Adds a header to the section.
     *
     * @param header A header.
     * @throws IllegalStateException thrown if a header already exists.
     */
    public void addHeader(Object header) {
        if (mHasHeader) {
            throw new IllegalStateException("Section already has a header.");
        } else {
            mItems.add(0, header);
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
            mHasHeader = false;
        } else {
            throw new IllegalStateException("Section does not contain a header,");
        }
    }

    /**
     * Sets the items for the section. The header will remain the same.
     *
     * @param items the items.
     */
    public void setItems(List<Object> items) {
        if (hasHeader()) {
            // Remove all but the header.
            mItems.subList(1, mItems.size()).clear();
        } else {
            // Remove all items.
            mItems.clear();
        }
        mItems.addAll(items);
    }

    public List<Object> getItems() {
        return mItems;
    }

    /**
     * Adds an item to the section. The header will remain the same.
     *
     * @param item The item.
     */
    public void add(Object item) {
        mItems.add(item);
    }

    public void add(int position, Object item) {
        mItems.add(position, item);
    }

    public Object remove(int position) {
        return mItems.remove(position);
    }

    /**
     * The item at the given position.
     *
     * @param position The position of the item in the section.
     * @return The item at the given position in the section.
     */
    public Object get(int position) {
        return mItems.get(position);
    }

    /**
     * The number of items in the section including the header.
     *
     * @return The number of items in the section.
     */
    public int size() {
        return mItems.size();
    }
}

