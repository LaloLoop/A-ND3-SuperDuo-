package barqsoft.footballscores;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.sync.ScoresSyncAdapter;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    // Added new leagues and fixed numbers.
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEGAUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int BUNDESLIGA3 = 403;
    public static final int EREDIVISIE = 404;
    public static final int CHAMPIONS_LEAGUE = 405;
    private static final String LOG_TAG = Utilies.class.getSimpleName();

    public static String getLeague(Context context, int league_num)
    {
        // Added new leagues to case.
        switch (league_num)
        {
            case BUNDESLIGA1: return context.getString(R.string.bundesliga);
            case BUNDESLIGA2: return context.getString(R.string.bundesliga2);
            case LIGUE1: return context.getString(R.string.ligue1);
            case LIGUE2: return context.getString(R.string.ligue2);
            case PREMIER_LEGAUE : return context.getString(R.string.premierleague);
            case PRIMERA_DIVISION : return context.getString(R.string.primeradivison);
            case SEGUNDA_DIVISION : return context.getString(R.string.segundadivision);
            case SERIE_A : return context.getString(R.string.seriaa);
            case PRIMERA_LIGA : return context.getString(R.string.primeraliga);
            case BUNDESLIGA3 : return context.getString(R.string.bundesliga3);
            case EREDIVISIE : return context.getString(R.string.eredivisie);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.champions_league);
            default: return context.getString(R.string.error_not_known_league);
        }
    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.group_stage_text, match_day);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.semi_final);
            }
            else
            {
                return context.getString(R.string.final_text);
            }
        }
        else
        {
            return context.getString(R.string.matchday_text, match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }

    public static String formatScore(int score) {
        if(score < 0 )
        {
            return "";
        }
        else
        {
            return String.valueOf(score);
        }
    }

    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        }
        else if ( julianDay == currentJulianDay -1)
        {
            return context.getString(R.string.yesterday);
        }
        else
        {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static long dateToMillis(String dateString) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = f.parse(dateString);
        } catch (ParseException e) {
            Log.d(LOG_TAG, "Invalid date: " + dateString);
            return 0;
        }
        return d.getTime();
    }

    public static String getTodayQueryDate() {
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        return mformat.format(new Date());
    }

    public static String getDateForPage(int pagePosition) {
        Date fragmentdate = new Date(System.currentTimeMillis()+((pagePosition-2)*86400000));
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        return mformat.format(fragmentdate);
    }

    /**
     * Saves sync status to shared preferences.
     * @param context       App context
     * @param syncStatus  Sync status to save. Must be one of the defined in {@link ScoresSyncAdapter.SyncStatus}.
     */
    public static void saveSyncStatus(Context context, @ScoresSyncAdapter.SyncStatus int syncStatus) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_sync_status_key), syncStatus);
        editor.commit();
    }

    public static int getSyncStatus(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.pref_sync_status_key),
                ScoresSyncAdapter.SYNC_STATUS_UNKNOWN);
    }

    /**
     * Check if netowrk is accessible
     * @param context   App context
     * @return  true if connected or connecting
     */
    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
