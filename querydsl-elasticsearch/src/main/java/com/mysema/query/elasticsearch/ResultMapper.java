package com.mysema.query.elasticsearch;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.query.SearchResults;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 14.08.14
 */
public class ResultMapper {

    private final ObjectMapper objectMapper;

    public ResultMapper(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }
    public ResultMapper(){
        this.objectMapper = new ObjectMapper();
    }

    <T> SearchResults<T> mapQueryResponseToSearchResults(SearchResponse response,Class<T> clazz){

        return null;
    }

    <T> T mapFirstResult(SearchResponse response,Class<T> clazz){
        if (response.getHits().getTotalHits() > 0){
            try {
                return mapEntry(response.getHits().getAt(0).getSourceAsString(),clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    <T> List<T> mapQueryResponse(SearchResponse response,Class<T> clazz){
        List<T> lst = new ArrayList<T>();
        for (SearchHit hit : response.getHits().getHits())   {
            try {
                lst.add(mapEntry(hit.getSourceAsString(),clazz));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lst;
    }


    <T> T mapEntry(String content,Class<T> clazz) throws IOException {
         return objectMapper.readValue(content,clazz);
    }
}
