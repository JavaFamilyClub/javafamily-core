package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;

/**
 * @author Jack Li
 * @date 2021/7/26 3:45 下午
 * @description Provider composite two tableLens's tableLens by row.
 * @DevWarning ignore right table col header's style.
 */
public class CompositeTableLens implements TableLens {

   private TableLens left;
   private TableLens right;

   private CompositeTableLens(TableLens left, TableLens right) {
      this.left = left;
      this.right = right;
   }

   public static TableLens createCompositeTableLens(TableLens left,
                                                    TableLens right)
   {
      if(left == null) {
         return right;
      }
      else if(right == null) {
         return left;
      }
      else {
         return new CompositeTableLens(left, right);
      }
   }

   @Override
   public Cell getObject(int row, int col) {
      final boolean headerCell = isRowHeader(row);
      TableLens table;

      if(col < left.getColCount()) {
         table = left;
      }
      else {
         table = right;
         col = col - left.getColCount();
      }

      boolean smallHeaderTable = table.getHeaderRowCount() < getHeaderRowCount();

      // invalid header region
      if(headerCell && smallHeaderTable && row >= table.getHeaderRowCount()) {
         return null;
      }
      else if(smallHeaderTable) {
         row = row - (getHeaderRowCount() - table.getHeaderRowCount());
      }

      // invalid row region
      if(row >= table.getRowCount()) {
         return null;
      }

      return table.getObject(row, col);
   }

   @Override
   public int getRowCount() {
      return Math.max(left.getRowCount(), right.getRowCount());
   }

   @Override
   public int getColCount() {
      return left.getColCount() + right.getColCount();
   }

   @Override
   public int getHeaderRowCount() {
      return Math.max(left.getHeaderRowCount(), right.getHeaderRowCount());
   }

   /**
    * @DevWarning just left header col
    */
   @Override
   public int getHeaderColCount() {
      return left.getHeaderColCount();
   }

   @Override
   public Cell[] getRow(int row) {
      throw new UnsupportedOperationException("unsupported now!");
   }
}
