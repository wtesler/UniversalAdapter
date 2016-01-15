package will.tesler.asymmetricadapter.adapter;

public class ModelNotRegisteredException extends RuntimeException {
    private final String mMessage;

    public ModelNotRegisteredException(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
