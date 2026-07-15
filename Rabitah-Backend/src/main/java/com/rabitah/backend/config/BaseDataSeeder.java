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
 private final StudentRosterRepository roster; private final UserRepository users; private final PasswordEncoder passwords; private final JdbcTemplate jdbc; private final int count; private final String adminPassword;
 public BaseDataSeeder(StudentRosterRepository roster,UserRepository users,PasswordEncoder passwords,JdbcTemplate jdbc,@Value("${rabitah.seed.students-per-section-year:30}") int count,@Value("${RABITAH_SYSTEM_ADMIN_PASSWORD:}") String adminPassword){this.roster=roster;this.users=users;this.passwords=passwords;this.jdbc=jdbc;this.count=count;this.adminPassword=adminPassword;}
 @Override @Transactional public void run(ApplicationArguments args){
  for(String d:new String[]{"CSE","EEE","CEE"}) {jdbc.update("insert into departments(code,name) values (?,?) on conflict do nothing",d,name(d));for(String s:new String[]{"A","B"})jdbc.update("insert into department_sections(department_code,section_code) values (?,?) on conflict do nothing",d,s);jdbc.update("insert into academic_programs(department_code,name) values (?,?) on conflict do nothing",d,"BSc in "+name(d));}
  if(users.findByLoginIdIgnoreCase("SYSADMIN").isEmpty()){if(adminPassword.length()<8)throw new IllegalStateException("RABITAH_SYSTEM_ADMIN_PASSWORD must be configured with at least 8 characters");users.save(new User("SYSADMIN",null,null,"System Admin",passwords.encode(adminPassword),Role.SYSTEM_ADMIN,AccountStatus.ACTIVE,null,null,null));}
  if(roster.count()==0) for(String d:new String[]{"CSE","EEE","CEE"}) for(int y=1;y<=4;y++) for(String s:new String[]{"A","B"}) for(int n=1;n<=count;n++) {String serial="%03d".formatted(n); roster.save(new StudentRoster(d+y+s+serial,"Student "+d+" "+y+" "+s+" "+serial,d,s,y));}
  for(String d:new String[]{"CSE","EEE","CEE"}) for(int y=1;y<=4;y++) for(String s:new String[]{"A","B"}) jdbc.update("insert into community_rooms(id,department_code,section_code,academic_year,name) values (gen_random_uuid(),?,?,?,?) on conflict(department_code,section_code,academic_year) do nothing",d,s,y,d+" Year "+y+" Section "+s);
 }
 private String name(String d){return switch(d){case "CSE"->"Computer Science and Engineering";case "EEE"->"Electrical and Electronic Engineering";default->"Civil Engineering";};}
}
