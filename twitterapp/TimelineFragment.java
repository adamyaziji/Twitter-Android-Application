package twitterapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitterapp.R;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by adamyaziji on 22/03/2015.
 */
public class TimelineFragment extends Fragment {

    ProgressDialog progress;
    SharedPreferences pref;
    ArrayAdapter<Status> tweetAdapter;
    Context context;
    String printAccessToke;
    SimpleAdapter listItemAdapter;
    List<Status> statuses = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.timeline_fragment, container, false);
        context = container.getContext();
        Activity cont = getActivity();
        new GetTimeline(cont, view).execute();
        return view;
    }

    private class GetTimeline extends AsyncTask<String, String, ArrayList> {

        private Context mContext;
        private View rootView;

        public GetTimeline(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Getting timeline ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

        }

        protected ArrayList doInBackground(String... args) {

            ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();

            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                pref = getActivity().getPreferences(0);
                builder.setOAuthConsumerKey(pref.getString("CONSUMER_KEY", ""));
                builder.setOAuthConsumerSecret(pref.getString("CONSUMER_SECRET", ""));

                AccessToken accessToken = new AccessToken
                        (pref.getString("ACCESS_TOKEN", ""),
                        pref.getString("ACCESS_TOKEN_SECRET", ""));

                printAccessToke = accessToken.toString();
                Log.d("Execution", "Access token is" + printAccessToke);

                Twitter twitter = new TwitterFactory(builder.build()).
                        getInstance(accessToken);

                statuses = twitter.getHomeTimeline();

                for (twitter4j.Status status : statuses) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("from", status.getUser().getScreenName());
                    hm.put("to", status.getText());
                    listData.add(hm);
                }

            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return listData;
        }

        protected void onPostExecute(ArrayList listData) {
            super.onPostExecute(listData);
            progress.hide();

            listItemAdapter = (new SimpleAdapter(context, listData,
                    android.R.layout.simple_list_item_2,
                    new String[]{"from", "to"},
                    new int[]{android.R.id.text1, android.R.id.text2}));

            ListView timelineListView = (ListView) rootView.findViewById(R.id.timelineListView);
            timelineListView.setAdapter(listItemAdapter);
        }
    }
}