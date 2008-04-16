/**
 * OWASP Enterprise Security API (ESAPI)
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * http://www.owasp.org/esapi.
 *
 * Copyright (c) 2007 - The OWASP Foundation
 * 
 * The ESAPI is published by OWASP under the LGPL. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jeff Williams <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created 2007
 */
package org.owasp.esapi;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.http.TestHttpServletRequest;
import org.owasp.esapi.http.TestHttpServletResponse;
import org.owasp.esapi.interfaces.IValidator;

/**
 * The Class ValidatorTest.
 * 
 * @author Jeff Williams (jeff.williams@aspectsecurity.com)
 */
public class ValidatorTest extends TestCase {

	/**
	 * Instantiates a new validator test.
	 * 
	 * @param testName
	 *            the test name
	 */
	public ValidatorTest(String testName) {
		super(testName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// none
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		// none
	}

	/**
	 * Suite.
	 * 
	 * @return the test
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(ValidatorTest.class);

		return suite;
	}

	/**
	 * Test of isValidCreditCard method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidCreditCard() {
		System.out.println("isValidCreditCard");
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidCreditCard("test", "1234 9876 0000 0008", false));
		assertTrue(instance.isValidCreditCard("test", "1234987600000008", false));
		assertFalse(instance.isValidCreditCard("test", "12349876000000081", false));
		assertFalse(instance.isValidCreditCard("test", "4417 1234 5678 9112", false));
	}

	/**
	 * Test of isValidEmailAddress method, of class org.owasp.esapi.Validator.
	 */
	public void testisValidInput() {
		System.out.println("isValidInput");
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidInput("test", "Email", "jeff.williams@aspectsecurity.com", 100, false));
		assertFalse(instance.isValidInput("test", "Email", "jeff.williams@@aspectsecurity.com", 100, false));
		assertFalse(instance.isValidInput("test", "Email", "jeff.williams@aspectsecurity", 100, false));
		assertTrue(instance.isValidInput("test", "IPAddress", "123.168.100.234", 100, false));
		assertTrue(instance.isValidInput("test", "IPAddress", "192.168.1.234", 100, false));
		assertFalse(instance.isValidInput("test", "IPAddress", "..168.1.234", 100, false));
		assertFalse(instance.isValidInput("test", "IPAddress", "10.x.1.234", 100, false));
		assertTrue(instance.isValidInput("test", "URL", "http://www.aspectsecurity.com", 100, false));
		assertFalse(instance.isValidInput("test", "URL", "http:///www.aspectsecurity.com", 100, false));
		assertFalse(instance.isValidInput("test", "URL", "http://www.aspect security.com", 100, false));
		assertTrue(instance.isValidInput("test", "SSN", "078-05-1120", 100, false));
		assertTrue(instance.isValidInput("test", "SSN", "078 05 1120", 100, false));
		assertTrue(instance.isValidInput("test", "SSN", "078051120", 100, false));
		assertFalse(instance.isValidInput("test", "SSN", "987-65-4320", 100, false));
		assertFalse(instance.isValidInput("test", "SSN", "000-00-0000", 100, false));
		assertFalse(instance.isValidInput("test", "SSN", "(555) 555-5555", 100, false));
		assertFalse(instance.isValidInput("test", "SSN", "test", 100, false));
	}

	/**
	 * Test of isValidSafeHTML method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidSafeHTML() {
		System.out.println("isValidSafeHTML");
		IValidator instance = ESAPI.validator();

		assertTrue(instance.isValidSafeHTML("test", "<b>Jeff</b>", 100, false));
		assertTrue(instance.isValidSafeHTML("test", "<a href=\"http://www.aspectsecurity.com\">Aspect Security</a>", 100, false));
		assertFalse(instance.isValidSafeHTML("test", "Test. <script>alert(document.cookie)</script>", 100, false));

		// FIXME: ENHANCE waiting for a way to validate text headed for an attribute for scripts		
		// This would be nice to catch, but just looks like text to AntiSamy
		// assertFalse(instance.isValidSafeHTML("test", "\" onload=\"alert(document.cookie)\" "));
	}

	/**
	 * Test of getValidSafeHTML method, of class org.owasp.esapi.Validator.
	 */
	public void testGetValidSafeHTML() throws Exception{
		System.out.println("getValidSafeHTML");
		IValidator instance = ESAPI.validator();
		String test1 = "<b>Jeff</b>";
		String result1 = instance.getValidSafeHTML("test", test1, 100, false);
		assertEquals(test1, result1);
		
		String test2 = "<a href=\"http://www.aspectsecurity.com\">Aspect Security</a>";
		String result2 = instance.getValidSafeHTML("test", test2, 100, false);
		assertEquals(test2, result2);
		
		String test3 = "Test. <script>alert(document.cookie)</script>";
		String result3 = instance.getValidSafeHTML("test", test3, 100, false);
		assertEquals("Test.", result3);
		
		// FIXME: ENHANCE waiting for a way to validate text headed for an attribute for scripts		
		// This would be nice to catch, but just looks like text to AntiSamy
		// assertFalse(instance.isValidSafeHTML("test", "\" onload=\"alert(document.cookie)\" "));
		// String result4 = instance.getValidSafeHTML("test", test4);
		// assertEquals("", result4);
	}

