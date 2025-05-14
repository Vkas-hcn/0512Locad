package androidx.core.app;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

// 1. 类定义继承 JobIntentService
public class ScwcJobIntentService extends JobIntentService {

    // 2. 静态常量定义
    private static final int JOB_ID = 1441;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ScwcJobIntentService.class, JOB_ID, work);
    }

    public ScwcJobIntentService() {
        super();
    }

    @Override
    protected void onHandleWork(Intent intent) {
        Log.e("MyJobIntentService", "ScwcJobIntentService: ");
    }

    @Override
    GenericWorkItem dequeueWork() {
        try {
            return super.dequeueWork();
        } catch (Exception e) {
            return null;
        }
    }
}

