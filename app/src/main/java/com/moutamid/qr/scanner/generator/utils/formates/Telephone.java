package com.moutamid.qr.scanner.generator.utils.formates;


import java.io.Serializable;
import java.util.Map;

import static com.moutamid.qr.scanner.generator.utils.formates.SchemeUtil.getParameters;

/**
 * Encodes a telephone number, format is: <code>tel:+1-212-555-1212</code>
 *
 */
public class Telephone extends Schema implements Serializable {

	private static final String TEL = "tel";
	private String telephone;
	private String separate;
	/**
	 * Default constructor to construct new telephone object.
	 */
	public Telephone() {
		super();
	}

	public String getSeparate() {
		return separate;
	}

	public void setSeparate(String separate) {
		this.separate = separate;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.trim().toLowerCase().startsWith(TEL)) {
			throw new IllegalArgumentException("this is not a valid telephone code: " + code);
		}
		Map<String, String> parameters = SchemeUtil.getParameters(code.trim().toLowerCase());
		if (parameters.containsKey(TEL)) {
			setTelephone(parameters.get(TEL));
		}
		return this;
	}

	@Override
	public String generateString() {
		return TEL + ":" + telephone;
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static Telephone parse(final String telephone) {
		Telephone tel = new Telephone();
		tel.parseSchema(telephone);
		return tel;
	}
}
