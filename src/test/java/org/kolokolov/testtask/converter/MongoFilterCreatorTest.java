package org.kolokolov.testtask.converter;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.bson.BsonDateTime;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kolokolov.testtask.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class MongoFilterCreatorTest {

    private final static Column TEST_COLUMN = new Column(new Table("t"), "a");
    private final static LongValue TEST_LONG_VALUE = new LongValue("0");
    private final static StringValue TEST_STRING_VALUE = new StringValue("'b'");
    private final static TimeValue TEST_TIME_VALUE = new TimeValue("'12:00:00'");
    private final static String BSON_TIME = new BsonDateTime(TEST_TIME_VALUE.getValue().getTime()).toString();

    @Autowired
    private FilterCreator filterCreator;

    private EqualsTo createMockEqualsToStringExpression() {
        EqualsTo expression = mock(EqualsTo.class);
        when(expression.getLeftExpression()).thenReturn(TEST_COLUMN);
        when(expression.getRightExpression()).thenReturn(TEST_STRING_VALUE);
        doCallRealMethod().when(expression).accept(any());
        return expression;
    }

    private GreaterThan createMockGreaterThanLongExpression() {
        GreaterThan expression = mock(GreaterThan.class);
        when(expression.getLeftExpression()).thenReturn(TEST_COLUMN);
        when(expression.getRightExpression()).thenReturn(TEST_LONG_VALUE);
        doCallRealMethod().when(expression).accept(any());
        return expression;
    }

    private MinorThan createMockLessThanTimeExpression() {
        MinorThan expression = mock(MinorThan.class);
        when(expression.getLeftExpression()).thenReturn(TEST_COLUMN);
        when(expression.getRightExpression()).thenReturn(TEST_TIME_VALUE);
        doCallRealMethod().when(expression).accept(any());
        return expression;
    }

    private OrExpression createMockOrExpression(Expression left, Expression right) {
        OrExpression expression = mock(OrExpression.class);
        when(expression.getLeftExpression()).thenReturn(left);
        when(expression.getRightExpression()).thenReturn(right);
        doCallRealMethod().when(expression).accept(any());
        return expression;
    }

    private AndExpression createMockAndExpression(Expression left, Expression right) {
        AndExpression expression = mock(AndExpression.class);
        when(expression.getLeftExpression()).thenReturn(left);
        when(expression.getRightExpression()).thenReturn(right);
        doCallRealMethod().when(expression).accept(any());
        return expression;
    }

    @Test
    public void testEqualsToFilterCreation() {
        EqualsTo expression = createMockEqualsToStringExpression();
        filterCreator.visit(expression);
        Bson filter = filterCreator.getFilter();
        String expectedResult = String.format("Filter{fieldName='%s', value=%s}",
                TEST_COLUMN.getColumnName(), TEST_STRING_VALUE.getValue());
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void testGreaterThanFilterCreation() {
        GreaterThan expression = createMockGreaterThanLongExpression();
        filterCreator.visit(expression);
        Bson filter = filterCreator.getFilter();
        String expectedResult = String.format("Operator Filter{fieldName='%s', operator='$gt', value=%d}",
                TEST_COLUMN.getColumnName(), TEST_LONG_VALUE.getValue());
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void testLessThanFilterCreation() {
        MinorThan expression = createMockLessThanTimeExpression();
        filterCreator.visit(expression);
        Bson filter = filterCreator.getFilter();
        String expectedResult = String.format("Operator Filter{fieldName='%s', operator='$lt', value=%s}",
                TEST_COLUMN.getColumnName(), BSON_TIME);
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void testOrFilterCreator() {
        EqualsTo equalsToExpression = createMockEqualsToStringExpression();
        GreaterThan greaterThanExpression = createMockGreaterThanLongExpression();
        OrExpression orExpression = createMockOrExpression(equalsToExpression, greaterThanExpression);
        filterCreator.visit(orExpression);
        Bson filter = filterCreator.getFilter();
        String expectedResult =
                String.format("Or Filter{filters=[Filter{fieldName='%1$s', value=%2$s}, "
                                + "Operator Filter{fieldName='%1$s', operator='$gt', value=%3$d}]}",
                        TEST_COLUMN.getColumnName(), TEST_STRING_VALUE.getValue(), TEST_LONG_VALUE.getValue());
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void testAndFilterCreator() {
        Expression equalsToExpression = createMockGreaterThanLongExpression();
        Expression greaterThanExpression = createMockLessThanTimeExpression();
        AndExpression andExpression = createMockAndExpression(equalsToExpression, greaterThanExpression);
        filterCreator.visit(andExpression);
        Bson filter = filterCreator.getFilter();
        String expectedResult =
                String.format("And Filter{filters=[Operator Filter{fieldName='%1$s', operator='$gt', value=%2$d}, "
                                + "Operator Filter{fieldName='%1$s', operator='$lt', value=%3$s}]}",
                        TEST_COLUMN.getColumnName(), TEST_LONG_VALUE.getValue(), BSON_TIME);
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }
}
