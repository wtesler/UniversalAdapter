package will.tesler.asymmetricadapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import will.tesler.asymmetricadapter.adapter.Section;
import will.tesler.asymmetricadapter.adapter.UniversalAdapter;
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
        mAdapter.register(Model1.class, TestTransformer1.class);
        mAdapter.add(new Model1());

        mAdapter.register(Model2.class, TestTransformer2.class);
        mAdapter.add(new Model2());

        assertThat(mAdapter.getItemViewType(0)).isEqualTo(0);
        assertThat(mAdapter.getItemViewType(1)).isEqualTo(1);
    }

    @Test
    public void getItemViewCount_returnsTheProperCount() {
        mAdapter.register(Model1.class, TestTransformer1.class);

        mAdapter.add(new Model1());
        mAdapter.add(new Model1());
        mAdapter.add(new Model1());

        assertThat(mAdapter.getItemCount()).isEqualTo(3);
    }

    @Test
    public void getItemViewCount_whenUsingSections_returnsTheTotalCount() {
        mAdapter.register(Model1.class, TestTransformer1.class);
        mAdapter.register(Header.class, TestHeaderTransformer.class);

        Section section = new Section(new Header());
        section.add(new Model1());
        section.add(new Model1());
        section.add(new Model1());

        mAdapter.add(section);

        assertThat(mAdapter.getItemCount()).isEqualTo(4);
    }

    @Test
    public void add_withTag_ReturnsAddStatusWithSameTag() {
        mAdapter.register(Model1.class, TestTransformer1.class);
        UniversalAdapter.AddResult addResult = mAdapter.add(new Model1(), "TAG");
        assertThat(addResult.getTag().equals("TAG"));
    }

    @Test
    public void add_withTag_AllowsRemovalOfSectionByTag() {
        mAdapter.register(Model1.class, TestTransformer1.class);

        Model1 model1 = new Model1();
        mAdapter.add(model1, "TAG");

        Model1 retrievedModel1 = (Model1) mAdapter.get("TAG").getModel(0);

        assertThat(retrievedModel1).isEqualTo(model1);
    }

    @Test
    public void createViewHolder_whenRegistered_constructsCorrectTransformer() {
        mAdapter.register(Model1.class, TestTransformer1.class);

        mAdapter.add(new Model1());

        UniversalAdapter.Transformer transformer = mAdapter.createViewHolder(mRecyclerView, 0);

        assertThat(transformer).isInstanceOf(TestTransformer1.class);
    }

    @Test
    public void createViewHolder_whenMultipleRegistrations_constructsCorrectTransformer() {
        mAdapter.register(Model1.class, TestTransformer1.class);
        mAdapter.register(Model2.class, TestTransformer2.class);

        UniversalAdapter.Transformer transformer1 = mAdapter.createViewHolder(mRecyclerView, 0);
        UniversalAdapter.Transformer transformer2 = mAdapter.createViewHolder(mRecyclerView, 1);

        assertThat(transformer1).isInstanceOf(TestTransformer1.class);
        assertThat(transformer2).isInstanceOf(TestTransformer2.class);
    }

    @Test
    public void get_withAdapterPosition_shouldGetTheCorrectModel() {
        mAdapter.register(Model1.class, TestTransformer1.class);
        mAdapter.register(Model2.class, TestTransformer2.class);

        mAdapter.add(new Model1());
        mAdapter.add(new Model1());
        mAdapter.add(new Model2());

        assertThat(mAdapter.get(2)).isInstanceOf(Model2.class);
    }

    @Test
    public void get_withAdapterPosition_shouldGetTheCorrectModelFromASection() {
        mAdapter.register(Model1.class, TestTransformer1.class);
        mAdapter.register(Model2.class, TestTransformer2.class);

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
        mAdapter.register(Model1.class, TestTransformer1.class);
        mAdapter.register(Model2.class, TestTransformer2.class);

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
        mAdapter.register(Model1.class, TestTransformer1.class);

        Model1 model = new Model1();
        mAdapter.add(model);

        TestTransformer1 transformer = (TestTransformer1) mAdapter.createViewHolder(mRecyclerView, 0);
        mAdapter.bindViewHolder(transformer, 0);

        assertThat(transformer.mModel).isEqualTo(model);
    }

    @Test
    public void clearSection_whenNotifyDataSetChanged_shouldClearSectionInAdapter() {
        mAdapter.register(Model1.class, TestTransformer1.class);

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
        UniversalAdapter.Transformer transformer = mAdapter.createViewHolder(mRecyclerView, 0);

        assertThat(transformer).isInstanceOf(TestTransformer1.class);
    }

    public void get_whenSectionIndexIsExceeded_throwsIndexOutOfBoundsException() {
        mAdapter.register(Model1.class, TestTransformer1.class);

        mAdapter.add(new Model1(), "TAG");

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

    public static class TestTransformer1 extends UniversalAdapter.Transformer<Model1> {

        public Object mModel;

        public TestTransformer1(ViewGroup parent) {
            super(R.layout.layout_a, parent);
        }

        @Override
        public void transform(Model1 model) {
            mModel = model;
        }
    }

    public static class TestTransformer2 extends UniversalAdapter.Transformer<Model2> {

        public TestTransformer2(ViewGroup parent) {
            super(R.layout.layout_b, parent);
        }

        @Override
        public void transform(Model2 model) { }
    }

    public static class TestHeaderTransformer extends UniversalAdapter.Transformer<Header> {

        public TestHeaderTransformer(ViewGroup parent) {
            super(R.layout.layout_header, parent);
        }

        @Override
        public void transform(Header header) { }
    }
}
