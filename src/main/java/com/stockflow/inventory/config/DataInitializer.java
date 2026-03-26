package com.stockflow.inventory.config;

import com.stockflow.inventory.entity.Product;
import com.stockflow.inventory.entity.User;
import com.stockflow.inventory.enums.Role;
import com.stockflow.inventory.repository.ProductRepository;
import com.stockflow.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository    userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder   passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedProducts();
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    private void seedUsers() {
        if (userRepository.count() == 0) {
            log.info("Seeding default users...");

            userRepository.saveAll(List.of(
                User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .name("Admin User")
                    .role(Role.ADMIN)
                    .active(true)
                    .build(),
                User.builder()
                    .username("staff1")
                    .password(passwordEncoder.encode("staff123"))
                    .name("John Staff")
                    .role(Role.STAFF)
                    .active(true)
                    .build(),
                User.builder()
                    .username("staff2")
                    .password(passwordEncoder.encode("staff123"))
                    .name("Sarah Smith")
                    .role(Role.STAFF)
                    .active(true)
                    .build(),
                User.builder()
                    .username("staff3")
                    .password(passwordEncoder.encode("staff123"))
                    .name("Mike Johnson")
                    .role(Role.STAFF)
                    .active(true)
                    .build()
            ));

            log.info("Default users created.");
            log.info("  Admin  → username: admin,  password: admin123");
            log.info("  Staff  → username: staff1, password: staff123");
        }
    }

    // ── Products ──────────────────────────────────────────────────────────────

    private void seedProducts() {
        if (productRepository.count() == 0) {
            log.info("Seeding sample products...");

            productRepository.saveAll(List.of(
                product("Ceiling Fan 48\"",       "FAN",        25, 10, "Standard 48 inch ceiling fan"),
                product("Split AC 1.5 Ton",       "AC",          4,  5, "Energy-efficient 1.5 ton split AC"),
                product("Tower Fan",              "FAN",         8,  5, "Portable tower fan with remote"),
                product("Window AC 1 Ton",        "AC",          2,  3, "1 ton window air conditioner"),
                product("Exhaust Fan 12\"",        "FAN",        50, 15, "12 inch exhaust fan"),
                product("Portable AC 1 Ton",      "AC",          6,  3, "Portable air conditioner"),
                product("Industrial Fan",         "FAN",        12,  4, "Heavy duty industrial fan"),
                product("Air Cooler 30L",         "COOLER",      3,  5, "30 litre desert air cooler"),
                product("UPS 650VA",              "ELECTRICAL", 15,  5, "650VA UPS for backup power"),
                product("Extension Board 6-Port","ELECTRICAL",   1, 10, "6-port surge-protected board")
            ));

            log.info("Sample products created.");
        }
    }

    private Product product(String name, String category, int stock, int threshold, String desc) {
        return Product.builder()
                .name(name)
                .category(category)
                .stockQuantity(stock)
                .lowStockThreshold(threshold)
                .description(desc)
                .build();
    }
}
