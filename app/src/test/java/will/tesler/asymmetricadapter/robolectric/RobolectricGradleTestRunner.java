package will.tesler.asymmetricadapter.robolectric;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

public class RobolectricGradleTestRunner extends RobolectricTestRunner {
    public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String myAppPath = RobolectricGradleTestRunner.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        String manifestPath = myAppPath + "/../../../../../src/main/AndroidManifest.xml";
        String resPath = myAppPath + "/../../../../../src/main/res";
        String assetPath = myAppPath + "/../../../../../src/main/assets";

        return new AndroidManifest(
                Fs.fileFromPath(manifestPath), Fs.fileFromPath(resPath), Fs.fileFromPath(assetPath)) {
            @Override
            public int getTargetSdkVersion() {
                return 18;
            }
        };
    }
}