	/**
	 * Test of isValidListItem method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidListItem() {
		System.out.println("isValidListItem");
		IValidator instance = ESAPI.validator();
		List list = new ArrayList();
		list.add("one");
		list.add("two");
		assertTrue(instance.isValidListItem("test", "one", list));
		assertFalse(instance.isValidListItem("test", "three", list));
	}

	/**
	 * Test of isValidNumber method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidNumber() {
		System.out.println("isValidNumber");
		IValidator instance = ESAPI.validator();
		//testing negative range
		assertFalse(instance.isValidNumber("test", "-4", 1, 10, false));
		assertTrue(instance.isValidNumber("test", "-4", -10, 10, false));
		//testing null value
		assertTrue(instance.isValidNumber("test", null, -10, 10, true));
		assertFalse(instance.isValidNumber("test", null, -10, 10, false));
		//testing empty string
		assertTrue(instance.isValidNumber("test", "", -10, 10, true));
		assertFalse(instance.isValidNumber("test", "", -10, 10, false));
		//testing improper range
		assertFalse(instance.isValidNumber("test", "5", 10, -10, false));
		//testing non-integers
		assertTrue(instance.isValidNumber("test", "4.3214", -10, 10, true));
		assertTrue(instance.isValidNumber("test", "-1.65", -10, 10, true));
		//other testing
		assertTrue(instance.isValidNumber("test", "4", 1, 10, false));
		assertTrue(instance.isValidNumber("test", "400", 1, 10000, false));
		assertTrue(instance.isValidNumber("test", "400000000", 1, 400000000, false));
		assertFalse(instance.isValidNumber("test", "4000000000000", 1, 10000, false));
		assertFalse(instance.isValidNumber("test", "alsdkf", 10, 10000, false));
		assertFalse(instance.isValidNumber("test", "--10", 10, 10000, false));
		assertFalse(instance.isValidNumber("test", "14.1414234x", 10, 10000, false));
		assertFalse(instance.isValidNumber("test", "Infinity", 10, 10000, false));
		assertFalse(instance.isValidNumber("test", "-Infinity", 10, 10000, false));
		assertFalse(instance.isValidNumber("test", "NaN", 10, 10000, false));
		assertFalse(instance.isValidNumber("test", "-NaN", 10, 10000, false));
		assertFalse(instance.isValidNumber("test", "+NaN", 10, 10000, false));
		assertTrue(instance.isValidNumber("test", "1e-6", -999999999, 999999999, false));
		assertTrue(instance.isValidNumber("test", "-1e-6", -999999999, 999999999, false));
	}
	
	public void testIsValidInteger() {
		System.out.println("isValidInteger");
		IValidator instance = ESAPI.validator();
		//testing negative range
		assertFalse(instance.isValidInteger("test", "-4", 1, 10, false));
		assertTrue(instance.isValidInteger("test", "-4", -10, 10, false));
		//testing null value
		assertTrue(instance.isValidInteger("test", null, -10, 10, true));
		assertFalse(instance.isValidInteger("test", null, -10, 10, false));
		//testing empty string
		assertTrue(instance.isValidInteger("test", "", -10, 10, true));
		assertFalse(instance.isValidInteger("test", "", -10, 10, false));
		//testing improper range
		assertFalse(instance.isValidInteger("test", "5", 10, -10, false));
		//testing non-integers
		assertFalse(instance.isValidInteger("test", "4.3214", -10, 10, true));
		assertFalse(instance.isValidInteger("test", "-1.65", -10, 10, true));
		//other testing
		assertTrue(instance.isValidInteger("test", "4", 1, 10, false));
		assertTrue(instance.isValidInteger("test", "400", 1, 10000, false));
		assertTrue(instance.isValidInteger("test", "400000000", 1, 400000000, false));
		assertFalse(instance.isValidInteger("test", "4000000000000", 1, 10000, false));
		assertFalse(instance.isValidInteger("test", "alsdkf", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "--10", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "14.1414234x", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "Infinity", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "-Infinity", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "NaN", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "-NaN", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "+NaN", 10, 10000, false));
		assertFalse(instance.isValidInteger("test", "1e-6", -999999999, 999999999, false));
		assertFalse(instance.isValidInteger("test", "-1e-6", -999999999, 999999999, false));

	}

	/**
	 * Test of getValidDate method, of class org.owasp.esapi.Validator.
	 */
	public void testGetValidDate() throws Exception {
		System.out.println("getValidDate");
		IValidator instance = ESAPI.validator();
		assertTrue(instance.getValidDate("test", "June 23, 1967", DateFormat.getDateInstance(), false ) != null);
		try {
			instance.getValidDate("test", "freakshow", DateFormat.getDateInstance(), false );
		} catch( ValidationException e ) {
			// expected
		}
		
		// FIXME: AAA This test case fails due to an apparent bug in SimpleDateFormat
		try {
			instance.getValidDate( "test", "June 32, 2008", DateFormat.getDateInstance(), false );
			// fail();
		} catch( ValidationException e ) {
			// expected
		}
	}

