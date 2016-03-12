package johnson.redditing;

import android.graphics.Bitmap;

public class Entry {
    private String domain;
    private String subreddit;
    private String title;
    private String author;
    private String text;
    private String permaLink;
    private String _id;
    private int score;
    private String photo;
    private Bitmap thumbnail;
    private String after;
    private String before;

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
       this._id = _id;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }




}
