package will.tesler.asymmetricadapter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import will.tesler.asymmetricadapter.adapter.Section;
import will.tesler.asymmetricadapter.adapter.UniversalAdapter;
import will.tesler.asymmetricadapter.adapter.UniversalAdapter.AddResult;
import will.tesler.asymmetricadapter.adapter.UniversalAdapter.Listener;
import will.tesler.asymmetricadapter.adapter.UniversalAdapter.Transformer;

public class UniversalAdapterActivity extends AppCompatActivity {

    @Bind(R.id.recyclerview_universal) RecyclerView mRecyclerView;
    @Bind(R.id.button_odd) Button mButtonRemoveOdds;

    private UniversalAdapter mUniversalAdapter;
    private List<String> mSectionTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout.
        setContentView(R.layout.activity_universal_adapter);
        ButterKnife.bind(this);

        // Initialize recycler view.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a Universal Adapter.
        mUniversalAdapter = new UniversalAdapter();
        mRecyclerView.setAdapter(mUniversalAdapter);

        // Register
        mUniversalAdapter.register(ColorModel.class, ColorTransformer.class);
        mUniversalAdapter.register(ColorHeader.class, HeaderTransformer.class);

        // Add grey color sections.
        for (int i = 0; i < 16; i++) {
            Section section = new Section(new ColorHeader());
            for (int j = 0; j < 16; j++) {
                section.add(new ColorModel(Color.rgb(i * 16 + j, i * 16 + j, i * 16 + j)));
            }
            AddResult addStatus = mUniversalAdapter.add(section);
            mSectionTags.add(addStatus.getTag());
        }
    }

    public class ColorModel {
        public final int color;
        ColorModel(int color) {
            this.color = color;
        }
    }

    public class ColorHeader {
        ColorHeader() { }
    }

    public static class ColorTransformer extends Transformer<ColorModel> {

        @Bind(R.id.viewgroup_background) ViewGroup background;
        @Bind(R.id.textview_color) TextView color;

        public ColorTransformer(ViewGroup parent) {
            super(R.layout.layout_color, parent);
            ButterKnife.bind(this, getView());
        }

        @Override
        public void transform(ColorModel model, List<Listener<ColorModel>> listeners) {
            background.setBackgroundColor(model.color);
            color.setText(String.format("%d", Color.red(model.color) / 16));
        }
    }

    public static class HeaderTransformer extends Transformer<ColorHeader> {

        public HeaderTransformer(ViewGroup parent) {
            super(R.layout.layout_header, parent);
            ButterKnife.bind(this, getView());
        }

        @Override
        public void transform(ColorHeader model, List<Listener<ColorHeader>> listeners) { }
    }
}
