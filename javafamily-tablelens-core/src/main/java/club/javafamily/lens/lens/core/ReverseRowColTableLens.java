package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;

/**
 * @author Jack Li
 * @date 2021/7/27 10:56 上午
 * @description 用于翻转行列的 TableLens
 */
public class ReverseRowColTableLens implements TableLens {

   private TableLens lens;

   public ReverseRowColTableLens(TableLens lens) {
      this.lens = lens;
   }

   @Override
   public Cell getObject(int row, int col) {
      return lens.getObject(col, row);
   }

   @Override
   public int getRowCount() {
      return lens.getColCount();
   }

   @Override
   public int getColCount() {
      return lens.getRowCount();
   }

   @Override
   public Cell[] getRow(int row) {
      return lens.getColData(row).toArray(new Cell[0]);
   }
}
