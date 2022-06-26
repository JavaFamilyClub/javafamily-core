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

package club.javafamily.lens.lens.utils;

import club.javafamily.lens.lens.cell.CellValueType;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public final class CellValueTypeUtils {
   public static CellValueType getCellType(Object obj) {
      if(obj instanceof Integer) {
         return CellValueType.INTEGER;
      }
      if(obj instanceof Long) {
         return CellValueType.LONG;
      }
      else if(obj instanceof Double) {
         return CellValueType.DOUBLE;
      }
      else if(obj instanceof Float) {
         return CellValueType.FLOAT;
      }
      else if(obj instanceof LocalDate) {
         return CellValueType.DATE;
      }
      else if(obj instanceof LocalTime) {
         return CellValueType.TIME;
      }
      else if(obj instanceof LocalDateTime) {
         return CellValueType.DATETIME;
      }
      else if(obj instanceof Date) {
         return CellValueType.DATETIME;
      }
      else if(obj instanceof Array) {
         return CellValueType.ARRAY;
      }
      else {
         return CellValueType.STRING;
      }
   }

   public static boolean isNumber(CellValueType type) {
      return type == CellValueType.INTEGER || type == CellValueType.LONG
         || type == CellValueType.DOUBLE || type == CellValueType.FLOAT;
   }
}
