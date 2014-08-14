package com.mysema.query.elasticsearch;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.query.elasticsearch.domain.QUser;
import com.mysema.query.elasticsearch.domain.User;
import com.mysema.query.elasticsearch.domain.builders.MappingHelper;
import com.mysema.query.elasticsearch.domain.builders.UserBuilder;
import com.mysema.query.types.Ops;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Deque;
import java.util.Map;

import static com.mysema.commons.lang.Assert.assertThat;
import static com.mysema.commons.lang.Assert.isTrue;
import static junit.framework.Assert.assertTrue;
import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 12.08.14
 */
public class ElasticsearchQueryTest {

    private Client client;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final QUser user = QUser.user;



    private void testData(){
       MappingHelper.pushUserMapping(User.class.getSimpleName().toLowerCase(), client);

       UserBuilder builder = new UserBuilder();
       User user =           builder.age(20)
                                    .created(new Date())
                                    .details(null)
                                    .firstName("Alllasah")
                                    .lastName("Uzubehhh")
                                    .gender(User.Gender.MALE)
                                    .id("01")
                                    .build();
       User friend =        builder.age(17)
                                    .created(new Date())
                                    .details("User 02")
                                    .firstName("UUUUddsda")
                                    .lastName("Uzehhh")
                                    .gender(User.Gender.MALE)
                                    .id("02")
                                    .build();
       user.getFriends().add(friend);
       try {
           client.prepareIndex(User.class.getSimpleName().toLowerCase(), User.class.getSimpleName(), "01").setSource(objectMapper.writeValueAsString(user)).execute().actionGet();
           GetResponse getResponse = client.prepareGet(User.class.getSimpleName().toLowerCase(),User.class.getSimpleName(),"01").execute().actionGet();
           User responsee = objectMapper.readValue(getResponse.getSourceAsString(), User.class);
           client.prepareIndex(User.class.getSimpleName().toLowerCase(),User.class.getSimpleName(),"02").setSource(objectMapper.writeValueAsString(friend)).execute().actionGet();
           getResponse = client.prepareGet(User.class.getSimpleName().toLowerCase(),User.class.getSimpleName(),"02").execute().actionGet();
           responsee = objectMapper.readValue(getResponse.getSourceAsString(), User.class);
           //SearchResponse response = client.prepareSearch("users").setTypes("user").setSearchType(SearchType.QUERY_AND_FETCH).setQuery(filteredQuery(matchAllQuery(),CollectionHelpers.collectionSizeFilterBuilder("friends", Ops.LT,1))).setExplain(true).execute().actionGet();
           //SearchHit[] hits = response.getHits().getHits();
           //if (response==null) throw  new RuntimeException("Fuckit");

       } catch (JsonProcessingException e) {
            e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    @Before
    public void setUp() throws Exception {
        //Node node = nodeBuilder().node();
        client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
        //client = node.client();
        testData();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void simpleQueriesTest(){
        ElasticsearchQuery<User> query = new ElasticsearchQuery<User>(client,user);
        User result = query.where(user.firstName.eq("Alllasah").and(user.lastName.eq("Uzubehhh"))).singleResult();
        assertNotNull(result);
        assertTrue(result.getFirstName().equals("Alllasah"));

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.firstName.eq("Alllasah").or(user.lastName.eq("Ojjjoj"))).singleResult();
        assertNotNull(result);
        assertTrue(result.getFirstName().equals("Alllasah"));

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.firstName.eq("Alllasah").and(user.lastName.eq("Ojjjoj"))).singleResult();
        assertNull(result);

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.firstName.between("Alllasah", "Alllasai")).singleResult();
        assertNull(result);

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.firstName.startsWith("Alll")).singleResult();
        assertNotNull(result);

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.firstName.endsWith("sah")).singleResult();
        assertNotNull(result);

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.firstName.isNotNull()).singleResult();
        assertNotNull(result);

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.details.isNull()).singleResult();
        assertNotNull(result);

        query = new ElasticsearchQuery<User>(client,user);
        result = query.where(user.firstName.matches(".*las.*")).singleResult();
        assertNotNull(result);

    }

    @Test
    public void comparisonOperators(){

        ElasticsearchQuery<User> query = new ElasticsearchQuery<User>(client,user);
        User result = query.where(user.age.between(16, 18)).singleResult();
        assertNotNull(result);

        query = new ElasticsearchQuery<User>(client,user);
        java.util.List<User> results = query.where(user.age.gt(16)).list();
        assertNotNull(results);
        Assert.assertTrue(results.size()==2);


        query = new ElasticsearchQuery<User>(client,user);
        results = query.where(user.age.goe(17)).list();
        assertNotNull(results);
        Assert.assertTrue(results.size()==2);

        query = new ElasticsearchQuery<User>(client,user);
        results = query.where(user.age.gt(17)).list();
        assertNotNull(results);
        Assert.assertTrue(results.size()==1);

    }
}
