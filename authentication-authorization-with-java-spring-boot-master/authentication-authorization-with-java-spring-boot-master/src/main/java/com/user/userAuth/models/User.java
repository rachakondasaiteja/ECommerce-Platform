package com.user.userAuth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name="user")
@Getter
@Setter
public class User extends BaseModel{
    private String name;
    private String hashPassword;
    private String email;
    @ManyToMany
    private List<Role> roles;
    private boolean isEmailVarified;
}
