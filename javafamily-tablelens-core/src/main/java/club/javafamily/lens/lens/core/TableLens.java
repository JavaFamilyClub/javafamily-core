/*
 * Copyright (c) 2020, JavaFamily Technology Corp, All Rights Reserved.
 *
 * The software and information contained herein are copyrighted and
 * proprietary to JavaFamily Technology Corp. This software is furnished
 * pursuant to a written license agreement and may be used, copied,
 * transmitted, and stored only in accordance with the terms of such
 * license and with the inclusion of the above copyright notice. Please
 * refer to the file "COPYRIGHT" for further copyright and licensing
 * information. This software and information or any other copies
 * thereof may not be provided or otherwise made available to any other
 * person.
 */

package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.utils.LensTool;
import club.javafamily.utils.spring.ObjectUtils;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Jack Li
 * @date 2021/7/26 3:42 下午
 * @description TableLens interface
 */
public interface TableLens {

   /**
    * get cell
    * @param row row index
    * @param col col index
    * @return Cell
    */
   Cell getObject(int row, int col);

   /**
    * get cell data value.
    * @param row row index
    * @param col col index
    * @return cell data value
    */
   default Object getData(int row, int col) {
      final Cell cell = getObject(row, col);

      return cell != null ? cell.getValue() : null;
   }

   /**
    * include header row count
    * @return row count
    */
   int getRowCount();

   /**
    * include header col count
    */
   int getColCount();

   /**
    * Getting data row count.
    */
   default int getDataRowCount() {
      return getRowCount() - getHeaderRowCount();
   }

   /**
    * Check is empty data core.
    */
   default boolean isEmptyData() {
      return getDataRowCount() < 1;
   }

   /**
    * decoration tableLens.
    */
   default TableLens getTable() {
      return null;
   }

   /**
    * for append table lens... skip row header.
    * @param row table display row index
    * @return real row index
    */
   default int getRealRowIndex(int row) {
      return row;
   }

   /**
    * for composite lens... skip col header
    * @param col table display col index
    * @return real col index
    */
   default int getRealColIndex(int col) {
      return col;
   }

   Cell[] getRow(int row);

   /**
    * Check is empty core.
    */
   default boolean isEmpty() {
      return getRowCount() < 1;
   }

   default int getHeaderRowCount() {
      return 1;
   }

   default int getHeaderColCount() {
      return 1;
   }

   default int getColWidth(int col) {
      // default char count;
      int max = 20;
      // max check 10 rows
      int checkLength = Math.min(getRowCount(), 10);

      for(int i = 0; i < checkLength; i++) {
         String value = LensTool.toString(getObject(i, col));

         if(value.length() > max) {
            max = value.length();
         }
      }

      return max;
   }

   default boolean isRowHeader(int row) {
      return row < getHeaderRowCount();
   }

   default boolean isColHeader(int col) {
      return col < getHeaderColCount();
   }

   default boolean isHeader(int row, int col) {
      return isRowHeader(row) || isColHeader(col);
   }

   /**
    * 获取指定 Cell 的字体
    * @param row row index
    * @param col col index
    * @return Font
    */
   default Font getFont(int row, int col) {
      if(isHeader(row, col)) {
         return LensTool.DEFAULT_HEADER_FONT;
      }

      return LensTool.DEFAULT_TEXT_FONT;
   }

   /**
    * getting Cell's font color
    * @param row row index
    * @param col col index
    * @return color
    */
   default Color getFontColor(int row, int col) {
      if(isHeader(row, col)) {
         return Color.orange;
      }

      return Color.BLACK;
   }

   /**
    * getting Cell's Background
    * @param row row index
    * @param col col index
    * @return Background Color
    */
   default Color getBackground(int row, int col) {
      if(isHeader(row, col)) {
         return LensTool.DEFAULT_HEADER_BG;
      }

      return LensTool.DEFAULT_TEXT_BG;
   }

   /**
    * getting cell's span
    * @param row row index
    * @param col col index
    * @return span dimension
    */
   default Dimension getSpan(int row, int col) {
      return null;
   }

   default Integer getColumnIndex(String columnName) {
      if(getRowCount() < 1 || getColCount() < 1 || ObjectUtils.isEmpty(columnName)) {
         return null;
      }

      for(int row = 0; row < getHeaderRowCount(); row++) {
         for(int col = 0; col < getColCount(); col++) {
            Cell cell = getObject(row, col);

            if(cell != null && columnName.equals(cell.getValue())) {
               return col;
            }
         }
      }

      return null;
   }

   /**
    * Getting col data.
    */
   default java.util.List<Cell> getColData(int colIndex) {
      if(colIndex < 0 || colIndex >= getColCount()) {
         return null;
      }

      java.util.List<Cell> data = new ArrayList<>();

      for(int i = getHeaderRowCount(); i < getRowCount(); i++) {
         data.add(getObject(i, colIndex));
      }

      return data;
   }

}
