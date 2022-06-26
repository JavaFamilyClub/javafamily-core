package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.utils.LensTool;

/**
 * @author Jack Li
 * @date 2021/7/27 11:05 上午
 * @description Provider append two tableLens's tableLens.
 * @DevWarning ignore bottom table's header.
 */
public class AppendTableLens implements TableLens {

   private TableLens top;
   private TableLens bottom;

   private AppendTableLens(TableLens top, TableLens bottom) {
      this.top = top;
      this.bottom = bottom;
   }

   public static TableLens createAppendTableLens(TableLens top,
                                                 TableLens bottom)
   {
      if (LensTool.isDataEmpty(bottom)) {
         return top;
      }
      else if (LensTool.isDataEmpty(top)) {
         return bottom;
      }
      else {
         return new AppendTableLens(top, bottom);
      }
   }

   @Override
   public Cell getObject(int row, int col) {
      TableLens table;

      if (row < top.getRowCount()) {
         table = top;
      }
      else {
         table = bottom;
         row = getRealRowIndex(row);
      }

      return table.getObject(row, col);
   }

   @Override
   public int getRealRowIndex(int row) {
      if (row >= top.getRowCount()) {
         return row - top.getRowCount() + bottom.getHeaderRowCount();
      }

      return row;
   }

   @Override
   public int getRowCount() {
      return top.getRowCount() + bottom.getDataRowCount();
   }

   @Override
   public int getColCount() {
      return top.getColCount();
   }

   @Override
   public int getHeaderRowCount() {
      return top.getHeaderRowCount();
   }

   /**
    * @DevWarning just left header col
    */
   @Override
   public int getHeaderColCount() {
      return top.getHeaderColCount();
   }

   @Override
   public Cell[] getRow(int row) {
      if (row >= top.getRowCount()) {
         return bottom.getRow(getRealRowIndex(row));
      }

      return top.getRow(row);
   }

}
