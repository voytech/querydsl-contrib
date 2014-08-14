package com.mysema.query.elasticsearch;

import com.mysema.query.elasticsearch.exceptions.ElasticsearchSerializerException;
import com.mysema.query.types.*;
import com.sun.corba.se.spi.ior.ObjectId;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.ScriptFilterBuilder;

import javax.annotation.Nullable;

import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 12.08.14
 */
public class ElasticsearchSerializer implements Visitor<Object, Void> {


    private SearchRequestBuilder builder;


    public void setBuilder(SearchRequestBuilder builder){
        this.builder = builder;

    }

    public Object handle(Expression<?> expr){
        return expr.accept(this,null);
    }

    private FilterBuilder toFilter(Expression<?> expr){
        return (FilterBuilder) expr.accept(this,null);
    }

    @Nullable
    @Override
    public Object visit(Constant<?> constant, @Nullable Void aVoid) {
        return constant.getConstant();
    }

    @Nullable
    @Override
    public Object visit(FactoryExpression<?> factoryExpression, @Nullable Void aVoid) {
        return null;
    }

    private FilterBuilder serializeCollCountOperation(Operation<?> nextOper){

        return null;
    }

    private FilterBuilder serializeCollectionAnyOperation(Operation<?> nextOper){
        return null;
    }



    @Nullable
    @Override
    public Object visit(Operation<?> operation, @Nullable Void aVoid) {
        Operator operator = operation.getOperator();
        if (operator == Ops.EQ){
           //handle case when first argument is operation of ArraySize
           //what if rhs is also pathexpression  -> then this should not be termFilter.
           return termFilter((String)handle(operation.getArg(0)),handle(operation.getArg(1)));
        }
        else if (operator == Ops.NE){
           return notFilter(termFilter((String) handle(operation.getArg(0)), handle(operation.getArg(1))));
        }
        else if (operator == Ops.GOE){
           return rangeFilter((String) handle(operation.getArg(0))).gte(handle(operation.getArg(1)));
        }
        else if (operator == Ops.GT){
            return rangeFilter((String) handle(operation.getArg(0))).gt(handle(operation.getArg(1)));
        }
        else if (operator == Ops.LOE){
            return rangeFilter((String) handle(operation.getArg(0))).lte(handle(operation.getArg(1)));
        }
        else if (operator == Ops.LT){
            return rangeFilter((String) handle(operation.getArg(0))).lt(handle(operation.getArg(1)));
        }
        else if (operator == Ops.AND){
            // What if teher is BooleanPath used as an second arguments.
            return andFilter().add(toFilter(operation.getArg(0))).add(toFilter(operation.getArg(1)));
        }
        else if (operator == Ops.OR){
            return orFilter().add(toFilter(operation.getArg(0))).add(toFilter(operation.getArg(1)));
        }
        else if (operator == Ops.BETWEEN){
            if (operation.getArgs().size() == 3){
                return rangeFilter((String) handle(operation.getArg(0))).gt(handle(operation.getArg(1))).lt(handle(operation.getArg(2)));
            }
            return null;
        }
        else if (operator == Ops.EXISTS){
            Object fieldName = handle(operation.getArg(0)); // arg should be constant expression
            return existsFilter((String)fieldName);
        }
        else if (operator == Ops.STARTS_WITH){
            Object fieldName = handle(operation.getArg(0));
            Object value = handle(operation.getArg(1));
            if (String.class.isAssignableFrom(fieldName.getClass()) && String.class.isAssignableFrom(value.getClass())){
                return prefixFilter((String)fieldName,(String) value);
            }
            else throw new ElasticsearchSerializerException("Starts with restriction can operate only on string args!");
        }
        else if (operator == Ops.ENDS_WITH){
            Object fieldName = handle(operation.getArg(0));
            Object value = handle(operation.getArg(1));
            if (String.class.isAssignableFrom(fieldName.getClass()) && String.class.isAssignableFrom(value.getClass())){
                return regexpFilter((String) fieldName, ".*" + ((String) value));
            }
            else throw new ElasticsearchSerializerException("Starts with restriction can operate only on string args!");
        }
        else if (operator == Ops.IS_NOT_NULL){
            Object fieldName = handle(operation.getArg(0));
            if (String.class.isAssignableFrom(fieldName.getClass())){
                return existsFilter((String) fieldName);
            }
            else throw new ElasticsearchSerializerException("Starts with restriction can operate only on string args!");
        }
        else if (operator == Ops.IS_NULL){
            Object fieldName = handle(operation.getArg(0));
            if (String.class.isAssignableFrom(fieldName.getClass())){
                return notFilter(existsFilter((String) fieldName));
            }
            else throw new ElasticsearchSerializerException("Starts with restriction can operate only on string args!");
        }
        else if (operator == Ops.MATCHES){
            Object fieldName = handle(operation.getArg(0));
            Object value = handle(operation.getArg(1));
            if (String.class.isAssignableFrom(fieldName.getClass()) && String.class.isAssignableFrom(value.getClass())){
                return regexpFilter((String) fieldName,((String) value));
            }
            else throw new ElasticsearchSerializerException("Starts with restriction can operate only on string args!");
        }

        //operator == Ops.
        return null;
    }

    @Nullable
    @Override
    public Object visit(ParamExpression<?> paramExpression, @Nullable Void aVoid) {
        return null;
    }

    @Nullable
    @Override
    public Object visit(Path<?> expr, @Nullable Void context) {
        PathMetadata<?> metadata = expr.getMetadata();
        if (metadata.getParent() != null) {
            if (metadata.getPathType() == PathType.COLLECTION_ANY) {
                return visit(metadata.getParent(), context);
            } else if (metadata.getParent().getMetadata().getPathType() != PathType.VARIABLE) {
                String rv = getKeyForPath(expr, metadata);
                return visit(metadata.getParent(), context) + "." + rv;
            }
        }
        return getKeyForPath(expr, metadata);
    }

    protected String getKeyForPath(Path<?> expr, PathMetadata<?> metadata) {
        if (expr.getType().equals(ObjectId.class)) {
            return "_id";
        } else {
            return metadata.getElement().toString();
        }
    }

    @Nullable
    @Override
    public Object visit(SubQueryExpression<?> subQueryExpression, @Nullable Void aVoid) {
        return null;
    }

    @Nullable
    @Override
    public Object visit(TemplateExpression<?> templateExpression, @Nullable Void aVoid) {
        return null;
    }
}
