package will.tesler.asymmetricadapter.adapter;

public class SectionNotFoundException extends RuntimeException {
    private final String mMessage;

    public SectionNotFoundException(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
