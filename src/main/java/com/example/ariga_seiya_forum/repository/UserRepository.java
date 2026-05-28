package com.example.ariga_seiya_forum.repository;

import com.example.ariga_seiya_forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByAccountAndPassword(
            String account,
            String password);
}
