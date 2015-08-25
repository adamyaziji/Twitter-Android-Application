package twitterapp;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitterapp.R;

public class ProfileFragment extends Fragment {
    TextView prof_name;
    SharedPreferences pref;
    Bitmap bitmap;
    ImageView prof_img,tweet,signout,timeline;
    ProgressDialog progress;
    Twitter twitter;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        prof_name = (TextView)view.findViewById(R.id.prof_name);
        pref = getActivity().getPreferences(0);
        prof_img = (ImageView)view.findViewById(R.id.prof_image);
        tweet = (ImageView)view.findViewById(R.id.tweet);
        tweet.setOnClickListener(new LoadGroups());
        signout = (ImageView)view.findViewById(R.id.signout);
        signout.setOnClickListener(new SignOut());
        timeline = (ImageView) view.findViewById(R.id.timeline);
        timeline.setOnClickListener(new ViewTimeline());
        new LoadProfile().execute();
        return view;
    }

    private class SignOut implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			SharedPreferences.Editor edit = pref.edit();
            edit.putString("ACCESS_TOKEN", "");
            edit.putString("ACCESS_TOKEN_SECRET", "");
            edit.commit();

            ConfigurationBuilder builder = new ConfigurationBuilder();
            pref = getActivity().getPreferences(0);
            builder.setOAuthConsumerKey(pref.getString("CONSUMER_KEY", ""));
            builder.setOAuthConsumerSecret(pref.getString("CONSUMER_SECRET", ""));

            AccessToken accessToken = new AccessToken
                    (pref.getString("ACCESS_TOKEN", ""),
                            pref.getString("ACCESS_TOKEN_SECRET", ""));

            Twitter twitter = new TwitterFactory(builder.build()).
                    getInstance(accessToken);
            twitter.setOAuthAccessToken(null);
            Fragment login = new LoginFragment();
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();	              
            ft.replace(R.id.content_frame, login);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
		}
    	
    }


    private class LoadGroups implements OnClickListener {

        @Override
        public void onClick(View v) {

            Fragment groups = new GroupFragment();
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, groups);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private class LoadProfile extends AsyncTask<String, String, Bitmap> {
        @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(getActivity());
                progress.setMessage("Loading Profile ...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                
        }
           protected Bitmap doInBackground(String... args) {
             try {
                   bitmap = BitmapFactory.decodeStream((InputStream)new URL
                           (pref.getString("IMAGE_URL", "")).getContent());
            } catch (Exception e) {
                  e.printStackTrace();
            }
          return bitmap;
           }
           protected void onPostExecute(Bitmap image) {
        	   Bitmap image_circle = Bitmap.createBitmap(bitmap.getWidth(),
                       bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        	   BitmapShader shader = new BitmapShader (bitmap,  TileMode.CLAMP, TileMode.CLAMP);
        	   Paint paint = new Paint();
        	   paint.setShader(shader);
        	   Canvas c = new Canvas(image_circle);
        	   c.drawCircle(image.getWidth()/2, image.getHeight()/2, image.getWidth()/2, paint);
               prof_img.setImageBitmap(image_circle);
               prof_name.setText("Hello there " +pref.getString("NAME", "") + "!");
               progress.hide();
              
           }
       }

    private class ViewTimeline implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub

            Fragment timeline = new TimelineFragment();
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, timeline);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}