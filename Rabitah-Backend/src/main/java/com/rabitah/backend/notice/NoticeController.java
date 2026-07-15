package com.rabitah.backend.notice;

import com.rabitah.backend.common.ApiException;
import com.rabitah.backend.security.CurrentUserService;
import com.rabitah.backend.user.Role;
import com.rabitah.backend.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/notices")
public class NoticeController {
 private final JdbcTemplate jdbc; private final CurrentUserService current;
 public NoticeController(JdbcTemplate jdbc,CurrentUserService current){this.jdbc=jdbc;this.current=current;}
 @GetMapping public List<NoticeView> list(@RequestParam(defaultValue="") String q,Authentication auth){User u=current.require(auth);String like="%"+q.toLowerCase()+"%";return jdbc.query("""
 select id,notice_type,title,body,department_code,academic_year,section_code,published_at
 from notices where deleted_at is null and (expires_at is null or expires_at>now())
 and (lower(title) like ? or lower(body) like ?)
 and (?='SYSTEM_ADMIN' or department_code is null or department_code=?)
 and (academic_year is null or academic_year=?) and (section_code is null or section_code=?)
 order by published_at desc""",(rs,n)->new NoticeView(rs.getObject(1,UUID.class),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),(Integer)rs.getObject(6),rs.getString(7),rs.getObject(8,java.time.OffsetDateTime.class).toInstant()),like,like,u.getRole().name(),u.getDepartmentCode(),u.getAcademicYear(),u.getSectionCode());}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional public NoticeView create(@Valid @RequestBody NoticeRequest r,Authentication auth){User u=current.require(auth);if(u.getRole()!=Role.SYSTEM_ADMIN&&u.getRole()!=Role.DEPARTMENT_ADMIN)throw new ApiException(HttpStatus.FORBIDDEN,"FORBIDDEN","Only administrators may publish notices");String dept=u.getRole()==Role.DEPARTMENT_ADMIN?u.getDepartmentCode():blank(r.department());UUID id=UUID.randomUUID();jdbc.update("insert into notices(id,department_code,title,body,published_by,notice_type,academic_year,section_code,published_at,updated_at) values(?,?,?,?,?,?,?,?,now(),now())",id,dept,r.title().trim(),r.body().trim(),u.getId(),r.type(),r.academicYear(),blank(r.section()));return list("",auth).stream().filter(x->x.id().equals(id)).findFirst().orElseThrow();}
 private String blank(String s){return s==null||s.isBlank()?null:s;}
 public record NoticeRequest(@NotBlank @Size(max=200) String title,@NotBlank @Size(max=4000) String body,@Pattern(regexp="GENERAL|ACADEMIC|EXAM|EVENT|EMERGENCY") String type,String department,Integer academicYear,String section){}
 public record NoticeView(UUID id,String type,String title,String body,String department,Integer academicYear,String section,Instant publishedAt){}
}
