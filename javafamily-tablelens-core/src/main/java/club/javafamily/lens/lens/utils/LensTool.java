package club.javafamily.lens.lens.utils;

import club.javafamily.lens.lens.annotation.TableLensColumn;
import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.cell.CellValueType;
import club.javafamily.lens.lens.core.DefaultTableLens;
import club.javafamily.lens.lens.core.TableLens;
import club.javafamily.utils.DateUtil;
import club.javafamily.utils.ReflectUtils;
import club.javafamily.utils.Tool;
import club.javafamily.utils.exp.SpelUtils;
import club.javafamily.utils.functions.ThreeConsumer;
import club.javafamily.utils.spring.ObjectUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static club.javafamily.lens.lens.annotation.TableLensColumn.NULL_STR;

/**
 * @author Jack Li
 * @date 2021/7/26 3:44 下午
 * @description
 */
public class LensTool {

   /**
    * Default text font.
    */
   public static final Font DEFAULT_TEXT_FONT = new Font("Times New Roman", Font.PLAIN, 16);

   /**
    * Default header text font.
    */
   public static final Font DEFAULT_HEADER_FONT = new Font("Times New Roman", Font.BOLD, 18);

   public static final Color DEFAULT_TEXT_BG = Color.WHITE;

   public static final Color DEFAULT_HEADER_BG = Color.LIGHT_GRAY;

   public static final Font getDefaultHeaderFont() {
      return getFont(true);
   }

   public static final Font getDefaultTextFont() {
      return getFont(false);
   }

   private static final Font getFont(boolean bold) {
      Font font = bold ? DEFAULT_HEADER_FONT : DEFAULT_TEXT_FONT;

      return new Font(font.getName(),
         bold ? Font.BOLD : Font.PLAIN,
         font.getSize());
   }

   public static boolean isDataEmpty(TableLens lens) {
      return lens == null || lens.isEmptyData();
   }

   public static Field[] processTableLensColumn(Field[] fields) {
      List<Field> fieldList = Arrays.asList(fields);
      Iterator<Field> iterator = fieldList.iterator();
      Field field;

      while(iterator.hasNext()) {
         field = iterator.next();
         field.setAccessible(true);
         TableLensColumn column = field.getAnnotation(TableLensColumn.class);

         if(column == null) {
            continue;
         }

         if(column.ignore()) {
            iterator.remove();
         }
      }

      return fieldList.toArray(new Field[0]);
   }

   private static <T> Cell getFieldValue0(Object field, T targetObj) {
      if(field instanceof Field) {
         final Object fieldValue = getFieldValue((Field) field, targetObj);
         return new Cell(fieldValue);
      }

      final List<Field> fieldList = (List<Field>) field;

      Object[] array = new Object[fieldList.size()];
      String arrayJoin = null;

      for (int i = 0; i < fieldList.size(); i++) {
         final Field dupFiled = fieldList.get(i);
         array[i] = getFieldValue(dupFiled, targetObj);

         if(arrayJoin == null) {
            final TableLensColumn tableLensColumn
               = dupFiled.getDeclaredAnnotation(TableLensColumn.class);

            if(!NULL_STR.equals(tableLensColumn.arrayJoin())) {
               arrayJoin = tableLensColumn.arrayJoin();
            }
         }
      }

      return new Cell(array, CellValueType.ARRAY, arrayJoin);
   }

   public static <T> Object getFieldValue(Field field, T targetObj) {
      field.setAccessible(true);
      Object value = ReflectionUtils.getField(field, targetObj);
      TableLensColumn column = field.getAnnotation(TableLensColumn.class);

      if(value == null || column == null
         || StringUtils.isEmpty(column.value()))
      {
         return value;
      }

      String expression = column.value();
      Class<?> valueType = column.valueType();

//      if(valueType == Void.class) {
//         valueType = value.getClass();
//      }

      value = SpelUtils.calcValue(expression, valueType, targetObj, value);

      return value;
   }

   /**
    * convert obj to double.
    * @param object obj
    * @return double.
    * @DevWarning if convert error will throw exception
    */
   public static Double parseDouble(Object object) {
      return Double.parseDouble(LensTool.toString(object));
   }

