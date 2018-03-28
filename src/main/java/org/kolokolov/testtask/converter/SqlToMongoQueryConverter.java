package org.kolokolov.testtask.converter;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.Select;
import org.bson.Document;
import org.kolokolov.testtask.queryparser.SqlQueryParser;
import org.kolokolov.testtask.querybuilder.MongoQueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SqlToMongoQueryConverter {

    private final SqlQueryParser queryParser;
    private final MongoQueryBuilder builder;
    private final PrintWriter output;

    @Value("${mongo.host:localhost}")
    private String host;

    @Value("${mongo.port:27017}")
    private Integer port;

    @Value("${mongo.database:testdb}")
    private String databaseName;

    public SqlToMongoQueryConverter(SqlQueryParser queryParser, MongoQueryBuilder builder, PrintWriter output) {
        this.queryParser = queryParser;
        this.builder = builder;
        this.output = output;
    }

    public void convertQueryAndRun(String sqlQuery) {
        MongoClient client = new MongoClient(host, port);
        MongoDatabase database = client.getDatabase(databaseName);
        Select select = queryParser.parseSqlQuery(sqlQuery);
        List<String> tables = queryParser.getTableNames(select);

        for (String table: tables) {
            MongoCollection<Document> collection = database.getCollection(table);
            FindIterable<Document> mongoQuery = collection.find();
            List<String> fields = queryParser.getFieldsList(select);
            builder.addProjection(mongoQuery, fields);
            Expression whereExpression = queryParser.getWhereExpression(select);
            builder.addFilter(mongoQuery, whereExpression);
            Limit limit = queryParser.getLimit(select);
            builder.addLimit(mongoQuery, limit);
            ArrayList<Document> result = mongoQuery.into(new ArrayList<>());
            result.forEach(output::println);
        }
    }

}
