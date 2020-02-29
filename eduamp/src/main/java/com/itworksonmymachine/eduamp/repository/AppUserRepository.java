package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

}