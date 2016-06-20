package com.jjboot.accounts;

import com.jjboot.commons.ErrorResponse;
import org.hibernate.hql.internal.ast.ErrorReporter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class AccountController {

    @Autowired
    private AccountService service;

    @Autowired
    private AccountRepository repository;

    @Autowired
    private ModelMapper modelMapper;

   @RequestMapping(value="/accounts", method = POST)
    public ResponseEntity createAccount(@RequestBody @Valid AccountDto.Create create,
                                        BindingResult result) {
       if(result.hasErrors()){
           // Need to add error response
           ErrorResponse errorResponse = new ErrorResponse();
           errorResponse.setMessage("Wrong request");
           errorResponse.setCode("bad.request");
           // use the info of BindingResult
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
           // JSON Path
       }

       Account newAccount = service.createAccount(create);
       return new ResponseEntity<>(modelMapper.map(newAccount, AccountDto.Response.class), HttpStatus.CREATED);
   }

    @RequestMapping(value="/accounts", method = GET)
    @ResponseStatus(HttpStatus.OK)
    public PageImpl<AccountDto.Response> getAccounts(Pageable pageable) {
        Page<Account> page = repository.findAll(pageable);
        List<AccountDto.Response> content = page.getContent()
                .parallelStream()
                .map(account -> modelMapper.map(account, AccountDto.Response.class))
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @RequestMapping(value="/accounts/{id}", method = GET)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto.Response getAccount(@PathVariable Long id) {
        Account account =service.getAccount(id);
        return modelMapper.map(account, AccountDto.Response.class);
    }

    @RequestMapping(value="/accounts/{id}", method= PUT)
    public ResponseEntity updateAccount(@PathVariable Long id,
                                        @RequestBody @Valid AccountDto.Update updateDto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Account updatedAccount = service.updateAccount(id, updateDto);
        return new ResponseEntity<>(modelMapper.map(updatedAccount, AccountDto.Response.class), HttpStatus.OK);
    }

    @ExceptionHandler(UserDuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserDuplicationdException(UserDuplicatedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("[" + e.getUsername() + "] + duplicated username ");
        errorResponse.setCode("duplicated.username.exception");
        return errorResponse;
    }

    @ExceptionHandler(AccountsNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccountsNotFoundException (AccountsNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("[" + e.getId() + "] + not exist");
        errorResponse.setCode("account.not.found.exception");
        return errorResponse;
    }

}
