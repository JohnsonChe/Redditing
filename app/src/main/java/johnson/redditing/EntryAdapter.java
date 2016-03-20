package johnson.redditing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


public class EntryAdapter extends ArrayAdapter<Entry> {

    private Context context;
    private List<Entry> entryList;

    public EntryAdapter(Context context, int resource, List<Entry> objects) {
        super(context, resource, objects);
        this.context = context;
        this.entryList = objects;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.entry_detail, parent, false);

        Entry entry = entryList.get(position);
        if (position == 0) return view;
//
//        if(position == entryList.size() -1) {
//            loadMoreData();
//        }
        TextView titleText = (TextView)view.findViewById(R.id.titleText);
        titleText.setText(entry.getTitle());

        TextView authorText = (TextView)view.findViewById(R.id.authorText);
        authorText.setText(entry.getAuthor());

        TextView scoreText = (TextView)view.findViewById(R.id.scoreNumText);
        scoreText.setText(String.valueOf(entry.getScore()));

        TextView subredditText = (TextView)view.findViewById(R.id.subredditText);
        subredditText.setText(entry.getSubreddit());

        if(entry.getThumbnail() != null){
            ImageView image = (ImageView) view.findViewById(R.id.entryImage);
            image.setImageBitmap(entry.getThumbnail());
        }else{
            EntryAndView container = new EntryAndView();
            container.entry = entry;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
    }

    private void setAfter(String after){
        MainActivity.AFTER = after;
    }

    private boolean reachedEndOfList(int position){
        return (position == entryList.size() - 1);
    }

    private void loadMoreData(){
        MainActivity main = new MainActivity();
        main.requestData(MainActivity.AFTER);
    }

    class EntryAndView{
        public Entry entry;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<EntryAndView,Void, EntryAndView> {

        @Override
        protected EntryAndView doInBackground(EntryAndView... params) {

            EntryAndView container = params[0];
            Entry entry = container.entry;

            try{
                    String imageUrl = entry.getPhoto();
                    InputStream in = (InputStream)new URL(imageUrl).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    entry.setThumbnail(bitmap);
                    in.close();
                    container.bitmap = bitmap;
                    return container;
                }catch(Exception e){
                    e.printStackTrace();
                    Log.d(MainActivity.PACKAGE_NAME, "Bitmap conversion Failed!");
                }

            return container;
        }
        @Override
        protected void onPostExecute(EntryAndView result){
            ImageView image = (ImageView)result.view.findViewById(R.id.entryImage);
            image.setImageBitmap(result.bitmap);
            result.entry.setThumbnail(result.bitmap);

        }
    }
}
