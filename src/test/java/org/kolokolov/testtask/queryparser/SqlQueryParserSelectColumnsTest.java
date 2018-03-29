package org.kolokolov.testtask.queryparser;

import net.sf.jsqlparser.statement.select.Select;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kolokolov.testtask.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
@SpringBootTest(classes = Application.class)
public class SqlQueryParserSelectColumnsTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private String sqlQuery;

    private List<String> selectedFields;

    @Autowired
    private SqlQueryParser sqlQueryParser;

    public SqlQueryParserSelectColumnsTest(String sqlQuery, List<String> selectedFields) {
        this.sqlQuery = sqlQuery;
        this.selectedFields = selectedFields;
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Arrays.asList(new Object[][] {
                {"SELECT * FROM x", Collections.emptyList()},
                {"SELECT a FROM x", Collections.singletonList("a")},
                {"SELECT a, b FROM x", Arrays.asList("a", "b")},
                {"SELECT a, b.* FROM x", Arrays.asList("a", "b")},
                {"SELECT a, b.c FROM x", Arrays.asList("a", "b.c")},
                {"SELECT a, b, * FROM x", Collections.emptyList()},
                {"SELECT *, name FROM x", Collections.emptyList()}
        });
    }

    @Test
    public void selectedFieldsTest() {
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        assertThat(sqlQueryParser.getFieldsList(select)).isEqualTo(selectedFields);
    }
}
