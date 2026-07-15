package com.rabitah.backend.academic;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class StudentRosterTest {@Test void normalizesCaseAndRepeatedSpaces(){var r=new StudentRoster("CSE1A001","Student CSE 1 A 001","CSE","A",1);assertTrue(r.matches("  STUDENT   cse 1 a 001 ","cse","a",1));assertFalse(r.matches("Student CSE 1 A 002","CSE","A",1));}}
