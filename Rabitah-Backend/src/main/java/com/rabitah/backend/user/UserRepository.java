package com.rabitah.backend.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLoginIdIgnoreCase(String loginId);
    boolean existsByStudentIdIgnoreCase(String studentId);
}
