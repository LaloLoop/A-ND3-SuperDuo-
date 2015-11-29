package barqsoft.footballscores.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Sync Service to perform sunshine automatic sync.
 * Created by lalo on 28/11/15.
 */
public class ScoresSyncService extends Service {
    private static final String LOG_TAG = ScoresSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static ScoresSyncAdapter sScoresSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onBind - ScoresSyncService");
        synchronized (sSyncAdapterLock) {
            if(sScoresSyncAdapter == null) {
                sScoresSyncAdapter = new ScoresSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sScoresSyncAdapter.getSyncAdapterBinder();
    }
}
