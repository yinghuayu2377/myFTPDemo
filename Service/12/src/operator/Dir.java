package operator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dir {

	public static String exe(String str) {
		// 接受客户端发过来的检索目录消息

		String path = str; // 文件夹路径
		String str1 = "";
		int len_str;
		int index=-1;
		String str3 = "";

		// 单击的时候

		if (path.equals("我的电脑")) // 读取系统根目录
		{
			File[] root = File.listRoots();
			for (File f : root) {
				System.out.println(f + "[menu]");

				// 更新时间
				long lastModified = f.lastModified();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String fileDate = dateFormat.format(new Date(lastModified));

				str1 = str1 + f + "[menu]";
				str1 = str1 + fileDate + "[date]" + "[size]" + "\n\r";

			}

			// 消除"/"符号
			len_str = str1.length();
			while (len_str > 0) {
				if ((index = str1.indexOf("[menu]")) != -1) {
					String out_string = str1.substring(0, index - 1) + "[menu]";
					str1 = str1.substring(index + 6, len_str);
					len_str = len_str - index - 6;
					str3 = str3 + out_string;
				} else
					break;
			}

			str1 = str3 + str1;
		} 
		
		
		else {  //读取其他文件路径
			File file = new File(path);

			if (!file.exists()) {
				System.out.printf(path + " not exists");
				str1 = "notexists";
			} else {
				File files[] = file.listFiles();

				System.out.println(files.length);
				for (int i = 0; i < files.length; i++) {
					File fs = files[i];
					
					//文件夹
					if (fs.isDirectory()) {

						System.out.println(fs.getName() + "[menu]");
						str1 = str1 + fs.getName() + "[menu]";
						// 更新时间
						long lastModified = fs.lastModified();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String fileDate = dateFormat.format(new Date(
								lastModified));

						str1 = str1 + fileDate + "[date]" + "[size]" + "\n\r";

					} 
					
				
					else {   //文件
						System.out.println(fs.getName());
						str1 = str1 + fs.getName() + "[file]";
						// 更新时间
						long lastModified = fs.lastModified();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String fileDate = dateFormat.format(new Date(
								lastModified));

						// 文件大小
						String fileSize = "0";
						fileSize = "" + fs.length();

						str1 = str1 + fileDate + "[date]" + fileSize + "[size]"
								+ "\n\r";
					}
				}

			}
		}

		System.out.println(str1);
		return str1;
	}

}
