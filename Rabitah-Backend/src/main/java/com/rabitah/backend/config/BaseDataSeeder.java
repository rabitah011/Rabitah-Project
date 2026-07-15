package com.rabitah.backend.config;

import com.rabitah.backend.academic.*;
import com.rabitah.backend.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BaseDataSeeder implements ApplicationRunner {
 private final StudentRosterRepository roster; private final UserRepository users; private final PasswordEncoder passwords; private final JdbcTemplate jdbc; private final int count; private final String adminPassword; private final String demoPassword;
 public BaseDataSeeder(StudentRosterRepository roster,UserRepository users,PasswordEncoder passwords,JdbcTemplate jdbc,@Value("${rabitah.seed.students-per-section-year:30}") int count,@Value("${RABITAH_SYSTEM_ADMIN_PASSWORD:}") String adminPassword,@Value("${RABITAH_DEMO_PASSWORD:}") String demoPassword){this.roster=roster;this.users=users;this.passwords=passwords;this.jdbc=jdbc;this.count=count;this.adminPassword=adminPassword;this.demoPassword=demoPassword;}
 @Override @Transactional public void run(ApplicationArguments args){
  for(String d:new String[]{"CSE","EEE","CEE"}) {jdbc.update("insert into departments(code,name) values (?,?) on conflict do nothing",d,name(d));for(String s:new String[]{"A","B"})jdbc.update("insert into department_sections(department_code,section_code) values (?,?) on conflict do nothing",d,s);jdbc.update("insert into academic_programs(department_code,name) values (?,?) on conflict do nothing",d,"BSc in "+name(d));}
  if(users.findByLoginIdIgnoreCase("SYSADMIN").isEmpty()){if(adminPassword.length()<8)throw new IllegalStateException("RABITAH_SYSTEM_ADMIN_PASSWORD must be configured with at least 8 characters");users.save(new User("SYSADMIN",null,null,"System Admin",passwords.encode(adminPassword),Role.SYSTEM_ADMIN,AccountStatus.ACTIVE,null,null,null));}
  if(roster.count()==0) for(String d:new String[]{"CSE","EEE","CEE"}) for(int y=1;y<=4;y++) for(String s:new String[]{"A","B"}) for(int n=1;n<=count;n++) {String serial="%03d".formatted(n); roster.save(new StudentRoster(d+y+s+serial,"Student "+d+" "+y+" "+s+" "+serial,d,s,y));}
  for(String d:new String[]{"CSE","EEE","CEE"}) for(int y=1;y<=4;y++) for(String s:new String[]{"A","B"}) jdbc.update("insert into community_rooms(id,department_code,section_code,academic_year,name) values (gen_random_uuid(),?,?,?,?) on conflict(department_code,section_code,academic_year) do nothing",d,s,y,d+" Year "+y+" Section "+s);
  seedCourses();
  if(!demoPassword.isBlank()) seedDemo();
 }
 private void seedCourses(){for(String d:new String[]{"CSE","EEE","CEE"}){java.util.UUID program=jdbc.queryForObject("select id from academic_programs where department_code=? limit 1",java.util.UUID.class,d);for(int y=1;y<=4;y++)jdbc.update("insert into courses(department_code,program_id,course_code,course_name,academic_year,semester) values(?,?,?,?,?,?) on conflict do nothing",d,program,d+y+"01",name(d)+" Year "+y,y,1);}}
 private void seedDemo(){seedUser("CSE1A003","Campus Coder","CSE","A",1);seedUser("EEE2B003","Circuit Sage","EEE","B",2);seedUser("CEE3A003","Civil Scholar","CEE","A",3);User admin=users.findByLoginIdIgnoreCase("SYSADMIN").orElseThrow();if(jdbc.queryForObject("select count(*) from posts",Long.class)==0){for(int i=1;i<=6;i++)jdbc.update("insert into posts(id,author_id,body,visibility,status,department_code,section_code,academic_year,created_at,updated_at) values(gen_random_uuid(),?,?,?,'APPROVED',null,null,null,now()-?*interval '1 hour',now())",admin.getId(),"Welcome to Rabitah campus update "+i,"PUBLIC",i);for(String d:new String[]{"CSE","EEE","CEE"})jdbc.update("insert into notices(id,department_code,title,body,published_by,notice_type,published_at,updated_at) values(gen_random_uuid(),?,?,?,?,?,now(),now())",d,d+" Academic Notice","Official academic information for "+d+" students.",admin.getId(),"ACADEMIC");}for(String login:new String[]{"CSE1A003","EEE2B003","CEE3A003"}){User u=users.findByLoginIdIgnoreCase(login).orElseThrow();java.util.UUID room=jdbc.queryForObject("select id from community_rooms where department_code=? and section_code=? and academic_year=?",java.util.UUID.class,u.getDepartmentCode(),u.getSectionCode(),u.getAcademicYear());jdbc.update("insert into community_messages(room_id,author_id,body) select ?,?,? where not exists(select 1 from community_messages where room_id=?)",room,admin.getId(),"Welcome to your official community room.",room);}}
 private void seedUser(String id,String nickname,String dept,String section,int year){if(users.findByLoginIdIgnoreCase(id).isEmpty())users.saveAndFlush(new User(id,id,"Student "+dept+" "+year+" "+section+" "+id.substring(id.length()-3),nickname,passwords.encode(demoPassword),Role.STUDENT,AccountStatus.ACTIVE,dept,section,year));}
 private String name(String d){return switch(d){case "CSE"->"Computer Science and Engineering";case "EEE"->"Electrical and Electronic Engineering";default->"Civil Engineering";};}
}
