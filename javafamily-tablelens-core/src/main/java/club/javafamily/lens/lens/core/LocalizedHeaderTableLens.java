package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.cell.CellValueType;
import club.javafamily.lens.lens.utils.LensTool;

import java.util.Map;

/**
 * @author Jack Li
 * @date 2021/7/27 11:15 上午
 * @description 本地化 Table Header 的 {@link TableLens} 抽象类实现。
 *    使用者需要自己实现 Header --> Localized 映射的 Map， 通过
 *    {@link LocalizedHeaderTableLens#getLocalizedMap } 方法
 */
public abstract class LocalizedHeaderTableLens extends DefaultTableLens {

   public LocalizedHeaderTableLens(TableLens table) {
      super(table);
   }

   /**
    * Getting localized map
    * @return Config Localized Map
    */
   public abstract Map<String, String> getLocalizedMap();

   @Override
   public Cell getObject(int row, int col) {
      final Cell cell = super.getObject(row, col);

      if(cell == null) {
         return null;
      }

      final Map<String, String> localizedMap = getLocalizedMap();
      boolean isHeader = row < getHeaderRowCount();
      final boolean isString = cell.getValue() instanceof String
         || cell.getType() == CellValueType.STRING;

      if(isHeader && isString
         && localizedMap.containsKey(LensTool.toString(cell.getValue())))
      {
         return new Cell(localizedMap.get(LensTool.toString(cell.getValue())),
            CellValueType.STRING);
      }

      return cell;
   }
}
