package com.example.demo.commons.config.security;

import com.example.demo.base.enums.EStatus;
import com.example.demo.commons.entity.Admin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * SpringSecurity中的用户实体类
 *
 * @author
 * @date
 */
public class SecurityUser implements UserDetails {
    /**
     *
     */
    public Admin admin;
    private static final long serialVersionUID = 1L;

//    private final String uid;
//    private final String userName;
//    private final String passWord;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(Admin admin, Collection<? extends GrantedAuthority> authorities){
            this.admin=admin;
        this.authorities = authorities;
        enabled=(admin.getStatus() == EStatus.ENABLE);
    }

    /**
     * 返回分配给用户的角色列表
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return admin.getPassWord();
    }

    @Override
    public String getUsername() {
        return admin.getUserName();
    }


    /**
     * 账户是否激活
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 账户是否未过期
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否未过期
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}

