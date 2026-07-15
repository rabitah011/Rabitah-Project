package com.rabitah.backend.academic;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="student_roster")
public class StudentRoster {
 @Id private UUID id; @Column(name="student_id",unique=true,nullable=false) private String studentId;
 @Column(name="legal_name",nullable=false) private String legalName; @Column(name="normalized_legal_name",nullable=false) private String normalizedLegalName;
 @Column(name="department_code",nullable=false) private String departmentCode; @Column(name="section_code",nullable=false) private String sectionCode;
 @Column(name="current_year",nullable=false) private int currentYear; @Column(nullable=false) private String status;
 @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected StudentRoster() {}
 public StudentRoster(String sid,String name,String dept,String section,int year){id=UUID.randomUUID();studentId=sid;legalName=name;normalizedLegalName=normalize(name);departmentCode=dept;sectionCode=section;currentYear=year;status="ACTIVE";createdAt=Instant.now();updatedAt=createdAt;}
 public static String normalize(String value){return value.trim().replaceAll("\\s+"," ").toLowerCase(java.util.Locale.ROOT);}
 public String getStudentId(){return studentId;} public String getLegalName(){return legalName;} public String getDepartmentCode(){return departmentCode;} public String getSectionCode(){return sectionCode;} public int getCurrentYear(){return currentYear;}
 public boolean matches(String name,String dept,String section,int year){return status.equals("ACTIVE")&&normalizedLegalName.equals(normalize(name))&&departmentCode.equalsIgnoreCase(dept)&&sectionCode.equalsIgnoreCase(section)&&currentYear==year;}
}
