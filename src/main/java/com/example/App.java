package com.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        
        Configuration configuration = new Configuration();
        configuration.configure();

        SessionFactory session = configuration.buildSessionFactory();
    }
}
