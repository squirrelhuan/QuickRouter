package cn.demomaster.quickrouter;

import android.app.Application;
import android.os.Environment;

import java.io.File;

import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdlogger_library.config.ConfigBuilder;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ConfigBuilder configBuilder = new ConfigBuilder(this);
        configBuilder.setSaveInternalSoragePath("/test/log");
        configBuilder.setSaveExternalStorageBeforeAndroidQ(false);
        configBuilder.setSaveExternalStoragePath(new File(Environment.getExternalStorageDirectory(),"/test/log"));
        /*Config config = new Config();
        config.logFileRelativePath = "/log/";
        config.fouceUseExternalStorage2 = false;*/
        QDLogger.init(this,configBuilder.build());
    }
}
