package barqsoft.footballscores;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.sync.ScoresSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public ScoresAdapter mScoresAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentDate = new String[1];
    private TextView mEmptyTextView;

    private final String FRAGMENT_DATE_KEY = "f_d_key";

    private int mSyncStatus;

    public MainScreenFragment()
    {
    }


    public void setFragmentDate(String date)
    {
        fragmentDate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ListView scoreList = (ListView) rootView.findViewById(R.id.scores_list);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.scores_empty_textview);

        scoreList.setEmptyView(mEmptyTextView);

        mScoresAdapter = new ScoresAdapter(getActivity(),null,0);
        scoreList.setAdapter(mScoresAdapter);

        // Fixes issue with rotation of the app.
        // Restore fragment date if available.
        if(savedInstanceState != null) {
            fragmentDate = savedInstanceState.getStringArray(FRAGMENT_DATE_KEY);
        }

        getLoaderManager().initLoader(SCORES_LOADER,null,this);

        mScoresAdapter.detail_match_id = MainActivity.selected_match_id;

        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewHolder selected = (ViewHolder) view.getTag();
            mScoresAdapter.detail_match_id = selected.match_id;
            MainActivity.selected_match_id = (int) selected.match_id;
            mScoresAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatus = Utilies.getSyncStatus(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(),DatabaseContract.scores_table.buildScoreWithDate(),
                null,null, fragmentDate,null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(FRAGMENT_DATE_KEY, fragmentDate);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {

        mScoresAdapter.swapCursor(cursor);

        updateEmptyView();
    }

    /**
     * Check for possible errors to show to the user.
     */
    private void updateEmptyView() {
        if(mScoresAdapter.getCount() == 0) {
            mEmptyTextView.setText(R.string.no_scores_available);

            @StringRes
            int msgId = 0;
            if(!Utilies.checkNetworkAvailable(getActivity())) {
                msgId = R.string.no_network_available;
            } else if(mSyncStatus == ScoresSyncAdapter.SYNC_STATUS_SERVER_DOWN) {
                msgId = R.string.scores_service_down;
            } else if(mSyncStatus == ScoresSyncAdapter.SYNC_STATUS_SERVER_INVALID) {
                msgId = R.string.check_for_updates;
            } else if(mSyncStatus == ScoresSyncAdapter.SYNC_STATUS_INVALID) {
                msgId = R.string.invalid_sync;
            } else if(mSyncStatus == ScoresSyncAdapter.SYNC_STATUS_UNKNOWN) {
                msgId = R.string.status_unknown;
            }

            if(msgId != 0) {
                mEmptyTextView.append("\n" + getString(msgId));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mScoresAdapter.swapCursor(null);
    }


}
