package com.jjboot.accounts;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class AccountService {

//    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public Account createAccount(AccountDto.Create dto) {
        Account account = modelMapper.map(dto, Account.class);
        //check valid username
        String username = dto.getUsername();
        if (repository.findByUsername(username) != null ) {
            log.error("user duplicated exception {}", username);
            throw new UserDuplicatedException(username);
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        // password hashing
        Date now = new Date();
        account.setJoined(now);
        account.setUpdated(now);

        return repository.save(account);
    }

    public Account updateAccount(Long id, AccountDto.Update updateDto) {
        Account account = getAccount(id);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setFullName(updateDto.getFullName());
        return repository.save(account);
    }

    public Account getAccount(Long id) {
        Account account = repository.findOne(id);
        if (account == null) {
            throw new AccountsNotFoundException(id);
        }
        return account;
    }

    public void deleteAccount(Long id) {
        repository.delete(getAccount(id));

    }
}
