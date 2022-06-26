package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;

/**
 * @author Jack Li
 * @date 2021/7/26 3:46 下午
 * @description The default implement for {@link TableLens}
 */
public class DefaultTableLens implements TableLens, EditableTableLens {

   public DefaultTableLens() {
      this(0, 0);
   }

   public DefaultTableLens(TableLens table) {
      this.table = table;
   }

   public DefaultTableLens(int rowCount, int colCount) {
      this.rowCount = rowCount;
      this.colCount = colCount;
      reset();
   }

   public DefaultTableLens(Object[][] data) {
      Cell[][] cells = new Cell[data.length][];

      for (int i = 0; i < data.length; i++) {
         Cell[] row = new Cell[data[i].length];

         for (int j = 0; j < data[i].length; j++) {
            row[j] = new Cell(data[i][j]);
         }

         cells[i] = row;

         this.colCount = Math.max(row.length, this.colCount);
      }

      this.data = cells;
      this.rowCount = data.length;
   }

   public DefaultTableLens(Cell[][] cells) {
      this.data = cells;
   }

   @Override
   public void reset() {
      if (getTable() != null) {
         throw new UnsupportedOperationException("Unsupported");
      }

      if(rowCount <=0 && colCount <= 0) {
         return;
      }

      if(colCount > 0) {
         this.data = new Cell[rowCount][colCount];
      }
      else {
         this.data = new Cell[rowCount][];
      }
   }

   @Override
   public Cell getObject(int row, int col) {
      if (getTable() != null) {
         return getTable().getObject(row, col);
      }

      return data[row][col];
   }

   @Override
   public void setObject(int row, int col, Cell cell) {
      if (getTable() != null) {
         throw new UnsupportedOperationException("Unsupported");
      }

      data[row][col] = cell;
   }

   @Override
   public int getRowCount() {
      if (getTable() != null) {
         return getTable().getRowCount();
      }

      return rowCount;
   }

   @Override
   public int getColCount() {
      if (getTable() != null) {
         return getTable().getColCount();
      }

      return colCount;
   }

   public void setRowCount(int rowCount) {
      if (getTable() != null) {
         throw new UnsupportedOperationException("Unsupported");
      }

      this.rowCount = rowCount;
   }

   public void setColCount(int colCount) {
      if (getTable() != null) {
         throw new UnsupportedOperationException("Unsupported");
      }

      this.colCount = colCount;
   }

   public void setData(Cell[][] data) {
      if (getTable() != null) {
         throw new UnsupportedOperationException("Unsupported");
      }

      this.data = data;
   }

   @Override
   public TableLens getTable() {
      return table;
   }

   @Override
   public Cell[] getRow(int row) {
      if (getTable() != null) {
         return getTable().getRow(row);
      }

      return data[row];
   }

   protected Cell[][] data;
   protected int rowCount;
   protected int colCount;
   protected TableLens table;
}
