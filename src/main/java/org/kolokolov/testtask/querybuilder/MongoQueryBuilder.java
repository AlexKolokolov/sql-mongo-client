package org.kolokolov.testtask.querybuilder;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoQueryBuilder {

    public void addFilter(FindIterable<Document> query, Bson filter) {
        if (filter != null) {
            query.filter(filter);
        }
    }

    public void addProjection(FindIterable<Document> query, List<String> selectedFields) {
        if (selectedFields != null && !selectedFields.isEmpty()) {
            query.projection(createProjectionOfFields(selectedFields));
        }
    }

    Document createProjectionOfFields(List<String> fields) {
        Document fieldFilter = new Document("_id", false);
        fields.forEach(field -> fieldFilter.append(field, true));
        return fieldFilter;
    }

    public void addLimit(FindIterable<Document> query, int limit, int skip) {
            query.limit(limit);
            query.skip(skip);
    }

    public void addSort(FindIterable<Document> query, List<SortRule> sortRules) {
        if (sortRules != null && !sortRules.isEmpty()) {
            Document sort = new Document();
            sortRules.forEach(r -> sort.append(r.getField(), r.ascend()));
            query.sort(sort);
        }
    }
}
