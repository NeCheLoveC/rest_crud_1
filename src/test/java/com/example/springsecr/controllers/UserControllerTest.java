package com.example.springsecr.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class UserControllerTest {
    private final String END_POINT_START = "/users";
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsersWithoutPredicate() throws Exception {
        final String REQUEST_END_POINT = END_POINT_START;

        this.mockMvc.perform(get(REQUEST_END_POINT))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json")
                );
    }
}