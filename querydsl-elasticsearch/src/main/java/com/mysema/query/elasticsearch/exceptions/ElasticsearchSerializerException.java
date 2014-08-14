package com.mysema.query.elasticsearch.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 14.08.14
 */
public class ElasticsearchSerializerException  extends  RuntimeException{
    public ElasticsearchSerializerException(String message){
        super(message);
    }
}
