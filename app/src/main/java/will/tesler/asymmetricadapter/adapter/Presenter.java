package will.tesler.asymmetricadapter.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This class alters a view given a model. Every new model which is added to the adapter must first
 * register a Presenter for that model with the adapter's {@code register} method.
 * <p>
 * <br/><br/><b>Every class that extends
 * this Presenter must define a public constructor {@code Presenter(ViewGroup parent)} which must at
 * least call into the base constructor and supply a layout resource. This is also a good place to bind to views.
 * </b>
 *
 * @param <T> The model which this Presenter will use to alter a view.
 */
public abstract class Presenter<T> extends RecyclerView.ViewHolder {

    protected Presenter(@LayoutRes int layoutRes, ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }

    /**
     * Alter the behavior and appearance of the view given the model.
     *
     * @param model   The model which will be used to alter the view.
     * @param relay A relay you can use to emit events out of the presenter.
     */
    protected abstract void present(T model, @NonNull UniversalRelay relay);

    /**
     * Get the view that the model will be bound to.
     *
     * @return The view that the model will be bound to.
     */
    @NonNull
    final protected View getView() {
        return itemView;
    }

    /**
     * Get the context that the view lives in.
     *
     * @return The context that the view lives in.
     */
    @NonNull
    final protected Context getContext() {
        return itemView.getContext();
    }
}