package com.jgermaine.fyp.rest.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jgermaine.fyp.rest.config.WebApplication;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebApplication.class)
public class SpringUtilTest {

	@Test
	public void testIsNullOrEmptySuccess() {
		assertTrue(StringUtil.isNotNullOrEmpty("value"));
	}
	
	@Test
	public void testIsNullOrEmptyFailureNull() {
		assertFalse(StringUtil.isNotNullOrEmpty(null));
	}
	
	@Test
	public void testIsNullOrEmptyFailureEmpty() {
		assertFalse(StringUtil.isNotNullOrEmpty(""));
	}
}
