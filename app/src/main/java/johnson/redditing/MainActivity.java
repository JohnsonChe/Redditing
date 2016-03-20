package johnson.redditing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static List<Entry> entryList;
    static List<Entry> appendList;
    ListView listView;
    public static final String PACKAGE_NAME = "johnson.redditing";
    private final String url = "https://www.reddit.com/";
    private final String SUBREDDIT = "https://www.reddit.com/r/";
    private final String USER = "user/";
    private final String PRE_AFTER_PARAMETERS = "?after=";
    private final String PRE_BEFORE_PARAMETERS = "?before=";
    private final String JSON = ".json";
    public static String AFTER = "";
    public static String BEFORE = "";
    public static String SUBREDDIT_TITLE = "Frontpage";

    public boolean newSubreddit = false;
    public boolean updatedList = false;
    EntryAdapter adapter;
    EditText searchBar;
    TextView subTitle;
    //Entry entry = new Entry();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        searchBar = (EditText)findViewById(R.id.searchBar);
        searchBar.setVisibility(View.GONE);
        subTitle = (TextView)findViewById(R.id.subTitle);
        subTitle.setText(SUBREDDIT_TITLE);

        listView = (ListView)findViewById(R.id.list);
        requestData();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            listView.setNestedScrollingEnabled(true);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            int mLastFirstVisibleItem = 0;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //listView.smoothScrollToPosition(entryList.size() / 2);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (!updatedList) {
                        Entry entryTemp = new Entry();
                        entryTemp = entryList.get(entryList.size() - 1);
                        AFTER = entryTemp.getAfter();
                        SUBREDDIT_TITLE = entryTemp.getSubreddit();

                        updatedList = true;

                        if(newSubreddit && SUBREDDIT_TITLE != "Frontpage")
                            requestData(SUBREDDIT + SUBREDDIT_TITLE,AFTER);
                        else
                            requestData(AFTER);
                    }
                }
                final int currentFirstVisibleItem = listView.getFirstVisiblePosition();


                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    //Hides ActionBar
                    getSupportActionBar().hide();
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    //Shows ActionBar
                    getSupportActionBar().show();
                }
                mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });
    }


    public String URLGenerator(String subreddit, String after){

        if(subreddit != null & after != null) {
            return "https://www.reddit.com/r/" + subreddit + "/.json/?after=" + after;
        }else if(after == null){
                    return "https://www.reddit.com/r/" + subreddit + "/.json";
                }else if(subreddit == null){
                            return "https://www.reddit.com/.json/?after=" + after;
                        }else
                            return "https://www.reddit.com/.json";


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
//        TextView tv = new TextView(this);
//        tv.setText("Frontpage");

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_get_data) {
            if (isOnline()) {
                searchBar = (EditText)findViewById(R.id.searchBar);
                final String subreddit = searchBar.getText().toString();

                searchBar.setVisibility(View.GONE);
                subTitle.setVisibility(View.VISIBLE);
                searchBar.setText("");


                requestData(SUBREDDIT,subreddit);
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_SHORT).show();

            }
        }
//       if(item.getItemId() == R.id.drawerButton){
//
//        }

        if(item.getItemId() == R.id.searchBar){
            searchBar.setFocusable(true);
            searchBar.requestFocus();
//
        }
        return false;
    }

    public void onSubTextClick(View view){
        subTitle.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        adapter.clear();
        entryList = null;
        newSubreddit=true;
//        SUBREDDIT_TITLE = searchBar.getText().toString();
//        requestData(SUBREDDIT,SUBREDDIT_TITLE);
    }

    @Override
    public void onBackPressed(){
        adapter.addAll(entryList);
        listView.setAdapter(adapter);
        searchBar.setVisibility(View.GONE);
        subTitle.setVisibility(View.VISIBLE);
        newSubreddit = false;
        requestData();
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

        if(newSubreddit && SUBREDDIT_TITLE != "Frontpage"){
            task.execute(uri + JSON + PRE_AFTER_PARAMETERS + AFTER);
        }else{
            task.execute(uri + subredditName + JSON);
        }

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
            subTitle.setText(SUBREDDIT_TITLE);

        }else{
           // adapter.clear();
            adapter.addAll(entryList);
            adapter.notifyDataSetChanged();
            subTitle.setText(SUBREDDIT_TITLE);
        }
        Log.d(PACKAGE_NAME, "Adapter Set!");

    }




    private class MyTask extends AsyncTask<String,String,List<Entry>> {

        @Override
        protected List<Entry> doInBackground(String... params) {
            String content = HttpManager.getData(params[0]);
            if(entryList == null) {
                //entryList.add(entry);
                //entryList.addAll(1,EntryJSONParser.parseFeed(content));
                entryList = EntryJSONParser.parseFeed(content);
            }
            else{
                if(SUBREDDIT_TITLE == "Frontpage") {
                    appendList = EntryJSONParser.parseFeed(content);
                }
                else{
                    SUBREDDIT_TITLE = entryList.get(0).getSubreddit().toString();
                    appendList = EntryJSONParser.parseFeed(content);

                }
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
            //result.add(0,null);
            entryList = result;
            updateDisplay();
            updatedList = false;
        }
    }


}
