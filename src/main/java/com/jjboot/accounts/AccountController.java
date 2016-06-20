package com.jjboot.accounts;

import com.jjboot.commons.ErrorResponse;
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

@RestController
public class AccountController {

    @Autowired
    private AccountService service;

    @Autowired
    private AccountRepository repository;

    @Autowired
    private ModelMapper modelMapper;

   @RequestMapping(value="/accounts", method = RequestMethod.POST)
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

    @RequestMapping(value="/accounts", method = RequestMethod.GET)
    public ResponseEntity getAccounts(Pageable pageable) {
        Page<Account> page = repository.findAll(pageable);
        List<AccountDto.Response> content = page.getContent()
                .parallelStream()
                .map(account -> modelMapper.map(account, AccountDto.Response.class))
                .collect(Collectors.toList());
        PageImpl<AccountDto.Response> result = new PageImpl<>(content, pageable, page.getTotalElements());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ExceptionHandler(UserDuplicatedException.class)
    public ResponseEntity handleUserDuplicationdException(UserDuplicatedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("[" + e.getUsername() + "] + duplicated username ");
        errorResponse.setCode("duplicated.username.exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
