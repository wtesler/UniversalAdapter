package will.tesler.asymmetricadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import will.tesler.asymmetricadapter.adapter.AddResult;
import will.tesler.asymmetricadapter.adapter.Presenter;
import will.tesler.asymmetricadapter.adapter.Section;
import will.tesler.asymmetricadapter.adapter.UniversalAdapter;
import will.tesler.asymmetricadapter.adapter.UniversalRelay;
import will.tesler.asymmetricadapter.robolectric.RobolectricGradleTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class UniversalAdapterTest {

    private UniversalAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private final Context mContext = RuntimeEnvironment.application;

    @Before
    public void setup() {
        mAdapter = new UniversalAdapter();

        mRecyclerView = new RecyclerView(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRecyclerView.setAdapter(mAdapter);

    }

    @Test
    public void register_assignsAnOrdinalViewtypeToTransformerClass() {
        mAdapter.register(TestPresenter1.class);
        mAdapter.add(new Model1());

        mAdapter.register(TestPresenter2.class);
        mAdapter.add(new Model2());

        assertThat(mAdapter.getItemViewType(0)).isEqualTo(0);
        assertThat(mAdapter.getItemViewType(1)).isEqualTo(1);
    }

    @Test
    public void getItemViewCount_returnsTheProperCount() {
        mAdapter.register(TestPresenter1.class);

        mAdapter.add(new Model1());
        mAdapter.add(new Model1());
        mAdapter.add(new Model1());

        assertThat(mAdapter.getItemCount()).isEqualTo(3);
    }

    @Test
    public void getItemViewCount_whenUsingSections_returnsTheTotalCount() {
        mAdapter.register(TestPresenter1.class);
        mAdapter.register(TestHeaderPresenter.class);

        Section section = new Section(new Header());
        section.add(new Model1());
        section.add(new Model1());
        section.add(new Model1());

        mAdapter.add(section);

        assertThat(mAdapter.getItemCount()).isEqualTo(4);
    }

    @Test
    public void add_withTag_ReturnsAddStatusWithSameTag() {
        mAdapter.register(TestPresenter1.class);
        AddResult addResult = mAdapter.add(new Model1(), "TAG");
        assertThat(addResult.getTag().equals("TAG"));
    }

    @Test
    public void add_withTag_AllowsRemovalOfSectionByTag() {
        mAdapter.register(TestPresenter1.class);

        Model1 model1 = new Model1();
        mAdapter.add(model1, "TAG");

        @SuppressWarnings("ConstantConditions")
        Model1 retrievedModel1 = (Model1) mAdapter.get("TAG").getModel(0);

        assertThat(retrievedModel1).isEqualTo(model1);
    }

    @Test
    public void createViewHolder_whenRegistered_constructsCorrectTransformer() {
        mAdapter.register(TestPresenter1.class);

        mAdapter.add(new Model1());

        Presenter presenter = mAdapter.createViewHolder(mRecyclerView, 0);

        assertThat(presenter).isInstanceOf(TestPresenter1.class);
    }

    @Test
    public void createViewHolder_whenMultipleRegistrations_constructsCorrectTransformer() {
        mAdapter.register(TestPresenter1.class);
        mAdapter.register(TestPresenter2.class);

        Presenter presenter1 = mAdapter.createViewHolder(mRecyclerView, 0);
        Presenter presenter2 = mAdapter.createViewHolder(mRecyclerView, 1);

        assertThat(presenter1).isInstanceOf(TestPresenter1.class);
        assertThat(presenter2).isInstanceOf(TestPresenter2.class);
    }

    @Test
    public void get_withAdapterPosition_shouldGetTheCorrectModel() {
        mAdapter.register(TestPresenter1.class);
        mAdapter.register(TestPresenter2.class);

        mAdapter.add(new Model1());
        mAdapter.add(new Model1());
        mAdapter.add(new Model2());

        assertThat(mAdapter.get(2)).isInstanceOf(Model2.class);
    }

    @Test
    public void get_withAdapterPosition_shouldGetTheCorrectModelFromASection() {
        mAdapter.register(TestPresenter1.class);
        mAdapter.register(TestPresenter2.class);

        Section section1 = new Section(new Model1());
        mAdapter.add(section1);

        Section section2 = new Section();
        section2.add(new Model1());
        section2.add(new Model1());
        section2.add(new Model2());
        section2.add(new Model1());
        mAdapter.add(section2);

        assertThat(mAdapter.get(3)).isInstanceOf(Model2.class);
    }

    @Test
    public void removeWithAdapterPosition_removesTheCorrectModelFromASection() {
        mAdapter.register(TestPresenter1.class);
        mAdapter.register(TestPresenter2.class);

        Section section1 = new Section(new Model1());
        mAdapter.add(section1);

        Section section2 = new Section();
        section2.add(new Model1());
        section2.add(new Model1());
        section2.add(new Model2());
        section2.add(new Model1());
        mAdapter.add(section2);

        assertThat(mAdapter.remove(3)).isInstanceOf(Model2.class);
    }

    @Test
    public void bindView_shouldPassModelToTransformer() {
        mAdapter.register(TestPresenter1.class);

        Model1 model = new Model1();
        mAdapter.add(model);

        TestPresenter1 transformer = (TestPresenter1) mAdapter.createViewHolder(mRecyclerView, 0);
        mAdapter.bindViewHolder(transformer, 0);

        assertThat(transformer.mModel).isEqualTo(model);
    }

    @Test
    public void clearSection_whenNotifyDataSetChanged_shouldClearSectionInAdapter() {
        mAdapter.register(TestPresenter1.class);

        Section section = new Section();
        section.add(new Model1());

        mAdapter.add(section);
        mAdapter.notifyDataSetChanged();

        assertThat(mAdapter.getItemCount()).isEqualTo(1);

        section.clearSection();
        mAdapter.notifyDataSetChanged();

        assertThat(mAdapter.getItemCount()).isEqualTo(0);
    }

    @Test(expected = IllegalStateException.class)
    public void add_whenClassNotRegistered_throwsIllegalStateException() {
        mAdapter.add(new Object());
    }

    @Test(expected = RuntimeException.class)
    public void createViewHolder_whenNotRegistered_throwsRuntimeException() {
        Presenter presenter = mAdapter.createViewHolder(mRecyclerView, 0);

        assertThat(presenter).isInstanceOf(TestPresenter1.class);
    }

    public void get_whenSectionIndexIsExceeded_throwsIndexOutOfBoundsException() {
        mAdapter.register(TestPresenter1.class);

        mAdapter.add(new Model1(), "TAG");

        @SuppressWarnings("ConstantConditions")
        Object model = mAdapter.get("TAG").getModel(1);
        assertThat(model).isNull();
    }

    public void get_whenAdapterPositionExceeded_returnsNull() {
        Object model = mAdapter.get(0);
        assertThat(model).isNull();
    }

    public void remove_whenSectionTagDoesntExist_returnsNull() {
        Section section = mAdapter.remove("TAG");
        assertThat(section).isNull();
    }

    class Model1 { }

    class Model2 { }

    class Header { }

    public static class TestPresenter1 extends Presenter<Model1> {

        public Object mModel;

        public TestPresenter1(ViewGroup parent) {
            super(R.layout.layout_a, parent);
        }

        @Override
        public void present(Model1 model, @NonNull UniversalRelay relay) {
            mModel = model;
        }
    }

    public static class TestPresenter2 extends Presenter<Model2> {

        public TestPresenter2(ViewGroup parent) {
            super(R.layout.layout_b, parent);
        }

        @Override
        public void present(Model2 model, @NonNull UniversalRelay relay) { }
    }

    public static class TestHeaderPresenter extends Presenter<Header> {

        public TestHeaderPresenter(ViewGroup parent) {
            super(R.layout.layout_header, parent);
        }

        @Override
        public void present(Header header, @NonNull UniversalRelay relay) { }
    }
}
