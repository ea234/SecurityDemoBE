
package de.ea234.securitydemo.security.user.entity;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "demo_auth_authorities")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String authority;

    @ManyToOne(optional = false)
    private Benutzer userEntity;

    public Authority() {
    }

    public Authority(String authority, Benutzer userEntity) {
        this.authority = authority;
        this.userEntity = userEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Benutzer getUser() {
        return userEntity;
    }

    public void setUser(Benutzer user) {
        this.userEntity = user;
    }
}