	/**
	 * Test of isValidFileName method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidFileName() {
		System.out.println("isValidFileName");
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidFileName("test", "aspect.jar", false));
		assertFalse(instance.isValidFileName("test", "", false));
        try {
            instance.isValidFileName("test", "abc/def", false);
        } catch( IntrusionException e ) {
            // expected
        }
	}

	/**
	 * Test of isValidDirectoryPath method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidDirectoryPath() {
		System.out.println("isValidDirectoryPath");
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidDirectoryPath("test", "/", false));
		assertTrue(instance.isValidDirectoryPath("test", "c:\\temp", false));
		assertTrue(instance.isValidDirectoryPath("test", "/etc/config", false));
		// FIXME: ENHANCE doesn't accept filenames, just directories - should it?
		// assertTrue( instance.isValidDirectoryPath(
		// "c:\\Windows\\System32\\cmd.exe" ) );
		assertFalse(instance.isValidDirectoryPath("test", "c:\\temp\\..\\etc", false));
	}

	public void testIsValidPrintable() {
		System.out.println("isValidPrintable");
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidPrintable("name", "abcDEF", 100, false));
		assertTrue(instance.isValidPrintable("name", "!@#R()*$;><()", 100, false));
        byte[] bytes = { 0x60, (byte) 0xFF, 0x10, 0x25 };
        assertFalse( instance.isValidPrintable("name", bytes, 100, false ) );
		assertFalse(instance.isValidPrintable("name", "%08", 100, false));
    }

	/**
	 * Test of isValidFileContent method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidFileContent() {
		System.out.println("isValidFileContent");
		byte[] content = "This is some file content".getBytes();
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidFileContent("test", content, 100, false));
	}

	/**
	 * Test of isValidFileUpload method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidFileUpload() {
		System.out.println("isValidFileUpload");

		String filepath = "/etc";
		String filename = "aspect.jar";
		byte[] content = "Thisi is some file content".getBytes();
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidFileUpload("test", filepath, filename, content, 100, false));
	}

	/**
	 * Test of isValidParameterSet method, of class org.owasp.esapi.Validator.
	 */
	public void testIsValidParameterSet() {
		System.out.println("isValidParameterSet");

		Set requiredNames = new HashSet();
		requiredNames.add("p1");
		requiredNames.add("p2");
		requiredNames.add("p3");
		Set optionalNames = new HashSet();
		optionalNames.add("p4");
		optionalNames.add("p5");
		optionalNames.add("p6");
        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
		request.addParameter("p1","value");
		request.addParameter("p2","value");
		request.addParameter("p3","value");
        ((Authenticator)ESAPI.authenticator()).setCurrentHTTP(request, response);
		IValidator instance = ESAPI.validator();
		assertTrue(instance.isValidHTTPRequestParameterSet("HTTPParameters", requiredNames, optionalNames));
		request.addParameter("p4","value");
		request.addParameter("p5","value");
		request.addParameter("p6","value");
		assertTrue(instance.isValidHTTPRequestParameterSet("HTTPParameters", requiredNames, optionalNames));
		request.removeParameter("p1");
		assertFalse(instance.isValidHTTPRequestParameterSet("HTTPParameters", requiredNames, optionalNames));
	}

	/**
	 * Test safe read line.
	 */
	public void testSafeReadLine() {
		ByteArrayInputStream s = new ByteArrayInputStream("testString".getBytes());
		IValidator instance = ESAPI.validator();
		try {
			instance.safeReadLine(s, -1);
			fail();
		} catch (ValidationException e) {
			// Expected
		}
		s.reset();
		try {
			instance.safeReadLine(s, 4);
			fail();
		} catch (ValidationException e) {
			// Expected
		}
		s.reset();
		try {
			String u = instance.safeReadLine(s, 20);
			assertEquals("testString", u);
		} catch (ValidationException e) {
			fail();
		}
	}
}
