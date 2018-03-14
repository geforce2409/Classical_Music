package dataobject;

/**
 * Created by tranminhquan on 01/03/2018.
 */

public class Notification {
    private String id;
    private String senderName;  // tên người gửi
    private INTERACTION_TYPE type;  // loại tương tác
    private String postid;
    private String avatarLink;

    public Notification(){

    }

    public String getId() { return id; }

    public void setId(String value) { id = value; }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String value) { senderName = value; }

    public INTERACTION_TYPE getType() {
        return type;
    }

    public void setType(INTERACTION_TYPE value) { type = value; }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String value) { postid = value; }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String value) {
        avatarLink = value;
    }
}
