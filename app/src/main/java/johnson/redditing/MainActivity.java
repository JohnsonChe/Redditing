package johnson.redditing;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MainActivity extends Activity {

    static List<Entry> entryList;
    static List<Entry> appendList;
    ListView listView;
    public static final String PACKAGE_NAME = "johnson.redditing";
    private final String url = "https://www.reddit.com/";
    private final String SUBREDDIT = "r/";
    private final String USER = "user/";
    private final String PRE_AFTER_PARAMETERS = "?after=";
    private final String PRE_BEFORE_PARAMETERS = "?before=";
    private final String JSON = ".json";
    public static String AFTER = "";
    public static String BEFORE = "";
    public boolean updatedList = false;
    public boolean firstPass = false;
    EntryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.list);
        requestData();

        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScrollStateChanged(AbsListView view, int scrollState){
                //listView.smoothScrollToPosition(entryList.size() / 2);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0){
                    if(!updatedList) {
                        Entry entryTemp = new Entry();
                        entryTemp = entryList.get(entryList.size() - 1);
                        AFTER = entryTemp.getAfter();
                        updatedList = true;
                        requestData(AFTER);
//                        listView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                listView.setSelectionFromTop(listView.getFirstVisiblePosition(),0);
//                            }
//                        });
                    }
                }
            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_get_data) {
            if (isOnline()) {
                requestData();
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_SHORT).show();

            }
        }
        return false;
    }

    private void requestData(){
       //Frontpage
        MyTask task = new MyTask();
        task.execute(url + JSON);
    }

    public void requestData(String after){
        //Request next page

        MyTask task = new MyTask();
        task.execute(url + JSON + PRE_AFTER_PARAMETERS + after);
    }

    private void requestData(String uri, String subredditName){
        //Specific Subreddit
        MyTask task = new MyTask();
        task.execute(uri + SUBREDDIT + subredditName + JSON);
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }

    public void updateDisplay (){

        if( adapter == null) {
            adapter = new EntryAdapter(this,R.layout.entry_detail,entryList);
            listView.setAdapter(adapter);
        }else{
           // adapter.clear();
            adapter.addAll(entryList);
            adapter.notifyDataSetChanged();
        }
        Log.d(PACKAGE_NAME, "Adapter Set!");

    }




    private class MyTask extends AsyncTask<String,String,List<Entry>> {

        @Override
        protected List<Entry> doInBackground(String... params) {
            String content = HttpManager.getData(params[0]);
            if(entryList == null) {
                entryList = EntryJSONParser.parseFeed(content);
            }
            else{
               appendList = EntryJSONParser.parseFeed(content);
                return appendList;
            }
            return entryList;
        }

        @Override
        protected void onPostExecute(List<Entry> result){
            if(result == null){
                Toast.makeText(MainActivity.this,"Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            if(!firstPass) {
                entryList = result;
                firstPass = true;
            }
            else{
                entryList = result;
            }
            updateDisplay();
            updatedList = false;
        }
    }


}
