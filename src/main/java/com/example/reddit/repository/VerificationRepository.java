package com.example.reddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reddit.model.VerificationToken;

public interface VerificationRepository extends JpaRepository<VerificationToken, Long>{

}
