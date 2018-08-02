package com.jambuzzers.whatsthatjam;

import android.app.Application;

import com.facebook.soloader.SoLoader;
import com.facebook.sonar.android.AndroidSonarClient;
import com.facebook.sonar.android.utils.SonarUtils;
import com.facebook.sonar.core.SonarClient;

import com.facebook.sonar.plugins.network.NetworkSonarPlugin;

import com.facebook.sonar.plugins.inspector.DescriptorMapping;
import com.facebook.sonar.plugins.inspector.InspectorSonarPlugin;

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);

        if (BuildConfig.DEBUG && SonarUtils.shouldEnableSonar(this)) {
            final SonarClient client = AndroidSonarClient.getInstance(this);
            NetworkSonarPlugin networkSonarPlugin = new NetworkSonarPlugin();
            client.addPlugin(networkSonarPlugin);
            final DescriptorMapping descriptorMapping = DescriptorMapping.withDefaults();
            client.addPlugin(new InspectorSonarPlugin(getApplicationContext(), descriptorMapping));
            client.start();
        }
    }
}
