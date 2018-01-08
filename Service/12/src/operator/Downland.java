package operator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class Downland {

	public static void down(String str, Socket socket1) throws EOFException {
		Socket socket = socket1;

		// 接受客户端发过来的下载文件消息
		byte[] r = str.getBytes(); // 文件路径
		int len_str3 = r.length;
		String path = "";
		try {
			path = new String(r, 0, len_str3, "gbk");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 单击的时候

		try {

			File file = null;
			FileInputStream fi = null;
			BufferedInputStream bis = null;
			DataInputStream dis = null;

			file = new File(path);
			fi = new FileInputStream(file);
			bis = new BufferedInputStream(fi);
			dis = new DataInputStream(bis);

			BufferedOutputStream fos = null;
			DataOutputStream dos = null;
			fos = new BufferedOutputStream(socket.getOutputStream());
			dos = new DataOutputStream(fos);

			boolean re = true;

			byte[] by = new byte[1024];

			int len = -1; // 按字节读取剩余的内容

			while((len=dis.read(by))!=-1)
			{
				System.out.println(len);
				dos.write(by, 0, len);
			}

			dos.flush();

			fos.close();
			dos.close();
			fi.close();
			bis.close();
			dis.close();
			socket.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
