package com.shyam.awstest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

public class AmazonS3Test {

	private static final String awsKey = "CHANGEMEKEY";
	private static final String awsSecret = "CHANGEMESECRET";
//	key id eg: e7f17ef1-9523-4468-bc3b-5f02decf61f2
	private static final String encryptionKey = "YOUR KEY ID";
	private static final String bucketName= "YOURBUCKETNAME";
	
	
	public void uploadEncryptedToS3(String fileName,byte[] uploadFileData,String contentType){
		
        KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(encryptionKey);
        AmazonS3EncryptionClient encryptionClient = new AmazonS3EncryptionClient(getAwsCredentials(), materialProvider,
                new CryptoConfiguration());
        
        //Set the meta data for the uploading file object
        ObjectMetadata uploadMetaData = new ObjectMetadata();
        uploadMetaData.setContentType(contentType);
        uploadMetaData.setContentLength(uploadFileData.length);
        
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName,
                new ByteArrayInputStream(uploadFileData), uploadMetaData);
        
        encryptionClient.putObject(request);

		
	}
	
	public boolean checkS3Entry(String fileName){
		AmazonS3Client client = new AmazonS3Client(getAwsCredentials());
		try{
			client.getObject(bucketName, fileName);
		}catch(AmazonS3Exception s3Excpetion){
			//The file is not found
			return false;
		}
		//this means the file is found
		return true;
	}
	
	public byte[] downloadDecryptedFile(String fileName) throws IOException{
		KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(encryptionKey);
		AmazonS3EncryptionClient encryptionClient = new AmazonS3EncryptionClient(getAwsCredentials(), materialProvider,
                new CryptoConfiguration());
		S3Object object = encryptionClient.getObject(bucketName, fileName);
		return IOUtils.toByteArray(object.getObjectContent());
		
	}
	
	private AWSCredentials getAwsCredentials(){
		return new BasicAWSCredentials(awsKey, awsSecret);
	}
	
}
