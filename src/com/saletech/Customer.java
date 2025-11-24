package com.saletech;

public class Customer {

    private static int nextId = 1;

    private final int id;
    private final String name;
    private final String email;

    public Customer(String name, String email) {
        this.id = nextId++;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Customer[id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
