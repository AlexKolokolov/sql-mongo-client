package org.kolokolov.testtask.queryparser;

import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kolokolov.testtask.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class SqlQueryParserOrderByTest {

    @Autowired
    private SqlQueryParser sqlQueryParser;

    @Test
    public void testOrderByAscend() {
        String sqlQuery = "SELECT * FROM x ORDER BY y";
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        assertThat(sqlQueryParser.getOrderByElements(select).get(0).getExpression().toString())
                .isEqualTo("y");
        assertThat(sqlQueryParser.getOrderByElements(select).get(0).isAsc()).isTrue();
    }

    @Test
    public void testOrderByDescend() {
        String sqlQuery = "SELECT * FROM x ORDER BY y DESC";
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        assertThat(sqlQueryParser.getOrderByElements(select).get(0).getExpression().toString())
                .isEqualTo("y");
        assertThat(sqlQueryParser.getOrderByElements(select).get(0).isAsc()).isFalse();
    }

    @Test
    public void testNoOrderBy() {
        String sqlQuery = "SELECT * FROM x";
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        assertThat(sqlQueryParser.getOrderByElements(select)).isEmpty();
    }
}
