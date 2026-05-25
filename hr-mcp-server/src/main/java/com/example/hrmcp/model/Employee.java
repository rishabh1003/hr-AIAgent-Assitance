package com.example.hrmcp.model;
import jakarta.persistence.*;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String employeeId;
    private String name;
    private Integer ptoBalance;

    public String getEmployeeId() { return employeeId; }
    public Integer getPtoBalance() { return ptoBalance; }
}
