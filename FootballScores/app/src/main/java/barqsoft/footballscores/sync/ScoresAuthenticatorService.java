package barqsoft.footballscores.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Service that allows sync adapter framework to access the authenticator.
 * Created by lalo on 28/11/15.
 */
public class ScoresAuthenticatorService extends Service {
    private ScoresAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new ScoresAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
