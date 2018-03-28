package org.kolokolov.testtask.queryparser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.kolokolov.testtask.error.SyntaxError;
import org.kolokolov.testtask.error.UnsupportedQueryTypeException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SqlQueryParser {

    public Select parseSqlQuery(String sqlQuery) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sqlQuery);
            if (statement instanceof Select) {
                return (Select) statement;
            } else {
                throw new UnsupportedQueryTypeException("Only SELECT queries are supported");
            }
        } catch (JSQLParserException e) {
            throw new SyntaxError(e.getCause().getMessage());
        }
    }

    public List<String> getTableNames(Select selectQuery) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        return tablesNamesFinder.getTableList(selectQuery);
    }

    private List<SelectItem> getSelectItems(Select selectQuery) {
        PlainSelect plainSelect = fetchPlainSelectFromSelect(selectQuery);
        return plainSelect.getSelectItems();
    }

    public Expression getWhereExpression(Select selectQuery) {
        PlainSelect plainSelect = fetchPlainSelectFromSelect(selectQuery);
        return plainSelect.getWhere();
    }

    private PlainSelect fetchPlainSelectFromSelect(Select selectQuery) {
        SelectBody selectBody = selectQuery.getSelectBody();
        if (selectBody instanceof PlainSelect) {
            return (PlainSelect) selectBody;
        } else {
            throw new UnsupportedQueryTypeException("Only plain selects are supported");
        }
    }

    public List<String> getFieldsList(Select selectQuery) {
        List<SelectItem> selectItems = getSelectItems(selectQuery);
        List<String> selectedFields = new ArrayList<>();
        for (SelectItem item: selectItems) {
            if (item instanceof SelectExpressionItem) {
                selectedFields.add(((SelectExpressionItem) item).getExpression().toString());
            } else if (item instanceof AllTableColumns) {
                selectedFields.add(((AllTableColumns) item).getTable().toString() + ".*");
            } else {
                selectedFields = Collections.emptyList();
                break;
            }
        }
        return selectedFields;
    }

    public Limit getLimit(Select selectQuery) {
        PlainSelect plainSelect = fetchPlainSelectFromSelect(selectQuery);
        return plainSelect.getLimit();
    }
}
