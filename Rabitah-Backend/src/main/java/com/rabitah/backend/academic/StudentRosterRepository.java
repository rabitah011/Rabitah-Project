package com.rabitah.backend.academic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface StudentRosterRepository extends JpaRepository<StudentRoster, UUID>{Optional<StudentRoster> findByStudentIdIgnoreCase(String studentId);}
