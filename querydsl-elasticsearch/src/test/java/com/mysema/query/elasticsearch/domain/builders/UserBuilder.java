package com.mysema.query.elasticsearch.domain.builders;

import com.mysema.query.elasticsearch.domain.User;

import java.util.ArrayList;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: Wojciech Mąka
 * Date: 12.08.14
 */
public class UserBuilder {
    private String firstName;
    private String lastName;
    private String id;
    private java.util.Date created;
    private String description;
    private int age;
    private User.Gender gender;
    private final List<User> users = new ArrayList<User>();

    public UserBuilder firstName(String name) {
        this.firstName = name;
        return this;
    }

    public UserBuilder lastName(String name) {
        this.lastName = name;
        return this;
    }

    public UserBuilder age(int age) {
        this.age = age;
        return this;
    }

    public UserBuilder id(String id) {
        this.id = id;
        return this;
    }

    public UserBuilder details(String details) {
        this.description = details;
        return this;
    }

    public UserBuilder gender(User.Gender gender) {
        this.gender = gender;
        return this;
    }

    public UserBuilder created(java.util.Date date) {
        this.created = date;
        return this;
    }

    public User build() {
        User user = new User();
        user.setAge(age);
        user.setCreated(created);
        user.setDetails(description);
        user.setId(id);
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setGender(gender);
        users.add(user);
        return user;
    }

    public int count(){
        return users.size();
    }

    public User[] allUsers(){
        User[] arr =  users.toArray(new User[]{});
        users.clear();
        return arr;
    }

}
