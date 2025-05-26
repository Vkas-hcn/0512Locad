package androidx.core.app;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SccsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 1441;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SccsJobIntentService.class, JOB_ID, work);
    }

    public SccsJobIntentService() {
        super();
    }

    @Override
    protected void onHandleWork(Intent intent) {
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

