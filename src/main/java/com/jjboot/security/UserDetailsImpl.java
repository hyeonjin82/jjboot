package com.jjboot.security;

import com.jjboot.accounts.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jin on 2016-06-20.
 */
public class UserDetailsImpl extends User {

    public UserDetailsImpl(Account account) {
        super(account.getUsername(), account.getPassword(), authorityies(account));
    }

    private static Collection<? extends GrantedAuthority> authorityies(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (account.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
//        Role Hierarchy
//        if (account.isAdmin()) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        } else {
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        }
        return authorities;
    }
}
