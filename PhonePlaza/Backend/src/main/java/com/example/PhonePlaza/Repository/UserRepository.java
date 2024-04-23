package com.example.PhonePlaza.Repository;

import com.example.PhonePlaza.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    void deleteByEmail(String email);
}
