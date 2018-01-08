package Database;

/**
 * Created by lenovo on 2017/5/8.
 */
public class Down {

    private String title;  //名字
    private String url_down;   //链接
    private String url_down_path;  //文件夹路径
    private long index_state;  //进度
    private long state;  //状态

    public Down(String title, String url_down, String url_down_path,long state,long index_state){
        this.title=title;
        this.url_down=url_down;
        this.state=state;
        this.index_state=index_state;
        this.url_down_path=url_down_path;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl()
    {
        return url_down;
    }
    public void setUrl(String url_down) {
        this.url_down = url_down;
    }

    public String getUrl_down_path()
    {
        return url_down_path;
    }
    public void setUrl_down_path(String url_down_path) {
        this.url_down_path = url_down_path;
    }

    public long getIndex_state() {
        return index_state;
    }
    public void setIndex_state(long index_state) {
        this.index_state = index_state;
    }

    public long getState() {
        return state;
    }
    public void setState(long state) {
        this.state = state;
    }

}
