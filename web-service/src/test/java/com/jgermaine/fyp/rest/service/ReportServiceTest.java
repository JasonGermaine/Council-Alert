package com.jgermaine.fyp.rest.service;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;

public class ReportServiceTest {
	
	@Test
	public void testGetReport() {
		ReportServiceImpl reportService = new ReportServiceImpl();

		System.out.println("Testing Unit Test Setup");
		Report r = reportService.getReport();
		Assert.assertEquals(1, r.getId());
	}
}