   public static String toString(Cell cell) {
      if(cell == null || cell.getValue() == null) {
         return "";
      }

      Object data = cell.getValue();
      CellValueType type = cell.getType();

      switch(type) {
         case TIME:
            if(data instanceof Date) {
               Date time = (Date) data;
               return DateTimeFormatter.ofPattern(Tool.DEFAULT_TIME_FORMAT)
                  .format(LocalDateTime.ofInstant(time.toInstant(),
                     Tool.DEFAULT_TIME_ZONE.toZoneId()));
            }
            else {
               LocalTime localTime = (LocalTime) data;
               return DateTimeFormatter.ofPattern(Tool.DEFAULT_TIME_FORMAT)
                  .format(localTime);
            }

         case DATE:
            if(data instanceof Date) {
               Date date = (Date) data;

               return DateUtil.formatNormalDate(date);
            }
            else {
               LocalDate localDate = (LocalDate) data;
               return DateTimeFormatter.ofPattern(DateUtil.NORMAL_DATE_FORMAT)
                  .format(localDate);
            }

         case DATETIME:
            if(data instanceof Date) {
               Date datetime = (Date) data;
               return DateTimeFormatter.ofPattern(Tool.DEFAULT_DATETIME_FORMAT)
                  .format(LocalDateTime.ofInstant(datetime.toInstant(),
                     Tool.DEFAULT_TIME_ZONE.toZoneId()));
            }
            else {
               LocalDateTime localDateTime = (LocalDateTime) data;
               return DateTimeFormatter.ofPattern(Tool.DEFAULT_DATETIME_FORMAT)
                  .format(localDateTime);
            }

         case ARRAY:
            final String arrayJoin = cell.getArrayJoin();
            final Object[] array = (Object[]) data;

            return Arrays.stream(array)
               .map(LensTool::toString)
               .collect(Collectors.joining(arrayJoin));
         default:
            return ObjectUtils.nullSafeToString(data);
      }
   }

   public static String toString(Object obj) {
      return toString(obj, null);
   }

   public static String toString(Object obj, String defaultValue) {
      if(obj instanceof Date) {
         return DateUtil.formatDateTime((Date) obj);
      }

      return obj == null ? defaultValue
         : ObjectUtils.nullSafeToString(obj);
   }

   public static <T> void iterator(TableLens lens,
                                   ThreeConsumer<Cell, Integer, Integer> cellConsumer,
                                   BiConsumer<List<Cell>, Integer> rowConsumer)
   {
      for (int i = 0; i < lens.getRowCount(); i++) {
         if (cellConsumer != null) {
            for (int j = 0; j < lens.getColCount(); j++) {
               Cell obj = lens.getObject(i, j);
               cellConsumer.accept(obj, i, j);
            }
         }

         if (rowConsumer != null) {
            rowConsumer.accept(Arrays.asList(lens.getRow(i)), i);
         }
      }
   }

   public static void print(TableLens lens) {
      write(lens, System.out);
   }

   public static void write(TableLens lens, OutputStream out) {
      if(lens == null) {
         throw new NullPointerException("Lens is null.");
      }

      try(BufferedOutputStream bos = new BufferedOutputStream(out)) {
         LensTool.iterator(lens,
            (obj, i, j) -> {
               try {
                  bos.write((ObjectUtils.nullSafeToString(obj) + "  |\t").getBytes());
               } catch (IOException e) {
                  e.printStackTrace();
               }
            },
            (row, i) -> {
               try {
                  bos.write("\n----------------------------------------------\n".getBytes());
               } catch (IOException e) {
                  e.printStackTrace();
               }
            });
      }
      catch (Exception e){
         e.printStackTrace();
      }
   }

   public static <T> JSONObject toJsonTable(TableLens lens) {
      JSONObject table = new JSONObject();
      List<JSONObject> data = new ArrayList<>();
      JSONObject row;

      for (int i = lens.getHeaderRowCount(); i < lens.getRowCount(); i++) {
         row = new JSONObject();

         for (int j = 0; j < lens.getColCount(); j++) {
            row.put(LensTool.toString(lens.getObject(0, j)),
               LensTool.toString(lens.getObject(i, j)));
         }

         data.add(row);
      }

      table.put("rows", data);
      table.put("dataCount", lens.getDataRowCount());

      return table;
   }

   public static List<?> getLensColumnFields(Class clazz) {
      List<Field> fields = new ArrayList<>();

      ReflectionUtils.doWithFields(clazz, fields::add, (
         field) -> field.getDeclaredAnnotation(TableLensColumn.class) != null);

      if(fields.size() < 1) {
         return Arrays.stream(clazz.getDeclaredFields())
            // exclude final field
            .filter(field -> !Modifier.isFinal(field.getModifiers()))
            .collect(Collectors.toList());
      }

      fields = fields.stream()
         .filter(field -> !field.getDeclaredAnnotation(TableLensColumn.class)
            .ignore())
         .collect(Collectors.toList());

      // sort by order
      fields.sort(Comparator.comparingInt(
         field -> field.getDeclaredAnnotation(TableLensColumn.class).order()));

      List result = new ArrayList<>();
      Integer order = null;

      // make sure fields has sorted by order.
      for (Field field : fields) {
         final TableLensColumn column
            = field.getDeclaredAnnotation(TableLensColumn.class);

         if(order == null || order != column.order()) {
            order = column.order();
            result.add(field);
            continue;
         }

         // duplicate
         int lastIndex = result.size() - 1;
         final Object dupOrder = result.get(lastIndex);

         if(dupOrder instanceof Field) {
            final ArrayList<Object> dupCells = new ArrayList<>();
            dupCells.add(dupOrder);
            dupCells.add(field);
            result.set(lastIndex, dupCells);
         }
         else if(dupOrder instanceof List) {
            ((List) dupOrder).add(field);
         }
         else {
            LOGGER.error("Unexpected exception: Unknown field type.");
         }
      }

      return result;
   }

