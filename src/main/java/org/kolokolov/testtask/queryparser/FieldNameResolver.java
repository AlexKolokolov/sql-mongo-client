package org.kolokolov.testtask.queryparser;

import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import org.springframework.stereotype.Service;

@Service
public class FieldNameResolver extends SelectItemVisitorAdapter {

    private String field;

    public String getField() {
        return field;
    }

    @Override
    public void visit(AllColumns columns) {
        field = null;
    }

    @Override
    public void visit(AllTableColumns columns) {
        field = columns.getTable().getName();
    }

    @Override
    public void visit(SelectExpressionItem item) {
        field = item.getExpression().toString();
    }
}
