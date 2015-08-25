package twitterapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by adamyaziji on 24/04/2015.
 */

public class CreateGroupFragment extends Fragment {

    DataHandlerAdapter dbHandler;
    EditText listAddMembersET,groupNameET;
    String memberToAdd,groupName,groupArrayString,memberToAddWithSpace;
    Long listId;
    String usernameArray[] = {""};
    Context context;
    ArrayList<String> usernameList = new ArrayList<String>();
    Twitter twitter;
    CreateGroupFragment groupFragment;
    UserList list;
    SharedPreferences pref;
    StringBuffer result;
    ImageView addMemberImage,addGroupImage;
    ListView usernameListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.creategroup_fragment, container, false);
        listAddMembersET = (EditText) view.findViewById(R.id.addMemberET);
        usernameListView = (ListView)view.findViewById(R.id.usernameListView);
        groupNameET = (EditText) view.findViewById(R.id.groupName);
        context = container.getContext();
        groupFragment = new CreateGroupFragment();
        dbHandler = new DataHandlerAdapter(context);
        openDB();
        addMemberImage = (ImageView)view.findViewById(R.id.addmemberIV);
        addMemberImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembers();
                Toast.makeText(getActivity().getApplicationContext(), "Added " +
                        memberToAdd + " to group", Toast.LENGTH_SHORT).show();
            }
        });
        addGroupImage = (ImageView)view.findViewById(R.id.addgroupIV);
        addGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetGroupTimeline(context,view).execute();
                goToListView();
            }
        });
        return view;
    }

    public void openDB() {
        DataHandlerAdapter.dataHandler.open();
    }

    public void goToListView() {
        Fragment homeScreen = new GroupFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, homeScreen);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public String[] addMembers() {
        memberToAdd = listAddMembersET.getText().toString();
        if (listAddMembersET.getText().length() != 0) {
            memberToAddWithSpace = memberToAdd+" ";
            usernameList.add(memberToAddWithSpace);
            Toast.makeText(getActivity().getApplicationContext(), "Added " +
                    memberToAdd + " to group", Toast.LENGTH_SHORT).show();
            usernameArray = usernameList.toArray(new String[usernameList.size()]);
        }

        result = new StringBuffer();
        for (int i = 0; i < usernameArray.length; i++) {
            result.append( usernameArray[i] );
        }

        groupArrayString = result.toString();
        listAddMembersET.setText("");
        populateListView();
        return usernameArray;
    }

    private void populateListView() {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.usernamelist,
                R.id.usernameListName,usernameList);

        usernameListView.setAdapter(adapter);

    }


    private class GetGroupTimeline extends AsyncTask<String, String, Void> {

        private Context mContext;
        private View rootView;

        public GetGroupTimeline(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
        }

        @Override
        protected void onPreExecute() {
        }


        protected Void doInBackground(String... args) {
            groupName = groupNameET.getText().toString();

            ConfigurationBuilder builder = new ConfigurationBuilder();
            pref = getActivity().getPreferences(0);
            builder.setOAuthConsumerKey(pref.getString("CONSUMER_KEY", ""));
            builder.setOAuthConsumerSecret(pref.getString("CONSUMER_SECRET", ""));

            AccessToken accessToken = new AccessToken(pref.getString("ACCESS_TOKEN", ""),
                    pref.getString("ACCESS_TOKEN_SECRET", ""));
            twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

            try {
                list = twitter.createUserList(groupName, false, null);
                twitter.createUserListMembers(list.getId(), usernameArray);
                listId = list.getId();
                Log.d("DataBase", "Twitter list created. Name is:" + list.getName());
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            long id = dbHandler.insertData(groupName, groupArrayString, listId);
            return null;
        }
    }
}
