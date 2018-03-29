package org.kolokolov.testtask.converter;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kolokolov.testtask.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class SqlToMongoQueryConverterIntTest {

    @Value("${mongo.host:localhost}")
    private String host;

    @Value("${mongo.port:27017}")
    private Integer port;

    @Value("${mongo.database:testdb}")
    private String databaseName;

    private static MongoCollection<Document> collection;

    @Before
    public void establishConnectionToDB() {
        MongoClient client = new MongoClient(host, port);
        MongoDatabase database = client.getDatabase(databaseName);
        collection = database.getCollection("user");
        collection.drop();
    }

    @After
    public void cleanUpDB() {
        collection.drop();
    }

    @Autowired
    private SqlToMongoQueryConverter converter;

    @Test
    public void prepareQueriesTest() {
        Document document = new Document("name", "Bob");

        collection.insertOne(document);

        List<FindIterable<Document>> queries = converter.prepareQueries("select * from user where name = 'Bob'");
        List<Document> documents = queries.stream()
                .flatMap(query -> query.into(new ArrayList<>()).stream())
                .collect(toList());
        assertThat(documents).isNotEmpty();
        assertThat(documents.get(0).get("name")).isEqualTo("Bob");
    }
}
