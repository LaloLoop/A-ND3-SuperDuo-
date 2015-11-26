package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresAdapter;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.widget.TodayMatchWidgetProvider;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * Used to populate the Today's Match widget.
 * <p>
 */
public class TodayMatchIntentService extends IntentService {

    public TodayMatchIntentService() {
        super("TodayMatchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleUpdateSoccerMatch();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleUpdateSoccerMatch() {
        ComponentName widget = new ComponentName(this, TodayMatchWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        int[] ids = manager.getAppWidgetIds(widget);

        // int widgetLayout;
        Cursor cursor = null;

        if(ids != null && ids.length > 0) {
            Uri dataUri = DatabaseContract.scores_table.buildScoreWithDate();
            String date = Utilies.getTodayQueryDate();
            cursor = getContentResolver().query(dataUri, null, null, new String[]{date}, null);
        }

        if(cursor != null && cursor.moveToFirst()) {
            for(int id : ids) {
                // TODO choose right layout here

                RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.widget_today_large);

                // Add data
                String homeName = cursor.getString(ScoresAdapter.COL_HOME);
                rv.setTextViewText(R.id.home_name, homeName);

                String awayName = cursor.getString(ScoresAdapter.COL_AWAY);
                rv.setTextViewText(R.id.away_name, awayName);



                String date = Utilies.getDayName(this,
                        Utilies.dateToMillis(cursor.getString(ScoresAdapter.COL_DATE))) + ", " +
                        cursor.getString(ScoresAdapter.COL_MATCHTIME);
                rv.setTextViewText(R.id.data_textview, date);

                String homeGoals = Utilies.formatScore(cursor.getInt(ScoresAdapter.COL_HOME_GOALS));
                rv.setTextViewText(R.id.home_score, homeGoals);

                String awayGoals = Utilies.formatScore(cursor.getInt(ScoresAdapter.COL_AWAY_GOALS));
                rv.setTextViewText(R.id.away_score, awayGoals);

                if(homeGoals.isEmpty() || homeGoals.isEmpty()) {
                    rv.setViewVisibility(R.id.no_score_info, View.VISIBLE);
                    rv.setViewVisibility(R.id.home_score, View.INVISIBLE);
                    rv.setViewVisibility(R.id.away_score, View.INVISIBLE);
                }

                //double matchId = cursor.getDouble(ScoresAdapter.COL_ID);

                rv.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(homeName));
                rv.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(awayName));

                // Set content descriptions where necessary
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(rv, R.id.home_name, getString(R.string.cd_home, homeName));
                    setRemoteContentDescription(rv, R.id.away_name, getString(R.string.cd_away, awayName));
                    setRemoteContentDescription(rv, R.id.home_score, getString(R.string.cd_home_score, homeGoals));
                    setRemoteContentDescription(rv, R.id.away_score, getString(R.string.cd_away_score, awayGoals));
                    setRemoteContentDescription(rv, R.id.data_textview, getString(R.string.cd_match_date, date));
                }

                // Intent to open main activity
                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                rv.setOnClickPendingIntent(R.id.widget_main_view, pendingIntent);

                manager.updateAppWidget(id, rv);
            }

            cursor.close();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews rv, int id, String description) {
        rv.setContentDescription(id, description);
    }

}
