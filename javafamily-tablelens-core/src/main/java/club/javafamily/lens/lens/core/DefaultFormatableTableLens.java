package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.format.FormatInfo;
import club.javafamily.lens.lens.format.FormatModel;

/**
 * @author Jack Li
 * @date 2021/7/26 3:39 下午
 * @description Default can format/style 's implement for {@link TableLens}
 */
public class DefaultFormatableTableLens extends DefaultTableLens
   implements FormatableTableLens
{

   public DefaultFormatableTableLens() {
   }

   public DefaultFormatableTableLens(TableLens table) {
      super(table);
   }

   public DefaultFormatableTableLens(FormatInfo formatInfo) {
      this.formatInfo = formatInfo;
   }

   public DefaultFormatableTableLens(TableLens table, FormatInfo formatInfo) {
      super(table);
      this.formatInfo = formatInfo;
   }

   public DefaultFormatableTableLens(int rowCount, int colCount, FormatInfo formatInfo) {
      super(rowCount, colCount);
      this.formatInfo = formatInfo;
   }

   @Override
   public FormatModel getCellFormat(int row, int col) {
      return formatInfo.getCellFormat(row, col);
   }

   @Override
   public void setCellFormat(int row, int col, FormatModel formatModel) {
      formatInfo.setCellFormat(row, col, formatModel);
   }

   @Override
   public FormatModel getRowFormat(int row) {
      return formatInfo.getRowFormat(row);
   }

   @Override
   public void setRowFormat(int row, FormatModel formatModel) {
      formatInfo.setRowFormat(row, formatModel);
   }

   @Override
   public FormatModel getColFormat(int col) {
      return formatInfo.getColFormat(col);
   }

   @Override
   public void setColFormat(int col, FormatModel formatModel) {
      formatInfo.setColFormat(col, formatModel);
   }

   protected FormatInfo formatInfo = new FormatInfo();
}
