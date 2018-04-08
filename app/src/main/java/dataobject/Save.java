package dataobject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yul Lucia on 04/08/2018.
 */

public class Save extends Interaction {

    private String saveid;
    private String postid;
    private String content;
    private String avatarLink;
    private String audioLink;
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
    private int likeCount;
    private int cmtCount;

    public Save(){
        super();
        setAvatarLink(avatarLink);
        setImageLinks(imageLinks);
        setContent(content);
        setAudioLink(audioLink);
    }

    public Save(String sid, String pid, String uid, String uname, long ts, String ctn, String avaLink, String audLink, ArrayList<String> imglinks){
        super(pid, uid, uname, ts);

        saveid = sid;
        links = new ArrayList<>();
        content = ctn;
        avatarLink = avaLink;
        audioLink = audLink;
        imageLinks = imglinks;
        userid = uid;
        username = uname;
        timestamp = ts;
    }

    public void init(){
        imageLinks = new ArrayList<>();
    }
    //Get-Set
    public String getSaveid() { return saveid; }

    public void setSaveid(String value) { saveid = value; }

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

    public String getAudioLink() { return audioLink; }

    public void setAudioLink(String value) {
        audioLink = value;
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

    //add
    public void addImageLink(String element) { imageLinks.add(element); }
}
