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

import club.javafamily.lens.lens.format.FormatModel;

/**
 * @author Jack Li
 * @date 2021/7/26 3:46 下午
 * @description The basic {@link TableLens} interface that can been format.
 */
public interface FormatableTableLens extends TableLens {
   /**
    * Getting format model.
    * @param row row index
    * @param col col index
    * @return FormatModel
    */
   FormatModel getCellFormat(int row, int col);

   /**
    * Setting format model.
    * @param row row index
    * @param col col index
    * @param formatModel FormatModel
    */
   void setCellFormat(int row, int col, FormatModel formatModel);

   /**
    * Get Row Format
    * @param row row index
    * @return {@link FormatModel}
    */
   FormatModel getRowFormat(int row);
   /**
    * Set Row Format
    * @param row row index
    * @param formatModel {@link FormatModel}
    */
   void setRowFormat(int row, FormatModel formatModel);

   /**
    * Get col Format
    * @param col col index
    * @return {@link FormatModel}
    */
   FormatModel getColFormat(int col);
   /**
    * Set col Format
    * @param col col index
    * @param formatModel {@link FormatModel}
    */
   void setColFormat(int col, FormatModel formatModel);
}
