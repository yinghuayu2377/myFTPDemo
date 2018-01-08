package com.example.administrator.client;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileOutputStream;
import com.example.administrator.client.Manage.down_manage;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;

import ClientSocket.ClientSocket;
import ClientSocket.ClientSocket_down;
import ClientSocket.ClientSocket_update;
import Database.DownDB;
import Menu_file.Menu_file;
import MyAdapter.MyAdapter;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private EditText edit;
    private ListView list;
    private ImageView image_return;
    private Button button;
    private ArrayList<Menu_file> arrayList;
    private ArrayList<String> last_arrayList;   //保存之前遍历的文件路径
    private ArrayList<Menu_file> listItems;

    private String edit_text;
    private String result;
    private RelativeLayout layout_menu;
    private MyAdapter adapter;
    //上下文菜单
    private static final int ITEM6 = Menu.FIRST + 5;
    private static final int ITEM7 = Menu.FIRST + 6;
    private static final int ITEM8 = Menu.FIRST + 7;

    //选项菜单
    private static final int ITEM1 = Menu.FIRST;
    private static final int ITEM2 = Menu.FIRST + 1;
    private static final int ITEM3 = Menu.FIRST + 2;
    private static final int ITEM4 = Menu.FIRST + 3;
    private static final int ITEM5 = Menu.FIRST + 4;
    private static final int ITEM9 = Menu.FIRST + 8;

    private String str_down = "";
    private SQLiteDatabase db;
    private DownDB mydb = new DownDB(this);
    Cursor cursor;
    long _id;
    Long size_duan_down = Long.valueOf(0);  //要下载的文件大小

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        edit = (EditText) findViewById(R.id.edit1);
        list = (ListView) findViewById(R.id.listview);
        button = (Button) findViewById(R.id.butt);
        image_return = (ImageView) findViewById(R.id.image_return);
        arrayList = new ArrayList<>();
        last_arrayList = new ArrayList<>();
        edit_text = "";

        layout_menu = (RelativeLayout) findViewById(R.id.menu_layout);   //这个代表的是menu_layout
        last_arrayList.add("我的电脑");
        edit.setText("我的电脑");
        newThread("我的电脑", "1", "");  //初始时访问的是系统的根目录

        listItems = new ArrayList<>();
        listItems = arrayList;
        adapter = new MyAdapter(MainActivity.this, listItems);
        list.setAdapter(adapter);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                adapter.notifyDataSetChanged();
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                result = arrayList.get(position).getTitle();

                //打开文件夹
                String str;
                if ((str = arrayList.get(position).getType()) == "1") {
                    arrayList.clear();
                    edit_text = edit_text + result + File.separator;
                    edit.setText(edit_text);
                    last_arrayList.add(edit_text);
                    newThread(edit.getText().toString(), "1", "");   //new一个新线程
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_text = edit.getText().toString() + "/";
                last_arrayList.add(edit_text);
                newThread(edit.getText().toString(), "1", "");  //new一个新的线程
            }
        });

        image_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int leng_last = last_arrayList.size();
                if (leng_last > 1) {
                    newThread(last_arrayList.get(leng_last - 2).toString(), "1", "");
                    edit.setText(last_arrayList.get(leng_last - 2).toString());
                    edit_text = edit.getText().toString();
                    if (edit_text.equals("我的电脑")) {
                        edit_text = "";
                    }
                    last_arrayList.remove(leng_last - 1);
                }
            }
        });

        //注册上下文菜单
        this.registerForContextMenu(list);
    }

    //new一个新的线程
    private void newThread(final String input, final String type, final String str) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                //1  遍历
                //2  下载
                //3  上传
                //4  加入下载列表
                //5  开始断点下载
                //6  暂停断点下载

                //8  在服务器端打开
                //9  打开服务器端浏览器

                if (type.equals("1"))   //遍历目录
                {
                    ClientSocket clientSocket = new ClientSocket();
                    clientSocket.doConnect(input, type);   //与服务器连接

                    arrayList.clear();
                    int inde = clientSocket.getArrayList().size();
                    for (int i = 0; i < inde; i++) {
                        final String str_re = clientSocket.getArrayList().get(i);  //title,date,type,size等信息的一条字符串
                        Menu_file menus = null;
                        int in;
                        int in2;
                        int in3;

                        //将每一个字段都分出来  文件夹
                        if ((in = str_re.indexOf("[menu]")) != -1) {

                            String title_str = str_re.substring(0, in);

                            in2 = str_re.indexOf("[date]");
                            in3 = str_re.indexOf("[size]");
                            String date_str = "更新时间：" + str_re.substring(in + 6, in2);
                            menus = new Menu_file(title_str, "1", date_str, "-1");  //构造menu对象

                        }
                        //文件
                        else {

                            in = str_re.indexOf("[file]");
                            String title_str = str_re.substring(0, in);

                            in2 = str_re.indexOf("[date]");
                            in3 = str_re.indexOf("[size]");
                            String date_str = "更新时间：" + str_re.substring(in + 6, in2);
                            String size_str = str_re.substring(in2 + 6, in3) + " B";
                            menus = new Menu_file(title_str, "2", date_str, size_str);
                        }
                        arrayList.add(menus);
                    }
                } else if (type.equals("2")) //下载小文件
                {
                  /*  String path="/data"+ Environment.getDataDirectory().getAbsolutePath()
                            +File.separator+getPackageName()
                            +File.separator+"file"+File.separator; */ //文件夹路径

                    String path = "/storage/emulated/0" + File.separator + "file" + File.separator;
                    ClientSocket_down clientSocket_down = new ClientSocket_down();
                    clientSocket_down.doConnect(getBaseContext(), input, type, path, str_down, (long) 0, (long) 0, null, -1, null);   //与服务器连接

                } else if (type.equals("3"))//上传小文件
                {
                    ClientSocket_update clientSocket_update = new ClientSocket_update();
                    clientSocket_update.doConnect(input, type, str);   //与服务器连接

                } else if (type.equals("4"))   //断点下载  //因为是4，所以只会传输文件大小，没有其他内容
                {
                    ClientSocket clientSocket_down_duan = new ClientSocket();
                    clientSocket_down_duan.doConnect(input, type);
                    size_duan_down = clientSocket_down_duan.getFileSize();  //获取的断点续传原始文件大小
                } else if (type.equals("8"))   //在服务器端打开
                {
                    ClientSocket clientSocket = new ClientSocket();
                    clientSocket.doConnect(input, type);
                } else if (type.equals("9"))   //打开服务器端浏览器
                {
                    ClientSocket clientSocket = new ClientSocket();
                    clientSocket.doConnect(input, type);
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    //上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v == list) {
            menu.add(0, ITEM6, 0, "下载此文件");
            menu.add(0, ITEM7, 1, "加入下载列表");
            menu.add(0, ITEM8, 2, "在服务器端打开");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //下载小文件
            case ITEM6:

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final int position_longclick = info.position;

                result = arrayList.get(position_longclick).getTitle();
                Log.e("result", result);
                //下载文件
                String str;
                if ((str = arrayList.get(position_longclick).getType()) != "1") {
                    str_down = result;
                    newThread(edit_text + result, "2", "");   //new一个新线程
                }
                break;

            //加入下载列表
            case ITEM7:

                AdapterView.AdapterContextMenuInfo info1 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final int position_longclick1 = info1.position;

                //加入下载列表
                String str1;
                if ((str1 = arrayList.get(position_longclick1).getType()) != "1") {

                    //发送消息，获取文件的大小
                    String str_duan_down = arrayList.get(position_longclick1).getTitle();
                    newThread(edit_text + str_duan_down, "4", "");

                    db = mydb.getWritableDatabase();  //得到数据库实例
                    String title_1 = arrayList.get(position_longclick1).getTitle();
                    String url_1 = edit_text + str_duan_down;
                    Long index_1 = Long.valueOf(0);
                    insert(title_1, url_1, edit_text, size_duan_down, index_1);  //size_duan_down的内容是获取的断点下载文件的原始大小
                }
                break;


            //在服务器端打开某个文件
            case ITEM8:
                AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final int position_longclick2 = info2.position;

                String str2;
                if ((str1 = arrayList.get(position_longclick2).getType()) != "1") {  //如果是文件的话

                    //发送消息，在服务器端打开文件
                    String str_duan_down = arrayList.get(position_longclick2).getTitle();
                    newThread(edit_text + str_duan_down, "8", "");

                }
                break;

        }
        return true;
    }

    //选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ITEM1, 0, "上传文件");
        menu.add(0, ITEM2, 1, "加入上传列表");
        menu.add(0, ITEM3, 2, "下载列表");
        menu.add(0, ITEM4, 3, "上传列表");
        menu.add(0, ITEM5, 4, "打开服务器端浏览器");
        menu.add(0, ITEM9, 5, "备份短信");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ITEM1:  //上传文件
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  //本地文件
                intent.setType("file/*");
                // "file/*"设置数据对象为文件类型，则系统会默认调用文件浏览器，
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 0);
                break;

            case ITEM2:  //加入上传列表

                break;

            case ITEM3:  //下载列表
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, down_manage.class);
                startActivity(intent1);
                //   startActivityForResult(intent1,0);  //传递intent参数

                break;

            case ITEM4:  //上传列表

                break;

            case ITEM5:  //打开服务器端浏览器

                newThread("baidu.com", "9", "");
                break;

            //备份短信
            case ITEM9:
             //   Intent intent3 = new Intent(this,BackupSmsService.class);
              //  startService(intent3);

                try {
                    //创建一个存储备份短信的文件对象
                    File smsBackUpFile = new File("/storage/emulated/0" + File.separator + "file" + File.separator+"message.xml");

                    //创建一个xml文件的生成器。
                    XmlSerializer serializer = Xml.newSerializer();
                    //完成序列化器初始化操作。
                    FileOutputStream os = new FileOutputStream(smsBackUpFile);
                    serializer.setOutput(os, "utf-8");
                    //内容提供者。
                    //获取到一个数据库的内容的解析者
                    ContentResolver resolver = getContentResolver();
                    //游标（结果集）
                    Cursor cursor3 = resolver.query(Uri.parse("content://sms"),
                            new String[]{"address","date","type","body"}, null, null, null);
                    //生成xml文件的头
                    serializer.startDocument("utf-8", true);
                    serializer.startTag(null, "smss");
                    while(cursor3.moveToNext()){
                        serializer.startTag(null, "sms");
                        String address = cursor3.getString(0);
                        String date = cursor3.getString(1);
                        String type = cursor3.getString(2);
                        String body = cursor3.getString(3);

                        serializer.startTag(null, "address");
                        serializer.text(address);
                        serializer.endTag(null, "address");

                        serializer.startTag(null, "date");
                        serializer.text(date);
                        serializer.endTag(null, "date");

                        serializer.startTag(null, "body");
                        serializer.text(body);
                        serializer.endTag(null, "body");

                        serializer.startTag(null, "type");
                        serializer.text(type);
                        serializer.endTag(null, "type");

                        serializer.endTag(null, "sms");
                    }
                    cursor3.close();
                    serializer.endTag(null, "smss");
                    serializer.endDocument();
                    os.close();
                    Toast.makeText(getApplicationContext(), "备份完成",Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //然后将xml文件上传到服务器端
                newThread("D:/Message", "3", "/storage/emulated/0" + File.separator + "file" + File.separator+"message.xml");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //将图片的映射路径转换为真实路径
    public String getRealFilePath(final Uri uri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Uri uri = data.getData();
            String path = uri.getPath();

            //将图片的映射路径转换为真实路径
            if (uri.toString().contains("content://")) {
                path = getRealFilePath(uri);
            }
            Log.e("requestCode", "ok");
            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            newThread(edit_text, "3", path);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //增加下载内容
    private void insert(String title, String url, String url_path, Long state, Long index) {
        ContentValues cv = new ContentValues();
        cv.put(DownDB.Title_down, title);
        cv.put(DownDB.Url_down, url);
        cv.put(DownDB.Url_down_path, url_path);
        cv.put(DownDB.State_down, state);
        cv.put(DownDB.Index_down, index);
        db.insert(DownDB.table_name, DownDB.Title_down, cv);
    }

}
