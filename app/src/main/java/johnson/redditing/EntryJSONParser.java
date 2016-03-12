package johnson.redditing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EntryJSONParser {

    public static List<Entry> parseFeed(String content){

        try{
            JSONObject root = new JSONObject(content);
            JSONObject data = root.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");
            List<Entry> entryList = new ArrayList<>();

            MainActivity.AFTER = data.getString("after");
            Log.d("johnson.redditing", "Setting after");

            MainActivity.BEFORE = data.getString("before");
            Log.d("johnson.redditing", "Setting before");

            for(int i = 0; i < children.length(); i++){
                JSONObject obj = children.getJSONObject(i);
                JSONObject childData = obj.getJSONObject("data");
                Entry entry = new Entry();

                entry.setAfter(data.getString("after"));
                entry.setBefore(data.getString("before"));

                entry.setDomain(childData.getString("domain"));
                Log.d("johnson.redditing", "Setting Domain");

                entry.setSubreddit(childData.getString("subreddit"));
                Log.d("johnson.redditing", "Setting Subreddit");

                entry.setText(childData.getString("selftext"));
                Log.d("johnson.redditing", "Setting selftext");

                entry.set_id(childData.getString("id"));
                Log.d("johnson.redditing", "Setting id");

                entry.setAuthor(childData.getString("author"));
                Log.d("johnson.redditing", "Setting author");

                entry.setScore(childData.getInt("score"));
                Log.d("johnson.redditing", "Setting score");

                if(childData.getString("thumbnail") == "self") {
                    entry.setPhoto(null);
                }else{
                    entry.setPhoto(childData.getString("thumbnail"));
                    Log.d("johnson.redditing", "Setting thumbnail");
                }

                entry.setPermaLink(childData.getString("permalink"));
                Log.d("johnson.redditing", "Setting permalink");

                entry.setTitle(childData.getString("title"));
                Log.d("johnson.redditing", "Setting title");

                entryList.add(entry);
                Log.d("johnson.redditing", "Added Entry!");

            }

            return entryList;
        }catch(JSONException e){
            e.printStackTrace();
            Log.d("johnson.redditing","JSONException");
            return null;
        }
    }
}
