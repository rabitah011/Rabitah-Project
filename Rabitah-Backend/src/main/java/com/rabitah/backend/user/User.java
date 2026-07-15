package com.rabitah.backend.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id private UUID id;
    @Column(name="login_id", nullable=false, unique=true) private String loginId;
    @Column(name="student_id", unique=true) private String studentId;
    @Column(name="legal_name") private String legalName;
    @Column(nullable=false) private String nickname;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountStatus status;
    @Column(name="department_code") private String departmentCode;
    @Column(name="section_code") private String sectionCode;
    @Column(name="academic_year") private Integer academicYear;
    @Column(name="token_version", nullable=false) private int tokenVersion;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    @Version private long version;
    protected User() {}
    public User(String loginId, String studentId, String legalName, String nickname, String passwordHash, Role role,
                AccountStatus status, String departmentCode, String sectionCode, Integer academicYear) {
        this.id=UUID.randomUUID(); this.loginId=loginId; this.studentId=studentId; this.legalName=legalName;
        this.nickname=nickname; this.passwordHash=passwordHash; this.role=role; this.status=status;
        this.departmentCode=departmentCode; this.sectionCode=sectionCode; this.academicYear=academicYear;
        this.createdAt=Instant.now(); this.updatedAt=this.createdAt;
    }
    public UUID getId(){return id;} public String getLoginId(){return loginId;} public String getStudentId(){return studentId;}
    public String getLegalName(){return legalName;} public String getNickname(){return nickname;} public String getPasswordHash(){return passwordHash;}
    public Role getRole(){return role;} public AccountStatus getStatus(){return status;} public String getDepartmentCode(){return departmentCode;}
    public String getSectionCode(){return sectionCode;} public Integer getAcademicYear(){return academicYear;} public int getTokenVersion(){return tokenVersion;}
    public void approve(){status=AccountStatus.ACTIVE; updatedAt=Instant.now();}
    public void reject(){status=AccountStatus.REJECTED; updatedAt=Instant.now();}
    public void ban(){status=AccountStatus.BANNED; tokenVersion++; updatedAt=Instant.now();}
    public void unban(){status=AccountStatus.ACTIVE; tokenVersion++; updatedAt=Instant.now();}
    public void changePassword(String encoded){passwordHash=encoded; tokenVersion++; updatedAt=Instant.now();}
}
