package com.shashwat.docsapp.config;

import com.shashwat.docsapp.model.User;
import com.shashwat.docsapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        String[] names = {"shashwat", "gautam", "ajaia"};
        for (int i = 0; i < names.length; i++) {
            if (userRepository.findByUsername(names[i]).isEmpty()) {
                userRepository.save(new User(names[i], "password123")); //I used plain text as password. That’s fine for a demo with test accounts, it is a known limitation.
            }
        }
        System.out.println("Users in db: " + userRepository.count());
    }
}