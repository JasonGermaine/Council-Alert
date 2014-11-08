package com.jgermaine.fyp.rest.service;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.jgermaine.fyp.rest.model.Report;

public class ReportServiceTest {
	
	@Test
	public void testGetReport() {
		ReportService reportService = new ReportService();

		System.out.println("Testing Unit Test Setup");
		Report r = reportService.getReport();
		Assert.assertEquals(1, r.getId());
	}
}
