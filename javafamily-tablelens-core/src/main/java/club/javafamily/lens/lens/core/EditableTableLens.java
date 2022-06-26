package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;

/**
 * @author Jack Li
 * @date 2021/7/26 3:41 下午
 * @description
 */
public interface EditableTableLens {
   /**
    * 修改一个 Cell
    * @param row row index
    * @param col col index
    * @param cell Cell
    */
   void setObject(int row, int col, Cell cell);

   /**
    * setting cell data value
    * @param row row index
    * @param col col index
    * @param data cell data
    */
   default void setData(int row, int col, Object data) {
      setObject(row, col, new Cell(data));
   }

   /**
    * Reset
    */
   default void reset() {
      // no op
   }
}
