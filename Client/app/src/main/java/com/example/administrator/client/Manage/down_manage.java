package com.example.administrator.client.Manage;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.client.R;

import java.io.File;
import java.util.ArrayList;

import ClientSocket.ClientSocket_down;
import Database.Down;
import Database.DownDB;
import MyAdapter.MyAdapter_Down_Update;


public class  down_manage extends AppCompatActivity implements MyAdapter_Down_Update.Callback2 {

    ImageView image_return;
    ListView list_down_manage;
    MyAdapter_Down_Update adapter;
    private Handler handler;
    public ArrayList<Down> mDown_List = new ArrayList<Down>();
    private DownDB mDown_DB;
    private ArrayList<String> mDown_title = new ArrayList<String>();  //标题
    private ArrayList<String> mDown_Url = new ArrayList<String>();  //链接
    private ArrayList<Long> mDown_state = new ArrayList<Long>();  //状态
    private ArrayList<Long> mDown_index = new ArrayList<Long>();  //进度
    private ArrayList<String> mDown_Url_Path = new ArrayList<String>();   //文件夹路径
    private SQLiteDatabase db;
    private SQLiteDatabase db2;
    private DownDB mydb = new DownDB(this);
    Cursor cursor;
    Cursor cursor2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down_manage_layout);

        //返回按钮
        image_return = (ImageView) findViewById(R.id.return_down);
        image_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.return_down:
                        finish();
                        break;

                    default:
                        break;
                }
            }
        });

        //下载列表
        db = mydb.getWritableDatabase();  //得到数据库实例
        //查询数据库
        cursor = db.rawQuery("select _id,title_down,url_down,url_down_path,state_down,index_down from mytable order by title_down", null);  //游标
        list_down_manage = (ListView) findViewById(R.id.listview_down);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                Down down2=(Down)msg.obj;
                int position=msg.what;
                mDown_List.set(position,down2);
                adapter.notifyDataSetChanged();

            }

        };

        ReadCursor();
        adapter = new MyAdapter_Down_Update(down_manage.this, mDown_List, this);
        list_down_manage.setAdapter(adapter);
    }

    //遍历数据库
    public void ReadCursor() {
        //遍历数据库
        cursor.moveToFirst();  //要定位到第一列
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String title_d = cursor.getString(1);
            String url_d = cursor.getString(2);
            String url_d_p = cursor.getString(3);
            long state_d = cursor.getLong(4);
            long index_d = cursor.getLong(5);
            mDown_title.add(title_d);  //标题
            mDown_Url.add(url_d);  //链接
            mDown_state.add(state_d);  //状态(实际为文件的大小)
            mDown_index.add(index_d);  //进度
            mDown_Url_Path.add(url_d_p);   //文件夹路径

            Down down = new Down(title_d, url_d, url_d_p, state_d, index_d);
            mDown_List.add(down);

            cursor.moveToNext();
        }
        cursor.close();
    }

    //点击开始按钮命令
    @Override
    public void click(View v, int i) {

        int position_image_start = i;   //点击的ImageView是哪一个
        Long down_size = mDown_index.get(position_image_start);  //下载了多少
        Long file_size = mDown_state.get(position_image_start);   //文件原始大小
        String url_down = mDown_Url.get(position_image_start);  //文件的路径
        String url_down_path = mDown_Url_Path.get(position_image_start);   //文件夹的路径
        String title_down = mDown_title.get(position_image_start);  //文件的名字

        db2 = mydb.getReadableDatabase();
        String[] columns = {DownDB.Index_down};
        String[] str = {url_down};
        cursor2 = db2.query(DownDB.table_name, null, "Url_down=?", str, null, null, null, null);
        cursor2.moveToFirst();  //要定位到第一列
        Long down_size3 = Long.valueOf(down_size);

        while (!cursor2.isAfterLast()) {

            down_size3 = cursor2.getLong(5);
            cursor2.moveToNext();
        }
        cursor2.close();

        //还没有下载完毕(包括已经下载和没有下载)
        if (down_size3 < file_size) {

            String path = "/storage/emulated/0" + File.separator + "file" + File.separator;
            newThread(url_down, "5", path, title_down, file_size, down_size3, position_image_start,url_down_path);
        }
        //已经下载完毕
        else if (down_size3 >= file_size) {
            Toast.makeText(down_manage.this, title_down + "下载已完成", Toast.LENGTH_LONG).show();
        }
    }

    //点击暂停按钮命令
    @Override
    public void click_end(View v, int i) {

        Long down_size = mDown_index.get(i);  //下载了多少
        Long file_size = mDown_state.get(i);   //文件原始大小
        String url_down = mDown_Url.get(i);  //文件的路径

        //还没下载完毕
        if (down_size < file_size) {
            //发送暂停命令
            int position_image_start = i;   //点击的ImageView是哪一个
            Long down_size2 = mDown_index.get(position_image_start);  //下载了多少
            Long file_size2 = mDown_state.get(position_image_start);   //文件原始大小
            String url_down2 = mDown_Url.get(position_image_start);  //文件的路径
            String url_down_path2 = mDown_Url_Path.get(position_image_start);   //文件夹的路径
            String title_down2 = mDown_title.get(position_image_start);  //文件的名字

            String path = "/storage/emulated/0" + File.separator + "file" + File.separator;

            newThread(url_down, "6", path, title_down2, file_size2, down_size2, position_image_start,url_down_path2);

        }
    }

    private void newThread(final String url_down, final String type, final String path, final String title_down, final Long file_size, final Long down_size, final int i, final String url_down_path) {
        new Thread(new Runnable() {

            String title = title_down;

            @Override
            public void run() {


                if (type.equals("5"))   //断点续传
                {
                    //开始执行下载命令
                    ClientSocket_down clientSocket_duan_start = new ClientSocket_down();
                    clientSocket_duan_start.doConnect(getBaseContext(), url_down, "5", path, title_down, file_size, down_size, handler,i,url_down_path);
                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("duan_state", "run");
                    editor.commit();

                } else if (type.equals("6"))  //停止断点续传
                {

                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("duan_state", "stop");
                    editor.commit();

                }
            }
        }).start();

    }

}
