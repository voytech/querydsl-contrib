package com.mysema.query.elasticsearch;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.*;
import com.mysema.query.support.QueryMixin;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.Predicate;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.support.AbstractClient;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 12.08.14
 */
public class ElasticsearchQuery<Q> implements SimpleQuery<ElasticsearchQuery<Q>>, SimpleProjectable<Q> {
    private final QueryMixin<ElasticsearchQuery<Q>> queryMixin;
    private final AbstractClient client;

    public ElasticsearchQuery(AbstractClient client){
        this.queryMixin = new QueryMixin<ElasticsearchQuery<Q>>(this,new DefaultQueryMetadata().noValidate());
        this.client = client;
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

    @Override
    public List<Q> list() {
        return null;
    }

    @Nullable
    @Override
    public Q singleResult() {
        return null;
    }

    @Nullable
    @Override
    public Q uniqueResult() {
        return null;
    }

    @Override
    public SearchResults<Q> listResults() {
        return null;
    }

    @Override
    public long count() {
        return 0;
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
        return  queryMixin.where(predicates);
    }
}
