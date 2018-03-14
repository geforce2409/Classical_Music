package dataobject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranminhquan on 10/18/2017.
 * Chua data ve bai post trong app
 * Note: Hien tai co 4 post type: newsfeed, food, hotel, place
 */


public class Post {
    private String postid;
    private String content;
    private String avatarLink;
    private ArrayList<String> imageLinks;
    private String userid;
    private String username;
    private long timestamp;
    private List<String> links;
    private List<String> tags;
    private List<String> likes; //chua cac likeid
    private List<String> shares; //chua cac shareid
    private List<String> comments; //chua cac commentid
    private POST_TYPE type; //type cua bai post
    private int rating;
    private int likeCount;
    private int cmtCount;

    public Post(){
        //default constructor for firebase
    }

    public Post(String ctn, String avaLink, ArrayList<String> imglinks, String uid, String uname, long ts, List<String> lk,
                List<String> tg, List<String> lik, List<String> shrs, List<String> cmts, POST_TYPE tp, int rt){
        links = new ArrayList<>();
        //postid = pid;
        content = ctn;
        avatarLink = avaLink;
        imageLinks = imglinks;
        userid = uid;
        username = uname;
        timestamp = ts;
        links = lk;
        tags = tg;
        likes = lik;
        shares = shrs;
        comments = cmts;
        type = tp;
        rating = rt;
        likeCount = 0;
        cmtCount = 0;
    }

    public void init(){
        imageLinks = new ArrayList<>();
    }
    //Get-set
    public String getPostid() { return postid; }

    public void setPostid(String value) {
        postid = value;
    }

    public String getContent() { return content; }

    public void setContent(String value) {
        content = value;
    }

    public String getAvatarLink() { return avatarLink; }

    public void setAvatarLink(String value) {
        avatarLink = value;
    }

    public ArrayList<String> getImageLinks() { return imageLinks; }

    public void setImageLinks(ArrayList<String> value) {
        imageLinks = value;
    }

    public String getUserid() { return userid; }

    public void setUserid(String value) {
        userid = value;
    }

    public String getUsername() { return username; }

    public void setUsername(String value) {
        username = value;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long value) { timestamp = value; }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> value) { links = value; }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> value) { tags = value; }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> value) { likes = value; }

    public List<String> getShares() {
        return shares;
    }

    public void setShares(List<String> value) { shares = value; }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> value) { comments = value; }

    public POST_TYPE getType() {
        return type;
    }

    public void setType(POST_TYPE value) { type = value; }

    public int getRating() {
        return rating;
    }

    public void setRating(int value) { rating = value; }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int value) { likeCount = value; }

    public int getCmtCount() {
        return cmtCount;
    }

    public void setCmtCount(int value) { cmtCount = value; }


    //add
    public void addImageLink(String element) { imageLinks.add(element); }
    public void addLink(String element) { links.add(element); }
    public void addTag(String element) { tags.add(element); }
    public void addLike(String element) { likes.add(element); }
    public void addShare(String element) { shares.add(element); }
    public void addComment(String element) { comments.add(element); }
    public void increaseLike() { likeCount++; }
    public void decreaseLike() { if (likeCount < 0) return; likeCount--;}
    public void increaseCmt() { cmtCount++; }
    public void decreaseCmt() { if (cmtCount < 0) return; cmtCount--; }

}
