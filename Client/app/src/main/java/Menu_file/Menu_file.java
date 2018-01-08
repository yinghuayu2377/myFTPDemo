package Menu_file;

/**
 * Created by lenovo on 2017/4/5.
 */
public class Menu_file {
    private String title;
    private String type;
    private String date;
    private String size;

    public Menu_file(String title, String type, String date, String size)
    {
        this.title=title;
        this.type=type;
        this.date=date;
        this.size=size;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

}
