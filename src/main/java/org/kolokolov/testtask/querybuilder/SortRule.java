package org.kolokolov.testtask.querybuilder;

public class SortRule {

    private final String field;
    private final boolean ascend;

    public SortRule(String field, boolean ascend) {
        this.field = field;
        this.ascend = ascend;
    }

    public String getField() {
        return field;
    }

    public int ascend() {
        return ascend ? 1 : -1;
    }
}
