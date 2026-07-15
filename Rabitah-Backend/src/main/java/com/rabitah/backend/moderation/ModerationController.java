package com.rabitah.backend.moderation;

import com.rabitah.backend.common.ApiException;
import com.rabitah.backend.security.CurrentUserService;
import com.rabitah.backend.user.Role;
import com.rabitah.backend.user.User;
import com.rabitah.backend.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/approvals")
public class ModerationController {
    private final JdbcTemplate jdbc;
    private final CurrentUserService currentUsers;
    private final UserRepository users;

    public ModerationController(JdbcTemplate jdbc, CurrentUserService currentUsers, UserRepository users) {
        this.jdbc = jdbc;
        this.currentUsers = currentUsers;
        this.users = users;
    }

    @GetMapping("/summary")
    public ApprovalSummary summary(Authentication auth) {
        requireAdmin(auth);
        return new ApprovalSummary(count("select count(*) from users where status='PENDING'"),
                count("select count(*) from posts where status='PENDING' and deleted_at is null"),
                count("select count(*) from question_papers where status='PENDING'"));
    }

    @GetMapping("/users")
    public List<UserRequest> pendingUsers(Authentication auth) {
        requireAdmin(auth);
        return jdbc.query("select id,student_id,legal_name,nickname,department_code,section_code,academic_year,created_at from users where status='PENDING' order by created_at",
                (rs,n)->new UserRequest(rs.getObject(1,UUID.class),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getObject(7,Integer.class),rs.getObject(8,java.time.OffsetDateTime.class).toInstant()));
    }

    @GetMapping("/posts")
    public List<PostRequest> pendingPosts(Authentication auth) {
        requireAdmin(auth);
        return jdbc.query("select p.id,p.body,p.created_at,u.nickname,u.student_id from posts p join users u on u.id=p.author_id where p.status='PENDING' and p.deleted_at is null order by p.created_at",
                (rs,n)->new PostRequest(rs.getObject(1,UUID.class),rs.getString(2),rs.getObject(3,java.time.OffsetDateTime.class).toInstant(),rs.getString(4),rs.getString(5)));
    }

    @GetMapping("/papers")
    public List<PaperRequest> pendingPapers(Authentication auth) {
        requireAdmin(auth);
        return jdbc.query("select qp.id,qp.title,qp.original_name,qp.exam_year,qp.created_at,c.course_code,u.nickname,u.student_id from question_papers qp join courses c on c.id=qp.course_id join users u on u.id=qp.uploaded_by where qp.status='PENDING' order by qp.created_at",
                (rs,n)->new PaperRequest(rs.getObject(1,UUID.class),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getObject(5,java.time.OffsetDateTime.class).toInstant(),rs.getString(6),rs.getString(7),rs.getString(8)));
    }

    @PutMapping("/users/{id}")
    @Transactional
    public Map<String,String> decideUser(@PathVariable UUID id,@Valid @RequestBody Decision decision,Authentication auth) {
        requireAdmin(auth); User user=users.findById(id).orElseThrow(()->notFound("USER_NOT_FOUND","User request not found"));
        if(decision.action().equals("APPROVE"))user.approve();else user.reject(); users.save(user);return Map.of("message","User request "+decision.action().toLowerCase()+"d");
    }

    @PutMapping("/posts/{id}")
    @Transactional
    public Map<String,String> decidePost(@PathVariable UUID id,@Valid @RequestBody Decision decision,Authentication auth) {
        User admin=requireAdmin(auth);int changed=jdbc.update("update posts set status=?,moderated_by=?,moderated_at=now(),rejection_reason=?,updated_at=now() where id=? and status='PENDING'",decision.action().equals("APPROVE")?"APPROVED":"REJECTED",admin.getId(),decision.reason(),id);if(changed==0)throw notFound("POST_REQUEST_NOT_FOUND","Pending post not found");return Map.of("message","Post "+decision.action().toLowerCase()+"d");
    }

    @PutMapping("/papers/{id}")
    @Transactional
    public Map<String,String> decidePaper(@PathVariable UUID id,@Valid @RequestBody Decision decision,Authentication auth) {
        User admin=requireAdmin(auth);int changed=jdbc.update("update question_papers set status=?,moderated_by=?,moderated_at=now(),rejection_reason=? where id=? and status='PENDING'",decision.action().equals("APPROVE")?"APPROVED":"REJECTED",admin.getId(),decision.reason(),id);if(changed==0)throw notFound("PAPER_REQUEST_NOT_FOUND","Pending paper not found");return Map.of("message","Paper "+decision.action().toLowerCase()+"d");
    }

    private User requireAdmin(Authentication auth){User u=currentUsers.require(auth);if(u.getRole()!=Role.SYSTEM_ADMIN)throw new ApiException(HttpStatus.FORBIDDEN,"ADMIN_REQUIRED","System administrator access is required");return u;}
    private long count(String sql){Long value=jdbc.queryForObject(sql,Long.class);return value==null?0:value;}
    private ApiException notFound(String code,String message){return new ApiException(HttpStatus.NOT_FOUND,code,message);}
    public record Decision(@Pattern(regexp="APPROVE|REJECT") String action,@Size(max=500) String reason){}
    public record ApprovalSummary(long users,long posts,long papers){}
    public record UserRequest(UUID id,String studentId,String legalName,String nickname,String department,String section,Integer academicYear,Instant requestedAt){}
    public record PostRequest(UUID id,String body,Instant createdAt,String authorNickname,String authorStudentId){}
    public record PaperRequest(UUID id,String title,String filename,int examYear,Instant createdAt,String courseCode,String authorNickname,String authorStudentId){}
}
