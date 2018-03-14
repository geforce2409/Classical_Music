package dataobject;

/**
 * Created by tranminhquan on 10/18/2017.
 * Class nay thua thong tin cua 1 like duoc tao ra
 */

public class Like extends Interaction {
    private String likeid;

    public Like(){
        super();
    }

    public Like(String lid, String pid, String uid, String uname, long ts){
        super(pid, uid, uname, ts);
        likeid = lid;
    }

    //Get-Set
    public String getLikeid() { return likeid; }

    public void setLikeid(String value) { likeid = value; }
}
