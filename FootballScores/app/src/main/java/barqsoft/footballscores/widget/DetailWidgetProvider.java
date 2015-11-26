package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.DetailWidgetRemoteViewsService;
import barqsoft.footballscores.service.ScoresFetchService;

/**
 * Detail Football Scores provider
 * Created by lalo on 26/11/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int appWidgetId : appWidgetIds) {
            // Intent to start remote views service
            Intent intent = new Intent(context, DetailWidgetRemoteViewsService.class);
            // Add App Widget Id to intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // Set the RemoteViews object to use a RemoteViews adapter.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_detail);
            // Instantiate RemoteViews object to use a RemoteViews adapter.
            // This adapter connects to a RemoteViewsService through the specified intent.
            // This is how data gets filled.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(rv, intent);
            } else {
                setRemoteAdapterV11(rv, intent);
            }
            // Empty view to display when the collection has no items.
            rv.setEmptyView(R.id.list_view, R.id.empty_view);

            // Intent to main activity
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
            rv.setOnClickPendingIntent(R.id.widget_detail_toolbar, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(RemoteViews rv, Intent intent) {
        rv.setRemoteAdapter(0, R.id.list_view, intent);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(RemoteViews rv, Intent intent) {
        rv.setRemoteAdapter(R.id.list_view, intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(ScoresFetchService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName component = new ComponentName(context, DetailWidgetProvider.class);
            int[] appWidgetIds = manager.getAppWidgetIds(component);
            manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        }
    }
}
