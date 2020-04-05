package com.aoher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.aoher.util.Constants.STATUS_NOT_FOUND;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"ehpath=ehcache.xml"})
public class AppTest {

    private static final String VALUE = "5001";
    private static final String URI = format("/api/cache?value=%s", VALUE);

    private static final String SERVER_PORT_FORMAT = "--server.port=%d";
    private static final String EH_CACHE_PATH = "--ehpath=ehcache.xml";

    @Test
    public void testPutAndGetCache() throws Exception {
        String[] args = new String[] { format(SERVER_PORT_FORMAT, 3003), EH_CACHE_PATH };

        WebApplicationContext applicationContext = (WebApplicationContext) SpringApplication.run(App.class, args);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(URI)).andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(VALUE, content);
    }

    @Test
    public void testDistributedPutAndGetCache() throws Exception {
        String[] args = new String[] { format(SERVER_PORT_FORMAT, 3001), EH_CACHE_PATH };
        String[] args1 = new String[] { format(SERVER_PORT_FORMAT, 3002), EH_CACHE_PATH };

        WebApplicationContext appContext = (WebApplicationContext) SpringApplication.run(App.class, args);
        WebApplicationContext appContext1 = (WebApplicationContext) SpringApplication.run(App.class, args1);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();
        MockMvc mockMvc1 = MockMvcBuilders.webAppContextSetup(appContext1).build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI));
        MvcResult getResult = mockMvc1.perform(MockMvcRequestBuilders.get(URI)).andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertEquals(VALUE, content);
    }

    @Test
    public void testCacheTTL() throws Exception {
        String[] args = new String[] { format(SERVER_PORT_FORMAT, 3004), EH_CACHE_PATH};

        WebApplicationContext appContext = (WebApplicationContext) SpringApplication.run(App.class, args);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI));
        Thread.sleep(5000);
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get(URI)).andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertEquals(STATUS_NOT_FOUND, content);
    }
}