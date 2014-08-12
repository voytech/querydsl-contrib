package com.mysema.query.elasticsearch;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.query.elasticsearch.domain.User;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Deque;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 12.08.14
 */
public class ElasticsearchQueryTest {

    private Client client;
    private ObjectMapper objectMapper = new ObjectMapper();

    class UserBuilder{
        private String firstName;
        private String lastName;
        private String id;
        private java.util.Date created;
        private String description;
        private int age;
        private User.Gender gender;

        public UserBuilder firstName(String name){
            this.firstName = name;
            return this;
        }

        public UserBuilder lastName(String name){
            this.lastName = name;
            return this;
        }

        public UserBuilder age(int age){
            this.age = age;
            return this;
        }

        public UserBuilder id(String id){
            this.id = id;
            return this;
        }

        public UserBuilder details(String details){
            this.description = details;
            return this;
        }

        public UserBuilder gender(User.Gender gender){
            this.gender = gender;
            return this;
        }

        public UserBuilder created(java.util.Date date){
            this.created = date;
            return this;
        }

        public User build(){
            User user = new User();
            user.setAge(age);
            user.setCreated(created);
            user.setDetails(description);
            user.setId(id);
            user.setLastName(lastName);
            user.setGender(gender);

            return user;
        }
    }

    private void testData(){
       User user = new UserBuilder().age(20)
                                    .created(new Date())
                                    .details("User 01")
                                    .firstName("Alllasah")
                                    .lastName("Uzubehhh")
                                    .gender(User.Gender.MALE)
                                    .build();
       try {
            client.prepareIndex("users","user","01").setSource(objectMapper.writeValueAsString(user)).execute().actionGet();
            GetResponse getResponse = client.prepareGet("users","user","01").execute().actionGet();
            User response = objectMapper.readValue(getResponse.getSourceAsString(), User.class);
            if (response==null) throw  new RuntimeException("Fuckit");
       } catch (JsonProcessingException e) {
            e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    @Before
    public void setUp() throws Exception {
        Node node = nodeBuilder().node();
        client = node.client();
        testData();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void simpleWhereQueryTest(){

    }
}
