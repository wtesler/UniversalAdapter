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
import will.tesler.asymmetricadapter.adapter.AsymmetricAdapter;

public class AsymmetricAdapterActivity extends AppCompatActivity {

    @Bind(R.id.recyclerview_asymmetric) RecyclerView mRecyclerView;

    private AsymmetricAdapter mAsymmetricAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asymmetric_adapter);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAsymmetricAdapter = new AsymmetricAdapter();

        mAsymmetricAdapter.register(ModelA.class, TransformerA.class);
        mAsymmetricAdapter.register(ModelB.class, TransformerB.class);
        mAsymmetricAdapter.register(ModelC.class, TransformerC.class);

        mRecyclerView.setAdapter(mAsymmetricAdapter);

        mAsymmetricAdapter.add(new ModelA(1));
        mAsymmetricAdapter.add(new ModelB("Red", Color.RED));
        mAsymmetricAdapter.add(new ModelA(2));
        mAsymmetricAdapter.add(new ModelB("Green", Color.GREEN));
        mAsymmetricAdapter.add(new ModelB("Yellow", Color.YELLOW));
        mAsymmetricAdapter.add(new ModelC());
        mAsymmetricAdapter.add(new ModelB("Blue", Color.BLUE));
        mAsymmetricAdapter.add(new ModelA(3));
        mAsymmetricAdapter.add(new ModelA(4));
        mAsymmetricAdapter.add(new ModelC());
    }

    static class TransformerA extends AsymmetricAdapter.Transformer<ModelA> {

        @Bind(R.id.edittext_a) EditText edittext_a;

        public TransformerA(ViewGroup parent) {
            super(R.layout.layout_a, parent);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void transform(ModelA model) {
            edittext_a.setText(Integer.toString(model.getId()));
        }
    }

    static class TransformerB extends AsymmetricAdapter.Transformer<ModelB> {

        @Bind(R.id.viewgroup_b) ViewGroup viewgroup_b;
        @Bind(R.id.button_b) Button button_b;

        public TransformerB(ViewGroup parent) {
            super(R.layout.layout_b, parent);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void transform(ModelB model) {
            button_b.setText(model.getAction());
            viewgroup_b.setBackgroundColor(model.getColor());
        }

        @OnClick(R.id.button_b)
        public void buttonClick() {
            Toast.makeText(itemView.getContext(), button_b.getText() + " clicked.", Toast.LENGTH_SHORT).show();
        }
    }

    static class TransformerC extends AsymmetricAdapter.Transformer<ModelC> {

        public TransformerC(ViewGroup parent) {
            super(R.layout.layout_c, parent);
        }

        @Override
        public void transform(ModelC model) { }
    }

    public class ModelA {

        private final int mId;

        ModelA(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

    public class ModelB {

        private final String mAction;
        private final int mColor;

        ModelB(String action, int color) {
            mAction = action;
            mColor = color;
        }

        public String getAction() {
            return mAction;
        }

        public int getColor() {
            return mColor;
        }
    }

    public class ModelC {
        // Empty
    }
}
