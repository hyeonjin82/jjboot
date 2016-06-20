package com.jjboot.accounts;


import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AccountTest {

    @Test
    public void getterSetter() {
        Account account = new Account();
        account.setUsername("jin");
        account.setPassword("1234");
        assertThat(account.getUsername(), is("jin"));
    }
}