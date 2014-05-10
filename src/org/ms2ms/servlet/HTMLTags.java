package org.ms2ms.servlet;

import com.hfg.html.Table;
import com.hfg.html.Tr;

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
      for (String cell : cells) row.addCell(cell);
      return row;
    }
    return null;
  }
}
