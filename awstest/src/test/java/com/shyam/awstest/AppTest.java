package com.shyam.awstest;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	String uploadFileName = "upload_encryption.txt";
    	 AmazonS3Test s3Test = new AmazonS3Test();
    	 String contentType = "text/plain";
    	 String uploadContect = "Uploading a text file to see if i can retreive it";
    	try{
	       
	       s3Test.uploadEncryptedToS3(uploadFileName, uploadContect.getBytes(),contentType);
	       
	       assertTrue(s3Test.checkS3Entry(uploadFileName));
	       
	       byte[] content = s3Test.downloadDecryptedFile(uploadFileName);
	       
	       
	       String returnString = new String(content);
	       
	       assertEquals(uploadContect, returnString);
	       
    	}catch(IOException e){
    		e.printStackTrace();
    		fail("Failed to execute s3 tests");
    	}
    	
       
       
    }
}
