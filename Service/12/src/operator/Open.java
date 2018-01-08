package operator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Open {
	
	public static boolean open(String str)
	{
		Desktop desktop=Desktop.getDesktop();
		File file=new File(str);
		try {
			desktop.open(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static boolean openBrow() throws URISyntaxException
	{
		Desktop desktop=Desktop.getDesktop();
		try {
			desktop.browse(new URI("http://baidu.com"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
