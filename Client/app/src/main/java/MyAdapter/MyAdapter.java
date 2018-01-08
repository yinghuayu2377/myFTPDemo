package MyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.client.R;

import java.util.ArrayList;

import Menu_file.Menu_file;

/**
 * Created by lenovo on 2017/3/23.
 */
public class MyAdapter extends BaseAdapter {


    LayoutInflater inflater;
    private ArrayList<Menu_file> listItem;

    public MyAdapter(Context context, ArrayList<Menu_file> listItems)
    {
        inflater= LayoutInflater.from(context);  //所使用的布局是哪一个
        this.listItem=listItems;
    }

    //返回listItem的数量
    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Menu_file getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //将写好的listcontent_layout.xml文件转化为一个view
        View view1=inflater.inflate(R.layout.menu_layout,null);
        ImageView image=(ImageView)view1.findViewById(R.id.list_image);
        TextView tv_title=(TextView)view1.findViewById(R.id.title_content);
        TextView tv_date=(TextView)view1.findViewById(R.id.list_date);
        TextView tv_size=(TextView)view1.findViewById(R.id.list_size);
        String type;

        if(listItem.get(i).getType()=="1")
        {
            tv_title.setText(listItem.get(i).getTitle());
            tv_date.setText(listItem.get(i).getDate());
            tv_size.setVisibility(View.GONE);  //设为不可见
            image.setImageResource(R.drawable.menu);
        }
        else
        {
            tv_title.setText(listItem.get(i).getTitle());
            tv_date.setText(listItem.get(i).getDate());
            tv_size.setText(listItem.get(i).getSize());  //设为不可见
            image.setImageResource(R.drawable.doc);
        }

        return view1;
    }
}

