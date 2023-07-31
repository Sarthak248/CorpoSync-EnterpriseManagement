package com.sarthak.project.enterpriseMgmtSys.repository;

import java.util.Optional; 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 
import com.sarthak.project.enterpriseMgmtSys.entity.User;

@Repository public interface UserRepository extends JpaRepository<User, String> { 
   Optional<User> findUserByUsername(String username); 
}