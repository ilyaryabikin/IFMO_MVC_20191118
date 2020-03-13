package com.efimchik.ifmo.web.mvc.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "Department")
public class DepartmentEntity {

    @Id
    private Long id;

    private String name;

    private String location;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
