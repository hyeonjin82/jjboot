package com.jjboot.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjboot.JjbootApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JjbootApplication.class)
@WebAppConfiguration
@Transactional
public class AccountControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountService service;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    public void createAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();

        ResultActions result = mockMvc.perform(post("/accounts")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(createDto)));

        result.andDo(print());
        result.andExpect(status().isCreated());
        //{"id":1,"username":"jinjin","fullName":null,"joined":1466393971863,"updated":1466393971863}
        result.andExpect(jsonPath("$.username", is("jinjin")));

        // JSON Path

        //Test for Duplicated username
        result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.code", is("duplicated.username.exception")));
    }

    @Test
    public void createAccount_BadRequest() throws Exception {
        AccountDto.Create createDto = new AccountDto.Create();
        createDto.setUsername("  ");
        createDto.setPassword("123");

        ResultActions result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.code", is("bad.request")));
    }

    //twitch api, hateoas (Hypermedia as the Engine of Application State), URN

    // HATEOAS

    @Test
    public void getAccounts() throws Exception{
        AccountDto.Create createDto = accountCreateDto();
        service.createAccount(createDto);

        ResultActions result = mockMvc.perform(get("/accounts"));

        result.andDo(print());
        result.andExpect(status().isOk());
    }

    private AccountDto.Create accountCreateDto() {
        AccountDto.Create createDto = new AccountDto.Create();
        createDto.setUsername("jinjin");
        createDto.setPassword("abc4343");
        return createDto;
    }

    @Test
    public void getAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
        Account account = service.createAccount(createDto);

        ResultActions result = mockMvc.perform(get("/accounts/" + account.getId()));

        result.andDo(print());
        result.andExpect(status().isOk());
    }

    @Test
    public void updateAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
        Account account = service.createAccount(createDto);

        AccountDto.Update updateDto = new AccountDto.Update();
        updateDto.setFullName("leehyeo jin");
        updateDto.setPassword("abc434343");

        ResultActions result = mockMvc.perform(put("/accounts/" + account.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.fullName", is("leehyeo jin")));
//        result.andExpect(jsonPath("$.password", is("abc4343")));

    }

    @Test
    public void deleteAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
        Account account = service.createAccount(createDto);

        ResultActions result = mockMvc.perform(delete("/accounts/1111")
        .with(httpBasic(createDto.getUsername(), createDto.getPassword())));
        result.andDo(print());
        result.andExpect(status().isBadRequest());



        result = mockMvc.perform(delete("/accounts/" + account.getId())
                .with(httpBasic(createDto.getUsername(), createDto.getPassword())));
        result.andDo(print());
        result.andExpect(status().isNoContent());
    }



}