package dataobject;

import android.media.Image;

import java.util.List;

/**
 * Created by tranminhquan on 10/18/2017.
 * Class chua thong tin ve comment
 */

public class Comment extends Interaction {
    //private String commentid;
    private String content;
    private List<String> tags;
    private List<String> links;
    private Image image;

    public Comment(){

    }

    public Comment(String pid, String uid, String uname, long ts, String cnt,
                   List<String> tgs, List<String> lks, Image img){
        super(pid, uid, uname, ts);
        //commentid = cmtid;
        content = cnt;
        tags = tgs;
        links = lks;
        image = img;
    }

    //Get-Set
    //public String getCommentid() { return  commentid; }
    public String getContent() { return content; }
    public List<String> getTags() { return tags; }
    public List<String> getLinks() { return links; }
    public Image getImage() { return image; }

    //public void setCommentid(String value) { commentid = value; }
    public void setContent(String value) { content = value; }
    public void setTags(List<String> value) { tags = value; }
    public void setLinks(List<String> value) { links = value; }
    public void setImage(Image value) { image = value; }

    public void addTag(String element) { tags.add(element); }
    public void addLink(String element) { links.add(element); }
}
