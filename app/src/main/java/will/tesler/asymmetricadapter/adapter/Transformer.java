package will.tesler.asymmetricadapter.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class Transformer<T> extends RecyclerView.ViewHolder {

    protected Transformer(@LayoutRes int layoutId, ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    public abstract void transform(T model);
}