   public static <T> TableLens createTableLensByAllData(List<T> data,
                                                        Class<T> clazz)
   {
      return createTableLensByAllData(data, clazz, false);
   }

   public static <T> TableLens createTableLensByAllData(List<T> data,
                                                        Class<T> clazz,
                                                        boolean localizedHeader)
   {
      List<?> columnFields = getLensColumnFields(clazz);

      return createTableLensByAllData(data, clazz, columnFields, localizedHeader);
   }

   public static <T> TableLens createTableLensByAllData(List<T> data,
                                                        Class<T> clazz,
                                                        List<?> columnFields,
                                                        boolean localizedHeader)
   {
      DefaultTableLens lens = new DefaultTableLens();

      // 1. get field by order
//      List<?> columnFields = getLensColumnFields(clazz);

      // 2. build tablelens data
      lens.setRowCount(1 + data.size());
      lens.setColCount(columnFields.size());
      lens.reset();

      // 3. fill header by order
      for(int headerIndex = 0; headerIndex < lens.getHeaderRowCount(); headerIndex++) {
         for(int colIndex = 0; colIndex < columnFields.size(); colIndex++) {
            Object field = columnFields.get(colIndex);
            String header = getHeaderFromField(field, localizedHeader);

            // header is field name
            lens.setObject(headerIndex, colIndex,
               new Cell(header, CellValueType.STRING));
         }
      }

      int rowIndex = lens.getHeaderRowCount();

      // 4. fill data
      for(int i = 0; i < data.size(); i++, rowIndex++) {
         T row = data.get(i);

         for(int colIndex = 0; colIndex < columnFields.size(); colIndex++) {
            Object field = columnFields.get(colIndex);
//            Object value = ReflectionUtils.getField(field, row);
            final Cell cell = getFieldValue0(field, row);
            lens.setObject(rowIndex, colIndex, cell);
         }
      }

      return lens;
   }

   private static String getHeaderFromField(Object field, boolean localizedHeader) {
      return getHeaderFromField(field, localizedHeader
         ? TableLensColumn::localizedHeader
         : TableLensColumn::header);
   }

   public static String getHeaderFromField(
      Object field, Function<TableLensColumn, String> getHeader)
   {
      if(field instanceof Field) {
         final Field fieldType = (Field) field;
         final TableLensColumn column
            = fieldType.getDeclaredAnnotation(TableLensColumn.class);

         return column != null && !NULL_STR.equals(getHeader.apply(column))
            ? getHeader.apply(column)
            : fieldType.getName();
      }

      if(!(field instanceof List)) {
         throw new ClassCastException("Field type error.");
      }

      final List<Field> fieldList = (List<Field>) field;

      String header = fieldList.stream()
         .map(f -> f.getDeclaredAnnotation(TableLensColumn.class))
         .map(f -> f != null ? getHeader.apply(f) : null)
         .filter(f -> f != null && !NULL_STR.equals(f))
         .findFirst()
         .orElse(null);

      if(header == null) {
         throw new NullPointerException("Missing header setting for duplicate cell.");
      }

      return header;
   }

   public static <R> List<R> convertLensToData(TableLens lens,
                                               Class<R> clazz,
                                               boolean localized)
      throws Exception
   {
      if(LensTool.isDataEmpty(lens)) {
         return new ArrayList<>();
      }

      List<Field> fields = ReflectUtils.getAnnotationColumnFields(clazz,
         TableLensColumn.class);
      Map<Integer, Field> map = new HashMap<>(fields.size());

      for (int i = 0; i < lens.getColCount(); i++) {
         final Object header = lens.getData(0, i);

         if(!(header instanceof String)) {
            LOGGER.warn("Header is not is string for {}", header);
            continue;
         }

         Field field = findFieldByHeader(fields, (String) header, localized);

         if(field != null) {
            map.put(i, field);
         }
         else {
            LOGGER.warn("NOT found header field for {}", header);
         }
      }

      List<R> result = new ArrayList<>();

      for (int r = lens.getHeaderRowCount(); r < lens.getRowCount(); r++) {
         final R instance = clazz.newInstance();

         for (int c = 0; c < lens.getColCount(); c++) {
            final Object data = lens.getData(r, c);
            final Field field = map.get(c);

            fillInstanceData(instance, field, data);
         }

         result.add(instance);
      }

      return result;
   }

