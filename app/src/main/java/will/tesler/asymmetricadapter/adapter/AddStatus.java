package will.tesler.asymmetricadapter.adapter;

public class AddStatus {

    private String mTag;
    private boolean mReplaced;

    public AddStatus(String tag, boolean replaced) {
        mTag = tag;
        mReplaced = replaced;
    }

    public String getTag() {
        return mTag;
    }

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