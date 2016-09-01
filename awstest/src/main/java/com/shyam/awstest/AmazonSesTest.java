package com.shyam.awstest;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.GetIdentityVerificationAttributesRequest;
import com.amazonaws.services.simpleemail.model.GetIdentityVerificationAttributesResult;
import com.amazonaws.services.simpleemail.model.IdentityVerificationAttributes;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.amazonaws.util.StringUtils;

public class AmazonSesTest {
	
	String fallbackSender = "do_not_reply@email.com";
	
	public static final String AWS_EMAIL_VERIFICATION_SUCCESS = "Success";
	
	private AmazonSimpleEmailServiceClient getEmailService(String awsKey,String awsSecret){
		AWSCredentials cred = new BasicAWSCredentials(awsKey,awsSecret);
		AmazonSimpleEmailServiceClient emailService = new AmazonSimpleEmailServiceClient(cred);
		return emailService;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.eagree.reportsbuilder.service.AwsEmailService#isEmailVerified(com.eagree.reportsbuilder.request.SendEmailRequest)
	 * This method verified if the email in the request is verified by SES or not
	 */
	public boolean isEmailVerified(SendEmailRequest emailRequest){
		
		AmazonSimpleEmailServiceClient emailService = getEmailService(emailRequest.getAwsKey(), emailRequest.getAwsSecret());
		List<String> verificationList = new ArrayList();
		verificationList.add(emailRequest.getFrom());
		
		//Create a verification request with the email from address
		GetIdentityVerificationAttributesRequest verificationRequest = new GetIdentityVerificationAttributesRequest();
		verificationRequest.setIdentities(verificationList);
		GetIdentityVerificationAttributesResult verificationResult = emailService.getIdentityVerificationAttributes(verificationRequest);
		
		//Get the verification result for the given email address
		IdentityVerificationAttributes emailVerificationResult = verificationResult.getVerificationAttributes().get(emailRequest.getFrom());
		
		//If the result is not null and the verification status is "Success" that means the email is verified
		boolean verificationStatus = (emailVerificationResult!=null&&AWS_EMAIL_VERIFICATION_SUCCESS.equals(emailVerificationResult.getVerificationStatus()));
		
		//logger.debug("Verification Status "+emailRequest.getFrom()+" "+verificationStatus);
		
		return verificationStatus;
	}

	public String sendEmail(SendEmailRequest emailRequest){
		
		//Check the request for sanity. throws exception if required data is not present
		emailRequest.sanityCheck();
		
		AmazonSimpleEmailServiceClient emailService = getEmailService(emailRequest.getAwsKey(), emailRequest.getAwsSecret());
		
		//Check if the email address is registered to send for the current aws credentials
		boolean exists = isEmailVerified(emailRequest);
		//logger.debug("Sender email verified with aws "+exists);
		System.out.println("Sender email verified with aws "+exists);
		if(!exists){
//			throw new EagreeHelperException(MessageConstants.EMAIL_NOT_REGISTERED);
			//if email is not verified using fallbackEmail as sender
			emailRequest.setFrom(fallbackSender);
		}
		
		Destination destination = new Destination().withToAddresses(emailRequest.getToList())
				.withCcAddresses(emailRequest.getCcList());
		
		// Create the subject and body of the message.
		Content subject = new Content().withData(emailRequest.getSubject());
		Content textBody = new Content().withData(emailRequest.getBodyHtml());
		Body body = new Body().withHtml(textBody);

		// Create a message with the specified subject and body.
		Message message = new Message().withSubject(subject).withBody(body);

		// Assemble the email.
		String from = emailRequest.getFrom();
		if(!StringUtils.isNullOrEmpty(emailRequest.getSenderName())){
			from = emailRequest.getSenderName() + " <" + from + ">";
		}
		com.amazonaws.services.simpleemail.model.SendEmailRequest request = new com.amazonaws.services.simpleemail.model.SendEmailRequest().withSource(from).withDestination(destination)
																				.withMessage(message);
		//Send the email
		SendEmailResult result = emailService.sendEmail(request);
		
		//If we dont get a message id throw an exception
		if(result.getMessageId()==null){
			throw new RuntimeException("Error sending email");
		}
		
		//logger.debug("Sent message from "+emailRequest.getFrom()+" with message handle "+result.getMessageId());
		System.out.println("Sent message from "+emailRequest.getFrom()+" with message handle "+result.getMessageId());
		return result.getMessageId();
	}
	
	
}