   private static <R> void fillInstanceData(R instance,
                                            Field field,
                                            Object data)
   {
      ReflectUtils.setValue(field, instance, LensTool.toString(data));
   }

   private static Field findFieldByHeader(List<Field> fields,
                                          String header,
                                          Boolean localized)
   {
      for (Field field : fields) {
         final TableLensColumn lensColumn
            = field.getAnnotation(TableLensColumn.class);

         if(lensColumn == null) {
            LOGGER.warn("TableLensColumn not found for {} and field {}",
               header, field);
            continue;
         }

         final String customHeader = lensColumn.header();
         final String localizedHeader = lensColumn.localizedHeader();

         if(!localized && (ObjectUtils.nullSafeEquals(customHeader, header)
            || ObjectUtils.isEmpty(customHeader) && field.getName().equals(header)))
         {
            return field;
         }

         if(localized && ObjectUtils.nullSafeEquals(localizedHeader, header)) {
            return field;
         }

         if(field.getName().equals(header)) {
            return field;
         }
      }

      return null;
   }

   /**
    * Using {@link #convertLensToData}
    */
   @Deprecated
   public static <R> List<R> convertLensToPojo(TableLens lens,
                                               Class<R> clazz)
      throws Exception
   {
      List<Field> fields = ReflectUtils.getAnnotationColumnFields(clazz,
         TableLensColumn.class);
//      List<Field> fields = getLensColumnFields(clazz);
      List<R> data = new ArrayList<>();

      for (int r = lens.getHeaderRowCount(); r < lens.getRowCount(); r++) {
         R obj = clazz.newInstance();
         final Cell[] row = lens.getRow(r);

         if(row == null || row.length == 0) {
            LOGGER.error("Getting row is null. lens is: {}, row count: {}, real row index: {}",
               lens.getClass(), lens.getRowCount(), lens.getRealRowIndex(r));
            LensTool.print(lens);
         }

         fillObject(lens, fields, Arrays.asList(row), obj);

         data.add(obj);
      }

      return data;
   }

   private static <R> void fillObject(TableLens lens, List<Field> fields,
                                      List<Cell> row, R obj)
      throws InstantiationException, IllegalAccessException
   {
      for(Field field : fields) {
         TableLensColumn tableColumn
            = field.getDeclaredAnnotation(TableLensColumn.class);

         if(tableColumn == null) {
            continue;
         }

         String column = tableColumn.value();
         String relatedColName = tableColumn.relatedColName();
         Class<? extends BiFunction<TableLens, Cell, Cell>>
            processor = tableColumn.relatedProcessor();
         Cell value = null;

         if(StringUtils.hasText(column)) {
            Integer columnIndex = lens.getColumnIndex(column);

            if(columnIndex == null) {
               final Cell[] row1 = lens.getRow(0);
               LOGGER.error("find column index is null. row: {}, column: {}",
                  Arrays.asList(row1), column);
            }

            try {
               value = row.get(columnIndex);
            }
            catch (Exception e) {
               LOGGER.error("row: {}, index: {}, column: {}",
                  row, columnIndex, column);
               throw e;
            }
         }
         else if(StringUtils.hasText(relatedColName)) {
            Integer relatedIndex = lens.getColumnIndex(relatedColName);
            Cell relatedValue = row.get(relatedIndex);
            value = processor.newInstance().apply(lens, relatedValue);
         }

         ReflectUtils.setValue(field, obj, makeInvalidNumberToNull(value));
      }
   }

   public static String makeInvalidNumberToNull(Cell value) {
      final String v = LensTool.toString(value);

      return Tool.makeInvalidNumberToNull(v);
   }

   public static TableLens fillTableLensByResultSet(
      String[] title, List<Map<String, Object>> data)
   {
      DefaultTableLens lens
         = new DefaultTableLens(data.size() + 1, title.length);

      // fill title
      for (int col = 0; col < title.length; col++) {
         lens.setObject(0, col, new Cell(title[col]));
      }

      // fill data
      int row = 1;

      for (Map<String, Object> rowData : data) {
         for (int col = 0; col < title.length; col++) {
            lens.setObject(row, col,
               new Cell(LensTool.toString(rowData.get(title[col]))));
         }

         row++;
      }

      return lens;
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(LensTool.class);
}
