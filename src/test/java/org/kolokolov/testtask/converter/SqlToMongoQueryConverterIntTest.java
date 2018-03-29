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
import java.util.Arrays;
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
    public void prepareQueriesSingleDocumentTest() {
        Document document = new Document("name", "Bob");

        collection.insertOne(document);

        List<FindIterable<Document>> queries = converter.prepareQueries("SELECT * FROM user WHERE name = 'Bob'");
        List<Document> documents = runQueriesAndReturnDocuments(queries);
        assertThat(documents).isNotEmpty();
        assertThat(documents.get(0).get("name")).isEqualTo("Bob");
    }

    @Test
    public void prepareQueriesMultipleDocumentsWithOrderByAndLimitTest() {
        Document document1 = new Document("name", "Bob").append("age", "30");
        Document document2 = new Document("name", "Dug").append("age", "20");
        Document document3 = new Document("name", "Kit").append("age", "50");

        collection.insertMany(Arrays.asList(document1, document2, document3));

        String sqlQuery = "SELECT name FROM user WHERE age >= 30 ORDER BY age DESC LIMIT 1, 1";

        List<FindIterable<Document>> queries = converter.prepareQueries(sqlQuery);
        List<Document> documents = runQueriesAndReturnDocuments(queries);
        assertThat(documents).isNotEmpty();
        assertThat(documents).hasSize(1);
        assertThat(documents.get(0).get("name")).isEqualTo("Bob");
        assertThat(documents.get(0).get("age")).isNull();
        assertThat(documents.get(0).get("_id")).isNull();
    }

    @Test
    public void prepareQueriesSingleDocumentWithNestedDocumentTest() {
        Document document = new Document("name", "Bob")
                .append("contacts", new Document("phone", "555-55-55").append("email", "bob@mail.com"));

        collection.insertOne(document);

        String sqlQuery = "SELECT name, contacts.phone FROM user";

        List<FindIterable<Document>> queries = converter.prepareQueries(sqlQuery);
        List<Document> documents = runQueriesAndReturnDocuments(queries);
        assertThat(documents).isNotEmpty();
        assertThat(documents).hasSize(1);
        assertThat(documents.get(0).get("name")).isEqualTo("Bob");
        assertThat(documents.get(0).get("_id")).isNull();
        assertThat(documents.get(0).get("contacts")).isInstanceOf(Document.class);
        assertThat(((Document) documents.get(0).get("contacts")).get("phone")).isEqualTo("555-55-55");
        assertThat(((Document) documents.get(0).get("contacts")).get("email")).isNull();

    }

    private List<Document> runQueriesAndReturnDocuments(List<FindIterable<Document>> queries) {
        return queries.stream()
            .flatMap(query -> query.into(new ArrayList<>()).stream())
            .collect(toList());
    }
}
