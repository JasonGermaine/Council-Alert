package com.jgermaine.fyp.rest.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "USER_TYPE")
@JsonIgnoreProperties(value={"password"})
public abstract class User {

	@Id
    @NotEmpty
	@Email
	@Length(max = 255)
    @Column(name="email")
	private String email;
	
	@Transient
	@Pattern(regexp="[A-Za-z0-9?.$%]*")
	@Length(max = 255)
	private String password;
	
	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Transient
    private String type;
        
    public void setType(String type) {
    	this.type = type;
    }
    
    public abstract String getType();

}
