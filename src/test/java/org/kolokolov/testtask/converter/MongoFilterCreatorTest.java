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

    private EqualsTo createEqualsToStringExpression() {
        EqualsTo expression = new EqualsTo();
        expression.setLeftExpression(TEST_COLUMN);
        expression.setRightExpression(TEST_STRING_VALUE);
        return expression;
    }

    private GreaterThan createGreaterThanLongExpression() {
        GreaterThan expression = new GreaterThan();
        expression.setLeftExpression(TEST_COLUMN);
        expression.setRightExpression(TEST_LONG_VALUE);
        return expression;
    }

    private MinorThan createLessThanTimeExpression() {
        MinorThan expression = new MinorThan();
        expression.setLeftExpression(TEST_COLUMN);
        expression.setRightExpression(TEST_TIME_VALUE);
        return expression;
    }

    @Test
    public void testEqualsToFilterCreation() {
        EqualsTo expression = createEqualsToStringExpression();
        filterCreator.visit(expression);
        Bson filter = filterCreator.getFilter();
        String expectedResult = String.format("Filter{fieldName='%s', value=%s}",
                TEST_COLUMN.getColumnName(), TEST_STRING_VALUE.getValue());
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void testGreaterThanFilterCreation() {
        GreaterThan expression = createGreaterThanLongExpression();
        filterCreator.visit(expression);
        Bson filter = filterCreator.getFilter();
        String expectedResult = String.format("Operator Filter{fieldName='%s', operator='$gt', value=%d}",
                TEST_COLUMN.getColumnName(), TEST_LONG_VALUE.getValue());
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void testLessThanFilterCreation() {
        MinorThan expression = createLessThanTimeExpression();
        filterCreator.visit(expression);
        Bson filter = filterCreator.getFilter();
        String expectedResult = String.format("Operator Filter{fieldName='%s', operator='$lt', value=%s}",
                TEST_COLUMN.getColumnName(), BSON_TIME);
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void testOrFilterCreator() {
        EqualsTo equalsToExpression = createEqualsToStringExpression();
        GreaterThan greaterThanExpression = createGreaterThanLongExpression();
        OrExpression orExpression = new OrExpression(equalsToExpression, greaterThanExpression);
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
        Expression equalsToExpression = createGreaterThanLongExpression();
        Expression greaterThanExpression = createLessThanTimeExpression();
        AndExpression andExpression = new AndExpression(equalsToExpression, greaterThanExpression);
        filterCreator.visit(andExpression);
        Bson filter = filterCreator.getFilter();
        String expectedResult =
                String.format("And Filter{filters=[Operator Filter{fieldName='%1$s', operator='$gt', value=%2$d}, "
                                + "Operator Filter{fieldName='%1$s', operator='$lt', value=%3$s}]}",
                        TEST_COLUMN.getColumnName(), TEST_LONG_VALUE.getValue(), BSON_TIME);
        assertThat(filter.toString()).isEqualTo(expectedResult);
    }
}
