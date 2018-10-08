package com.lib.bibliosoft.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.*;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 3:23 PM. 9/28/2018
 * @Modify By:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getPrice() {

    }

    /**
     * Test the function with url
     * @throws Exception
     */
    @Test
    public void bookList() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/bookss")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string("abc"));
        //...we can test by this method
    }
}