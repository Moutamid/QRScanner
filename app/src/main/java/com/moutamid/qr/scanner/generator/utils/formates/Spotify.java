package com.moutamid.qr.scanner.generator.utils.formates;


import java.io.Serializable;
import java.util.Map;

/**
 * A simple wrapper for vCard data to use with ZXing QR Code generator.
 * <p>
 * See also http://zxing.appspot.com/generator/ and Contact Information
 *
 */
public class Spotify extends Schema implements Serializable {

	private static final String BEGIN_VCARD = "BEGIN:SPOTIFY";
	private static final String NAME = "NAME";
	private static final String SONG = "SONG";

	private String name;
	private String song;

	public Spotify() {
		super();
	}

	public Spotify(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Spotify setName(String name) {
		this.name = name;
		return this;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	@Override
	public Schema parseSchema(String code) {
		if (code == null || !code.startsWith(BEGIN_VCARD)) {
			throw new IllegalArgumentException("this is not a valid VCARD code: " + code);
		}
		Map<String, String> parameters = SchemeUtil.getParameters(code);
		if (parameters.containsKey(NAME)) {
			setName(parameters.get(NAME));
		}
		if (parameters.containsKey(song)) {
			setSong(parameters.get(SONG));
		}
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN_VCARD).append(SchemeUtil.LINE_FEED);
		sb.append("VERSION:3.0").append(SchemeUtil.LINE_FEED);
		if (name != null) {
			sb.append(NAME).append(":").append(name);
		}
		if (song != null) {
			sb.append(SchemeUtil.LINE_FEED).append(SONG).append(":").append(song);
		}
		sb.append(SchemeUtil.LINE_FEED).append("END:VCARD");
		return sb.toString();
	}

	/**
	 * Returns the textual representation of this vcard of the form
	 * <p>
	 * BEGIN:VCARD N:John Doe ORG:Company TITLE:Title TEL:1234 URL:www.code.org
	 * EMAIL:john.doe@code.org ADR:Street END:VCARD
	 * </p>
	 */
	public String toString() {
		return generateString();
	}

	public static Spotify parse(final String code) {
		Spotify vCard = new Spotify();
		vCard.parseSchema(code);
		return vCard;
	}
}
