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

package club.javafamily.lens.lens.annotation;

import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.core.TableLens;
import club.javafamily.lens.lens.processor.NullProcessor;

import java.lang.annotation.*;
import java.util.function.BiFunction;

/**
 * @author Jack Li
 * @date 2021/7/26 3:44 下午
 * @description
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TableLensColumn {

   String NULL_STR = "_NULL_";

   /**
    * get column value for object provider spel expression
    */
   String value() default "";

   Class<?> valueType() default String.class;

   String header() default NULL_STR;

   String localizedHeader() default NULL_STR;

   String arrayJoin() default NULL_STR;

   /**
    * table lens's column name
    */
   String relatedColName() default "";

   /**
    * related column processor
    * BiFunction&lt;lens, relatedValue, returnValue&gt;
    */
   Class<? extends BiFunction<TableLens, Cell, Cell>> relatedProcessor() default NullProcessor.class;

   /**
    * ignore field.
    */
   boolean ignore() default false;

   int order() default TABLE;

   int TABLE = -1;
}
