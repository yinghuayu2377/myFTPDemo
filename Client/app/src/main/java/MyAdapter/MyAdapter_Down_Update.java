package MyAdapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.client.R;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

import Database.Down;


/**
 * Created by lenovo on 2017/5/8.
 */
public class MyAdapter_Down_Update extends BaseAdapter{
    LayoutInflater inflater;
    private static final String TAG="MyAdapter_Down_Update";
    private ArrayList<Down> listItem;
    private Callback2 mCallback;

    //自定义接口，用于回调image点击事件到activity
    public interface Callback2
    {
        void click(View v,int i);  //点击的是哪一个开始按钮
        void click_end(View v,int i);   //点击的是哪一个暂停按钮
    }


    public MyAdapter_Down_Update(Context context, ArrayList<Down> listItems,Callback2 callback)
    {
        inflater= LayoutInflater.from(context);  //所使用的布局是哪一个
        this.listItem=listItems;
        this.mCallback=callback;
    }


    //返回listItem的数量
    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Down getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, final View view, ViewGroup viewGroup) {
        //将写好的listcontent_layout.xml文件转化为一个view
        final View view1 = inflater.inflate(R.layout.down_update_content_layout, null);

        TextView title_down_update = (TextView) view1.findViewById(R.id.title_content_down_update);  //标题
        ProgressBar pb = (ProgressBar) view1.findViewById(R.id.pb);  //进度条
        TextView index_down_update = (TextView) view1.findViewById(R.id.index_content);  //进度
        title_down_update.setText(listItem.get(i).getTitle());

        Long index_do=listItem.get(i).getIndex_state();
        Long state_do=listItem.get(i).getState();
        int percent= (int) (((index_do+1)*1.0/state_do)*100);
        pb.setProgress(percent);
        if(percent==100)
        {
            index_down_update.setText("完成");
        }
        else {
            index_down_update.setText(String.valueOf(percent));
        }
        ImageView image=(ImageView)view1.findViewById(R.id.start);  //开始按钮
        ImageView image_end=(ImageView)view1.findViewById(R.id.end);  //暂停按钮

        //点击的是哪一个开始按钮
        final int p=i;
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.click(v,p);  //调用接口中的点击事件

            }
        });
        //点击的是哪一个暂停按钮
        image_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.click_end(view,p);
            }
        });

        return view1;
    }

}
