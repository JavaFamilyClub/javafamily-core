package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.cell.CellValueType;
import club.javafamily.lens.lens.utils.LensTool;
import club.javafamily.utils.Tool;
import club.javafamily.utils.number.NumberUtil;
import club.javafamily.utils.spring.ObjectUtils;

/**
 * @author Jack Li
 * @date 2021/7/29 5:17 下午
 * @description simple diff tableLens for same struct tableLens.
 */
public class SimpleDiffTableLens extends DefaultTableLens {

   private static final String RIGHT_COLUMN_SUFFIX = "_2";
   private static final String DEFAULT_DIFF_COLUMN_NAME = "diff";
   private static final int DEFAULT_POINT_COUNT = 1;

   public SimpleDiffTableLens(TableLens left,
                              TableLens right)
   {
      this(left, right, DEFAULT_DIFF_COLUMN_NAME);
   }

   public SimpleDiffTableLens(TableLens left,
                              TableLens right,
                              String diffColumnName)
   {
      this(left, right, diffColumnName, DEFAULT_POINT_COUNT);
   }

   public SimpleDiffTableLens(TableLens left,
                              TableLens right,
                              int point)
   {
      this(left, right, DEFAULT_DIFF_COLUMN_NAME, point);
   }

   public SimpleDiffTableLens(TableLens left,
                              TableLens right,
                              String diffColumnName,
                              int point)
   {
      rowCount = Math.max(left.getRowCount(), right.getRowCount());
      // diff column
      colCount = 4;
      // create data array
      data = new Cell[rowCount][colCount];
      String leftVal, rightVal;
      int dataColIndex = 0;

      for(int i = 0; i < rowCount; i++) {
         dataColIndex = 0;

         for(int col = 0; col < colCount; col++) {
            if(i < left.getHeaderRowCount()) {
               if(col == colCount - 1) {
                  data[i][dataColIndex++]
                     = new Cell(diffColumnName, CellValueType.STRING);
               }
               else {
                  // diff column
                  int lc = col;

                  if(col == colCount - 2) {
                     lc = 1;
                  }

                  final Cell leftObject = left.getObject(i, lc);
                  Cell rightObject = right.getObject(i, 1);
                  final String lStr = LensTool.toString(leftObject.getValue());
                  final String rStr = LensTool.toString(rightObject.getValue());

                  if(ObjectUtils.nullSafeEquals(lStr, rStr)) {
                     rightObject = rightObject.clone();
                     rightObject.setValue(rStr + RIGHT_COLUMN_SUFFIX);
                     rightObject.setType(CellValueType.STRING);
                  }

                  data[i][dataColIndex++] = (col == colCount - 2)
                     ? rightObject : leftObject;
               }

               continue;
            }

            if(col == 0) {
               data[i][dataColIndex++] = i < left.getRowCount()
                  ? left.getObject(i, col) : right.getObject(i, col);
            }
            else if(col == 1) {
               data[i][dataColIndex++] = i < left.getRowCount()
                  ? left.getObject(i, col) : null;
            }
            else if(col == 2) {
               data[i][dataColIndex++] = i < right.getRowCount()
                  ? right.getObject(i, 1) : null;
            }
            else {
               leftVal = i < left.getRowCount()
                  ? LensTool.toString(left.getData(i, 1)) : null;
               rightVal = i < right.getRowCount()
                  ? LensTool.toString(right.getData(i, 1)) : null;

               double leftValue = NumberUtil.parseDoubleOrElse(leftVal, 0D);
               double rightValue = NumberUtil.parseDoubleOrElse(rightVal, 0D);

               data[i][dataColIndex++] = new Cell(
                  Tool.reserveDecimalPlace(leftValue - rightValue, point),
                  CellValueType.DOUBLE);
            }
         }
      }
   }
}
