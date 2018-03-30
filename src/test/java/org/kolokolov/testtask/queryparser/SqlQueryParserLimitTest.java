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
public class SqlQueryParserLimitTest {

    @Autowired
    private SqlQueryParser sqlQueryParser;

    @Test
    public void testLimitWithOffset() {
        String sqlQuery = "SELECT * FROM x LIMIT 1, 1";
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        assertThat(sqlQueryParser.getLimit(select).get().getOffset()).isEqualTo(1);
        assertThat(sqlQueryParser.getLimit(select).get().getRowCount()).isEqualTo(1);
    }

    @Test
    public void testLimitWithOutOffset() {
        String sqlQuery = "SELECT * FROM x LIMIT 1";
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        assertThat(sqlQueryParser.getLimit(select).get().getOffset()).isEqualTo(0);
        assertThat(sqlQueryParser.getLimit(select).get().getRowCount()).isEqualTo(1);
    }

    @Test
    public void testQueryWithOutLimit() {
        String sqlQuery = "SELECT * FROM x";
        Select select = sqlQueryParser.parseSqlQuery(sqlQuery);
        assertThat(sqlQueryParser.getLimit(select)).isEmpty();
    }
}
