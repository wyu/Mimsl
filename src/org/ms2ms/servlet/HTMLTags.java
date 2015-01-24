package org.ms2ms.servlet;

import com.hfg.html.*;
import com.hfg.xml.Doctype;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 5/9/14
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTMLTags
{
  public static Tr newHeaderRow(Table table, String... cells)
  {
    if (table!=null)
    {
      Tr row = table.addRow();
      for (String cell : cells) row.addHeaderCell(cell);
      return row;
    }
    return null;
  }
  public static Tr newRow(Table table, String... cells)
  {
    if (table!=null)
    {
      Tr row = table.addRow();
      for (String cell : cells)
      {
        String[] items = cell.split("\\^");
        Td C=row.addCell(items[0]);
        if (items.length>1) C.setClass(items[1]);
      }
      return row;
    }
    return null;
  }
  public static HTMLDoc newHTMLDoc(HTML html, String title)
  {
    HTMLDoc doc = new HTMLDoc(Doctype.HTML_4_01_TRANSITIONAL);
    doc.setRootNode(html);
    if (Strs.isSet(title)) html.getHead().setTitle(title);
    //html.getHead().addJavascriptLink("path to js file");
    html.getHead().addStyleSheetLink("css/ms2ms.css");
    //html.getHead().addJavascript("some script");
    //html.getHead().addStyleTag("some css");

    return doc;
  }
}
