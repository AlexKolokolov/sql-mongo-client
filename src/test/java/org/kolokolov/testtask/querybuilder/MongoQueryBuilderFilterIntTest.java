package org.kolokolov.testtask.querybuilder;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Select;
import org.bson.conversions.Bson;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kolokolov.testtask.Application;
import org.kolokolov.testtask.queryparser.SqlQueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
@SpringBootTest(classes = Application.class)
public class MongoQueryBuilderFilterIntTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private String sqlQuery;

    private String filterToString;

    @Autowired
    private SqlQueryParser sqlQueryParser;

    @Autowired
    private MongoQueryBuilder mongoQueryBuilder;

    public MongoQueryBuilderFilterIntTest(String sqlQuery, String filterToString) {
        this.sqlQuery = sqlQuery;
        this.filterToString = filterToString;
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Arrays.asList(new Object[][] {
                {"SELECT * FROM x WHERE a = 'b'", "Filter{fieldName='a', value=b}"},
                {"SELECT * FROM x WHERE a > 0", "Operator Filter{fieldName='a', operator='$gt', value=0}"},
                {"SELECT * FROM x WHERE a >= 5 OR b <> 3",
                        "Or Filter{filters=[Operator Filter{fieldName='a', operator='$gte', value=5}, "
                                + "Operator Filter{fieldName='b', operator='$ne', value=3}]}"},
                {"SELECT * FROM x WHERE a = 'bbb' AND b <> 'aaa'",
                        "And Filter{filters=[Filter{fieldName='a', value=bbb}, "
                                + "Operator Filter{fieldName='b', operator='$ne', value=aaa}]}"}
        });
    }

    @Test
    public void selectedFieldsTest() {
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        Expression whereExpression = sqlQueryParser.getWhereExpression(select);
        Bson filter = mongoQueryBuilder.createFilter(whereExpression);
        assertThat(filter.toString()).isEqualTo(filterToString);
    }
}
