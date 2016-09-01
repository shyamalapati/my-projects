package com.shyam.awstest;

import java.util.ArrayList;
import java.util.List;

public class SendEmailRequest {

	String from;
	String awsKey;
	String awsSecret;

	List<String> toList = new ArrayList();
	List<String> ccList = new ArrayList();
	
	String subject;
	String bodyHtml;
	String senderName;

	boolean html = true;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public String getAwsSecret() {
		return awsSecret;
	}

	public void setAwsSecret(String awsSecret) {
		this.awsSecret = awsSecret;
	}

	public List<String> getToList() {
		return toList;
	}

	public void setToList(List<String> toList) {
		this.toList = toList;
	}

	public List<String> getCcList() {
		return ccList;
	}

	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBodyHtml() {
		return bodyHtml;
	}

	public void setBodyHtml(String bodyHtml) {
		this.bodyHtml = bodyHtml;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public void sanityCheck(){
		if(from==null||toList.size()==0||awsKey==null||awsSecret==null||subject.length()==0||bodyHtml.length()==0)
			throw new RuntimeException("Required fields are missing. please send from,toList,awsKey,awsSecret,subject and bodtHtml");
	}

}
