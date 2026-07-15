package com.rabitah.frontend.session;
import com.rabitah.frontend.model.AuthResponse;
public final class SessionManager {private volatile AuthResponse session;public void open(AuthResponse value){session=value;}public String accessToken(){return session==null?null:session.accessToken();}public AuthResponse current(){return session;}public boolean authenticated(){return session!=null;}public void clear(){session=null;}}
