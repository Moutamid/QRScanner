package com.moutamid.qr.scanner.generator.utils.formates;



import java.io.Serializable;
import java.util.Map;

import static com.moutamid.qr.scanner.generator.utils.formates.SchemeUtil.getParameters;

/**
 * Encodes a e-mail address, format is: <code>mailto:mail@address.com</code>
 *
 */
public class EMail extends Schema implements Serializable {

	private static final String BEGIN_EMAIL = "BEGIN:EMAIL";
	private static final String BODY = "body";
	private static final String SUBJECT = "subject";
	private static final String MAILTO = "mailto";
	private String email;
	private String mailSubject;
	private String mailBody;

	public EMail(String email, String mailSubject, String mailBody) {
		this.email = email;
		this.mailSubject = mailSubject;
		this.mailBody = mailBody;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	/**
	 * Default constructor to construct new e-mail object.
	 */
	public EMail() {
		super();
	}


	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(BEGIN_EMAIL)) {
			throw new IllegalArgumentException("this is not a valid EMAIL code: " + code);
		}
		Map<String, String> parameters = SchemeUtil.getParameters(code);
		if (parameters.containsKey(MAILTO)) {
			setEmail(parameters.get(MAILTO));
		}
		if (parameters.containsKey(SUBJECT)) {
			setMailSubject(parameters.get(SUBJECT));

		}
		if (parameters.containsKey(BODY)) {
			setMailBody(parameters.get(BODY));
		}

		Map<String, String> param = SchemeUtil.getParameters(code);
		// TODO
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder bob = new StringBuilder();
		bob.append(BEGIN_EMAIL).append(SchemeUtil.LINE_FEED);
		if (getEmail() != null) {
			bob.append(MAILTO).append(":").append(email).append(SchemeUtil.LINE_FEED);
		}
		if (getMailSubject() != null) {
			bob.append(SUBJECT).append(":").append(mailSubject).append(SchemeUtil.LINE_FEED);
		}
		if (getMailBody() != null) {
			bob.append(BODY).append(":").append(mailBody).append(SchemeUtil.LINE_FEED);
		}
		bob.append(SchemeUtil.LINE_FEED).append("END:EMAIL");
		return bob.toString();
		//return MAILTO + ":" + email +":"+ mailSubject +":"+mailBody;
	}

	public static EMail parse(final String emailCode) {
		EMail mail = new EMail();
		mail.parseSchema(emailCode);
		return mail;
	}

	@Override
	public String toString() {
		return generateString();
	}
}
//public class EMail extends Schema implements Serializable {
//
//	private static final String MAILTO = "mailto";
//	private String email;
//
//	/**
//	 * Default constructor to construct new e-mail object.
//	 */
//	public EMail() {
//		super();
//	}
//
//	public EMail(String email) {
//		super();
//		this.email = email;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	@Override
//	public Schema parseSchema(String code) {
//		if (code == null || !code.toLowerCase().startsWith(MAILTO)) {
//			throw new IllegalArgumentException("this is not a valid email code: " + code);
//		}
//		Map<String, String> parameters = getParameters(code.toLowerCase());
//		if (parameters.containsKey(MAILTO)) {
//			setEmail(parameters.get(MAILTO));
//		}
//		return this;
//	}
//
//	@Override
//	public String generateString() {
//		return MAILTO + ":" + email;
//	}
//
//	public static EMail parse(final String emailCode) {
//		EMail mail = new EMail();
//		mail.parseSchema(emailCode);
//		return mail;
//	}
//
//	@Override
//	public String toString() {
//		return generateString();
//	}
//}
