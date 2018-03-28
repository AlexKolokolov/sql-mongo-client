package org.kolokolov.testtask.querybuilder;

import com.mongodb.client.model.Filters;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Service
@Scope(SCOPE_PROTOTYPE)
public class FilterCreator extends ExpressionVisitorAdapter {

    private Bson filter;

    private String value;

    private FilterCreator leftFilterCreator;

    private FilterCreator rightFilterCreator;

    private ApplicationContext context;

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public Bson getFilter() {
        return filter;
    }

    private String getValue() {
        return value;
    }

    @Override
    public void visit(AndExpression expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.and(leftFilterCreator.getFilter(), rightFilterCreator.getFilter());
    }

    @Override
    public void visit(OrExpression expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.or(leftFilterCreator.getFilter(), rightFilterCreator.getFilter());
    }

    @Override
    public void visit(StringValue value) {
        this.value = value.getValue();
    }

    @Override
    public void visit(Column column) {
        this.value = column.getColumnName();
    }

    @Override
    public void visit(LongValue value) {
        this.value = value.getStringValue();
    }

    @Override
    public void visit(DoubleValue value) {
        this.value = value.toString();
    }

    @Override
    public void visit(DateValue value) {
        this.value = value.toString();
    }

    @Override
    public void visit(TimestampValue value) {
        this.value = value.toString();
    }

    @Override
    public void visit(TimeValue value) {
        this.value = value.toString();
    }

    @Override
    public void visit(EqualsTo expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.eq(leftFilterCreator.getValue(), rightFilterCreator.getValue());
    }

    @Override
    public void visit(NotEqualsTo expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.ne(leftFilterCreator.getValue(), rightFilterCreator.getValue());
    }

    @Override
    public void visit(GreaterThan expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.gt(leftFilterCreator.getValue(), rightFilterCreator.getValue());
    }

    @Override
    public void visit(GreaterThanEquals expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.gte(leftFilterCreator.getValue(), rightFilterCreator.getValue());
    }

    @Override
    public void visit(MinorThan expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.lt(leftFilterCreator.getValue(), rightFilterCreator.getValue());
    }

    @Override
    public void visit(MinorThanEquals expr) {
        setFilterCreatorsToSubExpressions(expr);
        filter = Filters.lte(leftFilterCreator.getValue(), rightFilterCreator.getValue());
    }

    private void initializeSubFilterCreators() {
        leftFilterCreator = context.getBean(FilterCreator.class);
        rightFilterCreator = context.getBean(FilterCreator.class);
    }

    private void setFilterCreatorsToSubExpressions(BinaryExpression expr) {
        initializeSubFilterCreators();
        expr.getLeftExpression().accept(leftFilterCreator);
        expr.getRightExpression().accept(rightFilterCreator);
    }
}
