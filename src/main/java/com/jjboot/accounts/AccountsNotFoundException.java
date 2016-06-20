package com.jjboot.accounts;

public class AccountsNotFoundException extends RuntimeException {

    Long id;

    public AccountsNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
