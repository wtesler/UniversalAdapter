package will.tesler.asymmetricadapter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import will.tesler.asymmetricadapter.adapter.Binder;
import will.tesler.asymmetricadapter.adapter.UniversalAdapter;

public class AsymmetricAdapterActivity extends AppCompatActivity {

    @Bind(R.id.recyclerview_asymmetric) RecyclerView mRecyclerView;

    private UniversalAdapter mUniversalAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asymmetric_adapter);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mUniversalAdapter = new UniversalAdapter();

        mUniversalAdapter.register(ModelA.class, BinderA.class);
        mUniversalAdapter.register(ModelB.class, BinderB.class);
        mUniversalAdapter.register(ModelC.class, BinderC.class);

        mRecyclerView.setAdapter(mUniversalAdapter);

        mUniversalAdapter.add(new ModelA(1));
        mUniversalAdapter.add(new ModelB("Red", Color.RED));
        mUniversalAdapter.add(new ModelA(2));
        mUniversalAdapter.add(new ModelB("Green", Color.GREEN));
        mUniversalAdapter.add(new ModelB("Yellow", Color.YELLOW));
        mUniversalAdapter.add(new ModelC());
        mUniversalAdapter.add(new ModelB("Blue", Color.BLUE));
        mUniversalAdapter.add(new ModelA(3));
        mUniversalAdapter.add(new ModelA(4));
        mUniversalAdapter.add(new ModelC());
    }

    static class BinderA extends Binder<ModelA> {

        @Bind(R.id.edittext_a) EditText edittext_a;

        public BinderA(ViewGroup parent) {
            super(R.layout.layout_a, parent);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(ModelA model) {
            edittext_a.setText(Integer.toString(model.id));
        }
    }

    static class BinderB extends Binder<ModelB> {

        @Bind(R.id.viewgroup_b) ViewGroup viewgroup_b;
        @Bind(R.id.button_b) Button button_b;

        public BinderB(ViewGroup parent) {
            super(R.layout.layout_b, parent);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(ModelB model) {
            button_b.setText(model.action);
            viewgroup_b.setBackgroundColor(model.color);
        }

        @OnClick(R.id.button_b)
        public void buttonClick() {
            Toast.makeText(itemView.getContext(), button_b.getText() + " clicked.", Toast.LENGTH_SHORT).show();
        }
    }

    static class BinderC extends Binder<ModelC> {

        public BinderC(ViewGroup parent) {
            super(R.layout.layout_c, parent);
        }

        @Override
        public void bind(ModelC model) { }
    }

    public class ModelA {
        public final int id;
        ModelA(int id) {
            this.id = id;
        }
    }

    public class ModelB {
        public final String action;
        public final int color;
        ModelB(String action, int color) {
            this.action = action;
            this.color = color;
        }
    }

    public class ModelC {
        // Empty
    }
}
