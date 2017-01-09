package will.tesler.asymmetricadapter.adapter;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.jakewharton.rxrelay2.PublishRelay;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

public class UniversalRelay {

    private final PublishRelay<Pair<Object, String>> mSubject = PublishRelay.create();

    public void accept(@NonNull Object object, @NonNull String action) {
        mSubject.accept(Pair.create(object, action));
    }

    Observable<Pair<Object, String>> filter(@NonNull Predicate<Pair<Object, String>> predicate) {
        return mSubject.filter(predicate);
    }
}