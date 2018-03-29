package org.kolokolov.testtask.querybuilder;

import com.mongodb.client.FindIterable;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoQueryBuilder {

    private final FilterCreator filterCreator;

    public MongoQueryBuilder(FilterCreator filterCreator) {
        this.filterCreator = filterCreator;
    }

    public void addFilter(FindIterable<Document> query, Expression expression) {
        if (expression != null) {
            query.filter(createFilter(expression));
        }
    }

    Bson createFilter(Expression expression) {
        expression.accept(filterCreator);
        return filterCreator.getFilter();
    }

    public void addProjection(FindIterable<Document> query, List<String> selectedFields) {
        if (selectedFields != null && !selectedFields.isEmpty()) {
            query.projection(createProjectionOfFields(selectedFields));
        }
    }

    Document createProjectionOfFields(List<String> fields) {
        Document fieldFilter = new Document("_id", false);
        fields.forEach(field -> fieldFilter.append(field, true));
        return fieldFilter;
    }

    public void addLimit(FindIterable<Document> query, Limit limit) {
        if (limit != null) {
            query.limit((int) limit.getRowCount());
            query.skip((int) limit.getOffset());
        }
    }

    public void addSort(FindIterable<Document> query, List<OrderByElement> orderByElements) {
        if (orderByElements != null) {
            Document sort = new Document();
            orderByElements.forEach(e -> sort.append(e.getExpression().toString(), e.isAsc() ? 1 : -1));
            query.sort(sort);
        }
    }
}
