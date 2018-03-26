package org.kolokolov.testtask.querybuilder;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.kolokolov.testtask.parser.SqlQueryParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoQueryBuilder {

    private SqlQueryParser sqlQueryParser;

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
        return Filters.eq(expression.getLeftExpression().toString(), expression.getRightExpression().toString());
    }

    private Bson createNotEqualsToFilter(NotEqualsTo expression) {
        return Filters.ne(expression.getLeftExpression().toString(), expression.getRightExpression().toString());
    }

    private Bson createGreaterThanFilter(GreaterThan expression) {
        return Filters.gt(expression.getLeftExpression().toString(), expression.getRightExpression().toString());
    }

    private Bson createGreaterThanEqualsFilter(GreaterThanEquals expression) {
        return Filters.gte(expression.getLeftExpression().toString(), expression.getRightExpression().toString());
    }

    private Bson createLessThanFilter(MinorThan expression) {
        return Filters.lt(expression.getLeftExpression().toString(), expression.getRightExpression().toString());
    }

    private Bson createLessThanEqualsFilter(MinorThanEquals expression) {
        return Filters.lte(expression.getLeftExpression().toString(), expression.getRightExpression().toString());
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
}
