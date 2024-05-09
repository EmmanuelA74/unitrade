package com.example.unitrade;

public class MySingleton {
    private static MySingleton instance;
    private String username;

    private MySingleton() {
    }

    public static MySingleton getInstance() {
        if (instance == null) {
            instance = new MySingleton();
        }
        return instance;
    }

    public String getMyVariable() {
        return username;
    }

    public void setMyVariable(String myVariable) {
        this.username = myVariable;
    }
}

