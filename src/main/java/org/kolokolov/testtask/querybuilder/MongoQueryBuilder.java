package org.kolokolov.testtask.querybuilder;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.statement.select.Limit;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoQueryBuilder {

    public void addFilter(FindIterable<Document> query, Expression expression) {
        if (expression != null) {
            query.filter(createFilter(expression));
        }
    }

    Bson createFilter(Expression expression) {
        if (expression instanceof AndExpression) {
            return createAndFilter((AndExpression) expression);
        } else if (expression instanceof OrExpression) {
            return createOrFilter((OrExpression) expression);
        } else if (expression instanceof EqualsTo) {
            return createEqualsToFilter((EqualsTo) expression);
        } else if (expression instanceof NotEqualsTo) {
            return createNotEqualsToFilter((NotEqualsTo) expression);
        } else if (expression instanceof GreaterThan) {
            return createGreaterThanFilter((GreaterThan) expression);
        } else if (expression instanceof GreaterThanEquals) {
            return createGreaterThanEqualsFilter((GreaterThanEquals) expression);
        } else if (expression instanceof MinorThan) {
            return createLessThanFilter((MinorThan) expression);
        } else if (expression instanceof MinorThanEquals) {
            return createLessThanEqualsFilter((MinorThanEquals) expression);
        } else return null;
    }

    private Bson createOrFilter(OrExpression orExpression) {
        return Filters.or(createFilter(orExpression.getLeftExpression()), createFilter(orExpression.getRightExpression()));
    }

    private Bson createAndFilter(AndExpression andExpression) {
        return Filters.and(createFilter(andExpression.getLeftExpression()), createFilter(andExpression.getRightExpression()));
    }

    private Bson createEqualsToFilter(EqualsTo expression) {
        return Filters.eq(expression.getLeftExpression().toString(), toString(expression.getRightExpression()));
    }

    private Bson createNotEqualsToFilter(NotEqualsTo expression) {
        return Filters.ne(expression.getLeftExpression().toString(), toString(expression.getRightExpression()));
    }

    private Bson createGreaterThanFilter(GreaterThan expression) {
        return Filters.gt(expression.getLeftExpression().toString(), toString(expression.getRightExpression()));
    }

    private Bson createGreaterThanEqualsFilter(GreaterThanEquals expression) {
        return Filters.gte(expression.getLeftExpression().toString(), toString(expression.getRightExpression()));
    }

    private Bson createLessThanFilter(MinorThan expression) {
        return Filters.lt(expression.getLeftExpression().toString(), toString(expression.getRightExpression()));
    }

    private Bson createLessThanEqualsFilter(MinorThanEquals expression) {
        return Filters.lte(expression.getLeftExpression().toString(), toString(expression.getRightExpression()));
    }

    private String toString(Expression expression) {
        if (expression instanceof StringValue) {
            return ((StringValue) expression).getValue();
        } else {
            return expression.toString();
        }
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
}
