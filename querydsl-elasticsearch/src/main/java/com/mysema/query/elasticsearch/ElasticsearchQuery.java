package com.mysema.query.elasticsearch;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.*;
import com.mysema.query.support.QueryMixin;
import com.mysema.query.types.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.index.query.FilterBuilder;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 12.08.14
 */
public class ElasticsearchQuery<Q> implements SimpleQuery<ElasticsearchQuery<Q>>, SimpleProjectable<Q> {
    private final QueryMixin<ElasticsearchQuery<Q>> queryMixin;
    private final ElasticsearchSerializer serializer = new ElasticsearchSerializer();
    private final Client client;
    private final EntityPath<Q> entityPath;
    private final ResultMapper mapper;
    private SearchRequestBuilder requestBuilder;

    public ElasticsearchQuery(Client client,EntityPath<Q> entityPath){
        this.queryMixin = new QueryMixin<ElasticsearchQuery<Q>>(this,new DefaultQueryMetadata().noValidate());
        this.client = client;
        this.entityPath = entityPath;
        this.mapper =new ResultMapper();
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean notExists() {
        return false;
    }

    @Override
    public CloseableIterator<Q> iterate() {
        return null;
    }

    public List<Q> list(Path<?>... paths) {
        queryMixin.addProjection(paths);
        return list();
    }

    public Q singleResult(Path<?>... paths) {
        queryMixin.addProjection(paths);
        return singleResult();
    }

    @Override
    public List<Q> list() {
        SearchResponse response = executeSearchRequest(prepareSearchRequest(entityPath.getType().getSimpleName().toLowerCase()));
        if (response != null){
            return (List<Q>)mapper.mapQueryResponse(response,entityPath.getType());
        }
        return null;
    }

    @Nullable
    @Override
    public Q singleResult() {
        SearchResponse response = executeSearchRequest(prepareSearchRequest(entityPath.getType().getSimpleName().toLowerCase()));
        if (response != null){
            return mapper.mapFirstResult(response, entityPath.getType());
        }
        return null;
    }

    @Nullable
    @Override
    public Q uniqueResult() {
        return null;

    }

    public SearchResults<Q> listResults(Path<?> projection) {
        queryMixin.addProjection(projection);
        return listResults();
    }

    @Override
    public SearchResults<Q> listResults() {
        SearchResponse response = executeSearchRequest(prepareSearchRequest(entityPath.getType().getSimpleName().toLowerCase()));
        if (response != null){
            return null;//mapper.mapQueryResponseToSearchResults(response,entityPath.getType());
        }
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    private SearchResponse executeSearchRequest(SearchRequestBuilder request){
        if (request == null) throw new IllegalArgumentException("request is null");
        return request.execute().actionGet();
    }

    private SearchRequestBuilder prepareSearchRequest(String index){
        QueryMetadata metadata = queryMixin.getMetadata();
         if (metadata.getWhere() != null){
             Object restrictions = serializer.handle(metadata.getWhere());
             if (FilterBuilder.class.isAssignableFrom(restrictions.getClass())){
                 FilterBuilder filterBuilder = (FilterBuilder)restrictions;
                 return client.prepareSearch(index).setTypes(entityPath.getType().getSimpleName()).setQuery(filteredQuery(matchAllQuery(),filterBuilder));
             }
        }
        return null;
    }

    @Override
    public ElasticsearchQuery<Q> limit(@Nonnegative long l) {
        return queryMixin.limit(l);
    }

    @Override
    public ElasticsearchQuery<Q> offset(@Nonnegative long l) {
        return queryMixin.offset(l);
    }

    @Override
    public ElasticsearchQuery<Q> restrict(QueryModifiers queryModifiers) {
        return queryMixin.restrict(queryModifiers);
    }

    @Override
    public ElasticsearchQuery<Q> orderBy(OrderSpecifier<?>... orderSpecifiers) {
        return queryMixin.orderBy(orderSpecifiers);
    }

    @Override
    public <T> ElasticsearchQuery<Q> set(ParamExpression<T> tParamExpression, T t) {
        return queryMixin.set(tParamExpression,t);
    }

    @Override
    public ElasticsearchQuery<Q> distinct() {
        return queryMixin.distinct();
    }

    @Override
    public ElasticsearchQuery<Q> where(Predicate... predicates) {
        return queryMixin.where(predicates);
    }
}
