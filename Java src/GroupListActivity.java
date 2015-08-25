package twitterapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class GroupListActivity extends Activity {

    String groupUsernames,accessToke,accessTokenSecret,
           consumerKey, consumerSecret ;
    String [] usernameArray;
    ProgressDialog progress;
    Context context;
    long cursor,list_idlong;
    int list_id;
    SimpleAdapter listItemAdapter;
    ResponseList<Status> statuses;
    ListView timelineListView;
    ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        timelineListView = (ListView)findViewById(R.id.listViewGroup);
        Log.d("Execution", "onCreate()");
        context = getApplicationContext();

        if(savedInstanceState== null){
            Bundle extras = getIntent().getExtras();
            if(extras==null){
                groupUsernames = null;
                accessToke = null;
                accessTokenSecret = null;
                consumerKey = null;
                consumerSecret = null;
            }else {
                groupUsernames = extras.getString("usernames");
                list_idlong = extras.getLong("list id");
                Log.d("Execution", "list id is" + list_idlong);
                accessToke = extras.getString("access token");
                accessTokenSecret = extras.getString("access token secret");
                consumerKey = extras.getString("consumer key");
                consumerSecret = extras.getString("consumer secret");
            }
        }

        list_id = (int) list_idlong;

        Log.d("Execution", "onCreate() username string is" + groupUsernames);
        usernameArray = groupUsernames.split(",");

        new GetGroupTimeline(getApplicationContext(), this.findViewById(R.id.content_frame)).execute();
    }


    private class GetGroupTimeline extends AsyncTask<String, String, ArrayList> {

        private Context mContext;
        private View rootView;

        public GetGroupTimeline(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Execution", "onPreExecute()");
            progress = new ProgressDialog(GroupListActivity.this);
            progress.setMessage("Getting timeline ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }


        protected ArrayList doInBackground(String... args) {
            Log.d("Execution", "doInBackground()");

            try {

                ConfigurationBuilder builder = new ConfigurationBuilder();

                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                AccessToken accessToken = new
                        AccessToken(accessToke,
                        accessTokenSecret);
                Twitter twitter = new
                        TwitterFactory(builder.build()).getInstance(accessToken);

                Log.d("Execution", "list id is" + list_id);
                Paging page = new Paging(1,100);
                statuses = twitter.getUserListStatuses(list_id, page);

                for (twitter4j.Status status : statuses) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("from", status.getUser().getName());
                    hm.put("to", status.getText());
                    listData.add(hm);
                }


            } catch (TwitterException e) {
                Log.e("TE", "Twitter Exception" + e);
            }

            return listData;
        }

        protected void onPostExecute(ArrayList listData) {
            super.onPostExecute(listData);

            Log.d("Execution", "onPostExecute()");

            listItemAdapter = (new SimpleAdapter(context, listData,
                    R.layout.groupstatuslist_layout,
                    new String[]{"from", "to"},
                    new int[]{R.id.usernameTextView, R.id.userstatusTextView}));

            timelineListView.setBackgroundColor(getResources().
                    getColor(R.color.backgroundblue));
            timelineListView.setAdapter(listItemAdapter);

            progress.hide();
        }
    }
}


