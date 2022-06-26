package club.javafamily.lens.lens.processor;

import club.javafamily.lens.lens.cell.Cell;
import club.javafamily.lens.lens.core.TableLens;

import java.util.function.BiFunction;

public class NullProcessor implements BiFunction<TableLens, Cell, Cell> {

    @Override
    public Cell apply(TableLens stringTableLens, Cell relatedValue) {
        return null;
    }

}
