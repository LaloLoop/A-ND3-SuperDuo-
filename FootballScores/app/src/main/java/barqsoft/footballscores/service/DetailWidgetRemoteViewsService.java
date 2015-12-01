package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Remote Service for Deatil Widget
 * Created by lalo on 26/11/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DetailWidgetRemoteViewsFactory(this, intent);
    }

    class DetailWidgetRemoteViewsFactory implements RemoteViewsFactory {

        private final String[] mProjection = {
                DatabaseContract.scores_table.MATCH_ID,
                DatabaseContract.scores_table.HOME_COL,
                DatabaseContract.scores_table.HOME_GOALS_COL,
                DatabaseContract.scores_table.AWAY_COL,
                DatabaseContract.scores_table.AWAY_GOALS_COL,
                DatabaseContract.scores_table.TIME_COL
        };

        private final int MATCH_ID_INDEX = 0;
        private final int HOME_COL_INDEX = 1;
        private final int HOME_GOALS_COL_INDEX = 2;
        private final int AWAY_COL_INDEX = 3;
        private final int AWAY_GOALS_COL_INDEX = 4;
        private final int TIME_COL_INDEX = 5;

        private int mAppWidgetId;
        private Context mContext;
        private Cursor mCursor;

        public DetailWidgetRemoteViewsFactory(Context mContext, Intent intent) {
            this.mContext = mContext;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if(mCursor != null) {
                mCursor.close();
            }

            // Query data
            Uri dataUri = DatabaseContract.scores_table.buildScoreWithDate();

            // Revert back to our process' identity so we can work with our content ptovider.
            final long identityToken = Binder.clearCallingIdentity();
            String date = Utilies.getTodayQueryDate();
            mCursor = getContentResolver().query(dataUri, mProjection, null,
                    new String[] { date }, null);

            // Restore identity
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if(mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(mCursor != null && mCursor.moveToPosition(position)) {

                // Read / Format data from cursor
                String homeName = mCursor.getString(HOME_COL_INDEX);
                int homeGoals = mCursor.getInt(HOME_GOALS_COL_INDEX);
                String awayName = mCursor.getString(AWAY_COL_INDEX);
                int awayGoals = mCursor.getInt(AWAY_GOALS_COL_INDEX);
                String time = mCursor.getString(TIME_COL_INDEX);
                String scores = Utilies.getScores(homeGoals, awayGoals);

                int homeCrest = Utilies.getTeamCrestByTeamName(homeName);
                int awayCrest = Utilies.getTeamCrestByTeamName(awayName);

                RemoteViews rv = new RemoteViews(
                        mContext.getPackageName(), R.layout.widget_detail_list_item);

                rv.setTextViewText(R.id.home_name, homeName);
                rv.setImageViewResource(R.id.home_crest, homeCrest);

                rv.setTextViewText(R.id.away_name, awayName);
                rv.setImageViewResource(R.id.away_crest, awayCrest);

                rv.setTextViewText(R.id.score_textview, scores);

                rv.setTextViewText(R.id.date_textview, time);

                String cdScores;
                if(homeGoals < 0 || awayGoals < 0){
                    cdScores = mContext.getString(R.string.cd_no_scores, homeName, awayName);
                } else {
                    cdScores = mContext
                            .getString(R.string.cd_scores, homeGoals, homeName, awayGoals, awayName);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    rv.setContentDescription(R.id.widget_item, cdScores);
                }

                return rv;
            }

            return null;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if(mCursor != null && mCursor.moveToPosition(position)) {
                return mCursor.getLong(MATCH_ID_INDEX);
            }
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
