package cloud.com.testinglibrary;

import android.app.Application;
import com.relibit.pulsemetrics.PulseAnalytics;

public class TestClass {

    public static TestClass getInstance() {
        return new TestClass();
    }

    public void start(final Application app, final String appID) {
        PulseAnalytics.initInstance();
        PulseAnalytics.getInstance().start(app, appID);
    }

}
