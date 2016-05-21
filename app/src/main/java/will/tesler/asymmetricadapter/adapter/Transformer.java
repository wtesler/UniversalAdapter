package will.tesler.asymmetricadapter.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class Transformer<T> extends RecyclerView.ViewHolder {

    protected Transformer(@LayoutRes int layoutId, ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    public abstract void transform(T model, List<Listener<T>> listeners);

    /**
     * Get the view that the model will be bound to.
     *
     * @return The view that the model will be bound to.
     */
    public View getView() {
        return itemView;
    }

    /**
     * Get the context that the view lives in.
     *
     * @return The context that the view lives in.
     */
    final protected Context getContext() {
        return itemView.getContext();
    }
}
