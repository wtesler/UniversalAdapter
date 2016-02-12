package will.tesler.asymmetricadapter.adapter;

public interface Listener<T> {

        void onEvent(T model, String event);
}
