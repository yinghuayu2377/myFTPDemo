package Excel;

import jxl.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class CreateExcel {

	public static void Create() throws WriteException, JDOMException
	{
		 try {
			WritableWorkbook workbook = Workbook.createWorkbook(new File("D:/Message/messageExcel.xls"));
			WritableSheet writesheet=workbook.createSheet("smss", 0);
			WritableCellFormat format=new WritableCellFormat();
			format.setAlignment(Alignment.CENTRE);
		    
			SAXBuilder bulider=new SAXBuilder();
			Document document=bulider.build("D:/Message/message.xml");
			Element root=document.getRootElement();
			
			List list=root.getChildren();
			Iterator iterator=list.iterator();
			int i=0;
			int j=0;
			
			while(iterator.hasNext())
			{
				i++;
				Object obj=iterator.next();
				if(obj instanceof Element)
				{
					Element child=(Element)obj;
					List childContent=child.getChildren();
					Iterator childIterator=childContent.iterator();
					
					while(childIterator.hasNext())
					{
						Object p=childIterator.next();
						if(p instanceof Element)
						{
							Element data=(Element)p;
							Label label=new Label(j,i,data.getTextTrim(),format);
							writesheet.addCell(label);
							j++;
						}
					}
					j=0;
				}
			}
			workbook.write();
			workbook.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 
	}
}
