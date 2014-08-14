package com.mysema.query.elasticsearch.domain.builders;

import com.mysema.query.elasticsearch.domain.User;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 14.08.14
 */
public class MappingHelper {

    public static void pushUserMapping(String indexName,Client client){
        if (client.admin().indices().prepareExists(indexName).execute().actionGet().isExists()){
            client.admin().indices().prepareDelete(indexName).execute().actionGet();
        }
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
        String docType = User.class.getSimpleName();
        XContentBuilder mappingBuilder = null;
        try {
            mappingBuilder = jsonBuilder().startObject()
                                              .startObject(docType)
                                                    .startObject("properties")
                                                        .startObject("firstName").field("type", "string").field("index", "not_analyzed")
                                                        .endObject()
                                                        .startObject("lastName").field("type", "string").field("index", "not_analyzed")
                                                        .endObject()
                                                        .startObject("details").field("type", "string").field("index", "not_analyzed")
                                                        .endObject()
                                                        .startObject("created").field("type", "date").field("index", "not_analyzed")
                                                        .endObject()
                                                        .startObject("age").field("type", "integer").field("index", "not_analyzed")
                                                        .endObject()
                                                        .startObject("gender").field("type", "string").field("index", "not_analyzed")
                                                        .endObject()
                                                        .startObject("friends")
                                                            .startObject("properties")
                                                                .startObject("firstName").field("type", "string").field("index", "not_analyzed")
                                                                .endObject()
                                                                .startObject("lastName").field("type", "string").field("index", "not_analyzed")
                                                                .endObject()
                                                                .startObject("details").field("type", "string").field("index", "not_analyzed")
                                                                .endObject()
                                                                .startObject("created").field("type", "date").field("index", "not_analyzed")
                                                                .endObject()
                                                                .startObject("age").field("type", "integer").field("index", "not_analyzed")
                                                                .endObject()
                                                                .startObject("gender").field("type", "string").field("index", "not_analyzed")
                                                                .endObject()
                                                            .endObject()
                                                        .endObject()
                                                    .endObject()
                                              .endObject()
                                           .endObject();
            System.out.println(mappingBuilder.string());

            createIndexRequestBuilder.addMapping(docType, mappingBuilder);
            createIndexRequestBuilder.execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
