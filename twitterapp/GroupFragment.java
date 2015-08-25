package twitterapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import twitterapp.R;
import twitter4j.conf.ConfigurationBuilder;

public class GroupFragment extends Fragment {

    Context context;
    ListView groupList;
    DataHandlerAdapter dbHandler;
    SharedPreferences pref;
    String accessToke, accessSec, consumerKey, consumerSec;
    ImageView creategroup, loadgroup, deletegroup;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        final View view = inflater.inflate(R.layout.group_fragment, container, false);
        context = container.getContext();
        dbHandler = new DataHandlerAdapter(context);

        creategroup = (ImageView)view.findViewById(R.id.creategroupIV);
        creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment groups = new CreateGroupFragment();
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, groups);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        loadgroup = (ImageView)view.findViewById(R.id.loadgroupIV);
        loadgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateListView();
            }
        });
        deletegroup = (ImageView)view.findViewById(R.id.deletegroupIV);
        deletegroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHandlerAdapter.dataHandler.deleteAllDB();
            }
        });
        getAccessTokenandSecret();

        groupList = (ListView) view.findViewById(R.id.groupListView);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataHandlerAdapter.dataHandler.getRowUsernames(id);
                DataHandlerAdapter.dataHandler.getRowListID(id);
                String usernames = DataHandlerAdapter.dataHandler.getusernames();
                long listId = DataHandlerAdapter.dataHandler.getlistID();
                Intent openGroupTimeline = new Intent(GroupFragment.this.getActivity(),
                        GroupListActivity.class);
                openGroupTimeline.putExtra("list id", listId);
                openGroupTimeline.putExtra("usernames", usernames);
                openGroupTimeline.putExtra("access token", accessToke);
                openGroupTimeline.putExtra("access token secret", accessSec);
                openGroupTimeline.putExtra("consumer key", consumerKey);
                openGroupTimeline.putExtra("consumer secret", consumerSec);
                startActivity(openGroupTimeline);
            }
        });


        return view;
    }

    private void getAccessTokenandSecret() {
        pref = getActivity().getPreferences(0);
        accessToke = pref.getString("ACCESS_TOKEN", "");
        accessSec = pref.getString("ACCESS_TOKEN_SECRET", "");
        consumerKey = pref.getString("CONSUMER_KEY", "");
        consumerSec = pref.getString("CONSUMER_SECRET", "");
    }

    private void populateListView() {

        Cursor c = DataHandlerAdapter.dataHandler.getallRows();

        String[] fromGroup = new String[]{DataHandlerAdapter.DataHandler.GROUP_NAME,
                DataHandlerAdapter.DataHandler.GROUP_MEMBERS};

        int[] itemViewId = new int[]{R.id.textViewGroupName,R.id.textViewGroupMembers};


        getActivity().startManagingCursor(c);

        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter
                (context, R.layout.list_view, c, fromGroup, itemViewId);


        groupList.setAdapter(myCursorAdapter);
        Log.d("DataBase", "Data should show");

    }
}






