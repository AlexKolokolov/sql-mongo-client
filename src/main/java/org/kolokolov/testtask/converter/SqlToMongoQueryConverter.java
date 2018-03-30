package org.kolokolov.testtask.converter;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.Select;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.kolokolov.testtask.querybuilder.SortRule;
import org.kolokolov.testtask.queryparser.SqlQueryParser;
import org.kolokolov.testtask.querybuilder.MongoQueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class SqlToMongoQueryConverter {

    private final SqlQueryParser queryParser;
    private final MongoQueryBuilder builder;
    private final FilterCreator filterCreator;
    private final PrintWriter output;

    @Value("${mongo.host:localhost}")
    private String host;

    @Value("${mongo.port:27017}")
    private Integer port;

    @Value("${mongo.database:proddb}")
    private String databaseName;

    public SqlToMongoQueryConverter(SqlQueryParser queryParser,
                                    MongoQueryBuilder builder,
                                    FilterCreator filterCreator,
                                    PrintWriter output) {
        this.queryParser = queryParser;
        this.builder = builder;
        this.filterCreator = filterCreator;
        this.output = output;
    }

    public void convertQueryAndRun(String sqlQuery) {
        List<FindIterable<Document>> queries = prepareQueries(sqlQuery);
        runQueriesAndPrintResult(queries);
    }

    List<FindIterable<Document>> prepareQueries(String sqlQuery) {
        MongoClient client = new MongoClient(host, port);
        MongoDatabase database = client.getDatabase(databaseName);
        Select select = queryParser.parseSqlQuery(sqlQuery);
        List<String> tables = queryParser.getTableNames(select);
        List<FindIterable<Document>> queries = new ArrayList<>();
        for (String tableName: tables) {
            MongoCollection<Document> collection = database.getCollection(tableName);
            FindIterable<Document> mongoQuery = buildMongoQuery(collection, select);
            queries.add(mongoQuery);
        }
        return queries;
    }

    private void runQueriesAndPrintResult(List<FindIterable<Document>> queries) {
        queries.forEach(query ->
                query.into(new ArrayList<>()).forEach(output::println));
    }

    Optional<Bson> createFilter(Expression expression) {
        if (expression != null) {
            expression.accept(filterCreator);
            return Optional.of(filterCreator.getFilter());
        } else {
            return Optional.empty();
        }

    }

    private List<SortRule> orderByElementToSortRules(List<OrderByElement> orderByElements) {
        return orderByElements.stream()
                .map(e -> new SortRule(e.getExpression().toString(), e.isAsc()))
                .collect(toList());
    }

    private FindIterable<Document> buildMongoQuery(MongoCollection<Document> collection, Select selectParameters) {
        FindIterable<Document> mongoQuery = collection.find();
        builder.addProjection(mongoQuery, queryParser.getFieldsList(selectParameters));
        queryParser.getWhereExpression(selectParameters)
                .flatMap(this::createFilter)
                .ifPresent(filter -> builder.addFilter(mongoQuery, filter));
        queryParser.getLimit(selectParameters).ifPresent(limit ->
                builder.addLimit(mongoQuery, (int) limit.getRowCount(), (int) limit.getOffset()));
        builder.addSort(mongoQuery, orderByElementToSortRules(queryParser.getOrderByElements(selectParameters)));
        return mongoQuery;
    }
}
