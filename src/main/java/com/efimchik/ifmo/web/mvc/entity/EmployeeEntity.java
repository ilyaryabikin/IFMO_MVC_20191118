package com.efimchik.ifmo.web.mvc.entity;

import com.efimchik.ifmo.web.mvc.domain.Position;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Employee")
public class EmployeeEntity {

    @Id
    private Long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "middlename")
    private String middleName;

    @Enumerated(EnumType.STRING)
    private Position position;

    @OneToOne
    @JoinColumn(name = "manager")
    private EmployeeEntity manager;

    @Column(name = "hiredate")
    private LocalDate hired;

    private BigDecimal salary;

    @ManyToOne
    @JoinColumn(name = "department")
    private DepartmentEntity department;

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Position getPosition() {
        return position;
    }

    public EmployeeEntity getManager() {
        return manager;
    }

    public LocalDate getHired() {
        return hired;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }
}
