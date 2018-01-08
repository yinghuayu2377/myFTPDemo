package ClientSocket;

import android.app.Notification;
import android.os.Handler;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;
import java.lang.String;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;

import Database.Down;
import Database.DownDB;

import Ip_Port.Ip_Port;
import MyAdapter.MyAdapter_Down_Update;


/**
 * Created by lenovo on 2017/4/11.
 */
public class ClientSocket_down {

    private Socket client;
    private Writer writer;
    private String path;
    private String str_down;
    private Long file_size;
    private Long down_size;
    private BufferedInputStream bis = null;
    private DataInputStream dis = null;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private DataOutputStream dos = null;
    private Context context;
    private SQLiteDatabase db_down;
    private DownDB mydb;
    private String cmd;
    private Handler handler;
    private int position;
    private String url_down_path;

    //与服务器端进行连接
    public void doConnect(Context context, String cmd, String type, String path2, String str_down2, Long file_size, Long down_size  , Handler handler,int position,String url_down_path) {

        //与服务端建立连接
        //客户端进行写操作
        this.path = path2;  //下载后的文件夹路径
        this.str_down = str_down2;
        this.file_size = file_size;   //文件的原始大小
        this.down_size = down_size;   //上次记录的文件开始
        this.context=context;
        this.cmd=cmd;
        mydb=new DownDB(context);
        this.handler=handler;
        this.position=position;
        this.url_down_path=url_down_path;  //服务器端文件夹路径

        try {

            client = new Socket(Ip_Port.getIP(), Ip_Port.getPort());
            writer = new OutputStreamWriter(client.getOutputStream(), "utf-8");
            writer.write(type);
            writer.write(cmd);

            if (type.equals("5")) {
                writer.write("[Path]");
                writer.write(String.valueOf(down_size));
            }

            writer.write("eof\n");
            writer.flush();//写完后要记得flush

            //读取从服务器端传过来的数据
            ReadString(type);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //建立连接后就可以往服务端发送数据
    }

    //读取从服务器端传过来的数据
    private void ReadString(String type) throws IOException {

        client.setSoTimeout(200 * 1000);
        if (type.equals("2") || type.equals("5"))  //下载或者断点下载命令
        {

            bis = new BufferedInputStream(client.getInputStream());
            dis = new DataInputStream(bis);

            File file = new File(path);
            if (!file.isDirectory()) {
                file.mkdirs();   //创建文件夹
            }

            try {

                File db = new File(file + File.separator + str_down);

                //如果断点下载的文件不存在或者是下载不管存不存在
                if (down_size == 0) {
                    File myfile = new File(file, str_down);
                    try {
                        myfile.createNewFile();  //创建文件

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (type.equals("2")) {   //下载不能追加，一下下完
                        fos = new FileOutputStream(db);
                    } else if (type.equals("5"))  //断点下载可以追加
                    {
                        fos = new FileOutputStream(db, true);  //可以追加

                    }
                    bos = new BufferedOutputStream(fos);
                    dos = new DataOutputStream(bos);

                    byte[] by = new byte[1024];

                    int len = -1; // 按字节读取剩余的内容
                    while ((len = dis.read(by)) != -1) {
                        SharedPreferences sharedPreferences3 = context.getSharedPreferences("config", context.MODE_PRIVATE);
                        String duan_state = sharedPreferences3.getString("duan_state", "run");
                        Log.e("duan_state",duan_state);
                        if(duan_state.equals("run")) {
                            down_size += len;
                            System.out.println(down_size);
                            dos.write(by, 0, len);

                            if(type.equals("5")) {
                                Message msg = new Message();
                                msg.what = position;
                                Down down = new Down(str_down, cmd, url_down_path, file_size, down_size);
                                msg.obj = down;
                                handler.sendMessage(msg);
                            }

                        }
                        else
                        {
                            dos.flush();
                            dos.close();

                            //将新的文件下载大小存入数据库中
                            db_down=mydb.getWritableDatabase();  //得到数据库实例
                            update(cmd,Long.valueOf(down_size));

                        }

                    }


                } else {   //如果断点下载的文件已经存在
                    File db2 = new File(file + File.separator + str_down);
                    fos = new FileOutputStream(db2, true);  //可以追加内容
                    bos = new BufferedOutputStream(fos);
                    dos = new DataOutputStream(bos);

                    byte[] by = new byte[1024];

                    int len = -1; // 按字节读取剩余的内容
                    while ((len = dis.read(by)) != -1) {
                        SharedPreferences sharedPreferences3 = context.getSharedPreferences("config", context.MODE_PRIVATE);
                        String duan_state = sharedPreferences3.getString("duan_state", "run");
                        Log.e("duan_state",duan_state);
                        if(duan_state.equals("run")) {
                            down_size += len;
                            System.out.println(down_size);
                            dos.write(by, 0, len);

                            if(type.equals("5")) {
                                Message msg = new Message();
                                Down down = new Down(str_down, cmd, path, file_size, down_size);
                                msg.what = position;
                                msg.obj = down;
                                handler.sendMessage(msg);
                            }

                        }
                        else
                        {
                            dos.flush();
                            dos.close();

                            //将新的文件下载大小存入数据库中
                            db_down=mydb.getWritableDatabase();  //得到数据库实例
                            update(cmd,Long.valueOf(down_size));

                        }

                    }
                }

                dos.flush();

                dis.close();
                bis.close();
                fos.close();
                dos.close();
                //将新的文件下载大小存入数据库中
                db_down=mydb.getWritableDatabase();  //得到数据库实例
                update(cmd,Long.valueOf(down_size));

                writer.close();
                Log.e("stop","stop5");


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            client.close();
        }

    }


    //修改下载内容
    private void update(String cmd , Long index) {
        ContentValues cv=new ContentValues();

        String where="Url_down=?";
        String[] whereValue={cmd};
        cv.put("index_down",index);
        db_down.update(DownDB.table_name,cv,where,whereValue);
    }

}
