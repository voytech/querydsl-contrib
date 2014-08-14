package com.mysema.query.elasticsearch;


import com.mysema.query.types.Operator;
import com.mysema.query.types.Ops;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.ScriptFilterBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 14.08.14
 */
public class CollectionHelpers {

    public static ScriptFilterBuilder collectionSizeFilterBuilder(String fieldName,Operator<Boolean> operator,Object value){
        String op = "=";
        if (operator == Ops.EQ)  op = "=";
        else if (operator == Ops.NE)  op = "!=";
        else if (operator ==Ops.GT)  op = ">";
        else if (operator ==Ops.LT)  op = "<";
        else if (operator ==Ops.GOE) op = ">=";
        else if (operator ==Ops.LOE) op = "<=";
        return FilterBuilders.scriptFilter("doc['" + fieldName + "'].values.length " + op +" "+value);
    }
}
