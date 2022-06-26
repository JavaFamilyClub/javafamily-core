package club.javafamily.lens.lens.cell;

import club.javafamily.lens.lens.utils.CellValueTypeUtils;
import club.javafamily.lens.lens.utils.LensTool;
import club.javafamily.utils.Tool;
import lombok.Data;

/**
 * @author Jack Li
 * @date 2021/7/26 3:22 下午
 * @description
 */
@Data
public class Cell implements Cloneable {
   private Object value;
   private CellValueType type = CellValueType.STRING;
   private String arrayJoin;

   public Cell() {
   }

   public static Cell buildCell(Object obj) {
      if(obj instanceof Cell) {
         return (Cell) obj;
      }

      return new Cell(obj);
   }

   public Cell(Object value) {
      this(value, CellValueTypeUtils.getCellType(value));
   }

   public Cell(Object value, CellValueType type) {
      this.value = value;
      this.type = type;
   }

   public Cell(Object value, CellValueType type, String arrayJoin) {
      this.value = value;
      this.type = type;
      this.arrayJoin = arrayJoin;
   }

   @Override
   public String toString() {
      return LensTool.toString(this);
   }

   @Override
   public Cell clone() {
      final Cell clone = new Cell();

      clone.type = this.type;
      clone.value = Tool.clone(value);

      return clone;
   }
}
