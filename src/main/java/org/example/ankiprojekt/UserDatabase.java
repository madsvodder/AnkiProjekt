package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDatabase implements Serializable {

    @Getter @Setter
    private ArrayList<User> users;

    @Getter @Setter
    private User activeUser;

    public UserDatabase() {
        users = new ArrayList<>();
    }

    public ArrayList<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    private static UserDatabase instance;
    public static synchronized UserDatabase getInstance() {
        // Check if there's no instance already created
        if (instance == null) {
            // Create a new instance if none exists
            instance = new UserDatabase();
        }
        return instance;
    }

    public static void setInstance(UserDatabase existingInstance) {
        instance = existingInstance;

        if (instance.users == null) {
            instance.users = new ArrayList<>();
        }

        System.out.println("Users after loading:");
        for (User user : instance.users) {
            System.out.println(user);
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }
}
