package club.javafamily.lens.lens.core;

import club.javafamily.lens.lens.cell.Cell;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Jack Li
 * @date 2021/7/26 3:51 下午
 * @description exclude cols filter
 */
public class ExcludeColumnFilter extends DefaultTableLens {

   public ExcludeColumnFilter(TableLens lens,
                              String... excludeColNames)
   {
      this(lens, Arrays.stream(excludeColNames)
         .map(lens::getColumnIndex)
         .filter(Objects::nonNull)
         .collect(Collectors.toList()));
   }

   public ExcludeColumnFilter(TableLens lens,
                              List<Integer> excludeColIndexes)
   {
      rowCount = lens.getRowCount();
      colCount = lens.getColCount() - excludeColIndexes.size();

      if(rowCount < 0 || colCount < 0
         || CollectionUtils.isEmpty(excludeColIndexes))
      {
         this.table = lens;
         return;
      }

      data = new Cell[rowCount][colCount];
      int col;

      for(int row = 0; row < rowCount; row++) {
         col = 0;

         for (int oldCol = 0; oldCol < lens.getColCount(); oldCol++) {
            if(excludeColIndexes.contains(oldCol)) {
               continue;
            }

            data[row][col++] = Cell.buildCell(lens.getObject(row, oldCol));
         }
      }
   }
}
