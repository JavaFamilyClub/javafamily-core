package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.utils.LensTool;

import java.awt.*;
import java.util.Map;

/**
 * @author Jack Li
 * @date 2021/7/26 3:51 下午
 * @description exportable table lens.
 */
public class ExportTableLens extends DefaultFormatableTableLens {
   private String tableName;
   private String sheetName;
   private boolean ignoreTitle = true;
   private Map<String, String> exportProperties;

   public ExportTableLens() {
   }

   public ExportTableLens(TableLens table) {
      this(table, null);
   }

   public ExportTableLens(TableLens table, String tableName) {
      this(table, tableName, null);
   }

   public ExportTableLens(TableLens table, String tableName, String sheetName) {
      this(table, tableName, sheetName, null);
   }

   public ExportTableLens(TableLens table, String tableName,
                          String sheetName,
                          Map<String, String> exportProperties)
   {
      this(table, tableName, sheetName, exportProperties, true);
   }

   public ExportTableLens(TableLens table,
                          String tableName,
                          String sheetName,
                          Map<String, String> exportProperties,
                          boolean ignoreTitle)
   {
      this.table = table;
      this.tableName = tableName;
      this.sheetName = sheetName;
      this.exportProperties = exportProperties;
      this.ignoreTitle = ignoreTitle;
   }

   public Color getFontColor() {
      return Color.orange;
   }

   public Font getTitleFont() {
      return new Font(LensTool.DEFAULT_HEADER_FONT.getName(), Font.BOLD, 22);
   }

   public String getTableName() {
      return this.tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public String getProperty(String key) {
      return exportProperties.get(key);
   }

   public boolean isIgnoreTitle() {
      return ignoreTitle;
   }

   public void setIgnoreTitle(boolean ignoreTitle) {
      this.ignoreTitle = ignoreTitle;
   }

   public String getSheetName() {
      return sheetName;
   }

   @Override
   public void setObject(int row, int col, Cell cell) {
      if (getTable() != null) {

         if(getTable() instanceof EditableTableLens) {
            ((EditableTableLens) getTable()).setObject(row, col, cell);
            return;
         }
         else {
            throw new UnsupportedOperationException("Unsupported Operator");
         }
      }

      if(data != null) {
         data[row][col] = cell;
      }
   }

   public void setSheetName(String sheetName) {
      this.sheetName = sheetName;
   }
}
