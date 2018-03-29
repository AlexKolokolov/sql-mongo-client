package org.kolokolov.testtask.querybuilder;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kolokolov.testtask.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class MongoQueryBuilderProjectionTest {

    @Autowired
    private MongoQueryBuilder mongoQueryBuilder;

    @Test
    public void projectionWithSpecifiedFieldsExcludesId() {
        List<String> selectedFields = Arrays.asList("a", "b");
        Document projection = mongoQueryBuilder.createProjectionOfFields(selectedFields);
        assertThat(projection).contains(entry("a", true), entry("b", true), entry("_id", false));
    }
}
