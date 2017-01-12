package will.tesler.asymmetricadapter.adapter;

import android.support.annotation.NonNull;

/**
 * An AddResult is an immutable class returned by every add operation in the adapter. It contains a unique tag
 * for the added section, as well as a boolean to determine whether the section was replaced by the add
 * operation.
 */
public class AddResult {

    private final String mTag;
    private final boolean mReplaced;

    /**
     * Construct an immutable AddResult.
     *
     * @param tag      The unique tag for the section.
     * @param replaced {@code true} if the add operation caused the adapter to replace an existing section.
     */
    public AddResult(String tag, boolean replaced) {
        mTag = tag;
        mReplaced = replaced;
    }

    /**
     * @return The unique tag for the section.
     */
    @NonNull
    public String getTag() {
        return mTag;
    }

    /**
     * @return {@code true} if the add operation caused the adapter to replace an existing section.
     */
    public boolean wasReplaced() {
        return mReplaced;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", mTag, mReplaced);
    }

    @Override
    public int hashCode() {
        return mTag.hashCode();
    }
}