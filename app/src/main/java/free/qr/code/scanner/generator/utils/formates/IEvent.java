package free.qr.code.scanner.generator.utils.formates;



import java.io.Serializable;
import java.util.Map;

import static free.qr.code.scanner.generator.utils.formates.SchemeUtil.LINE_FEED;
import static free.qr.code.scanner.generator.utils.formates.SchemeUtil.getParameters;

/**
 * A simple wrapper for iEvent data to use with ZXing QR Code generator.
 * 
 * <code>
 * BEGIN:VEVENT 
 * UID:uid1@code.com
 * DTSTAMP:19970714T170000Z 
 * ORGANIZER;CN=John Doe:MAILTO:john.doe@code.com
 * DTSTART:19970714T170000Z
 * DTEND:19970715T035959Z 
 * SUMMARY:Bastille Day Party 
 * END:VEVENT
 * </code>
 *
 */
public class IEvent extends Schema implements Serializable {

	public static final String NAME = "VEVENT";
	private static final String BEGIN_EVENT = "BEGIN:VEVENT";
	private static final String UID = "UID";
	private static final String STAMP = "DTSTAMP";
	private static final String ORGANIZER = "ORGANIZER";
	private static final String START = "DTSTART";
	private static final String END = "DTEND";
	private static final String SUMMARY = "SUMMARY";

	private String uid;
	private String stamp;
	private String organizer;
	private String start;
	private String end;
	private String summary;

	public IEvent() {
		super();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public Schema parseSchema( String code) {
		if (code == null || !code.startsWith(BEGIN_EVENT)) {
			throw new IllegalArgumentException("this is not a valid VEVENT code: " + code);
		}
		Map<String, String> parameters = getParameters(code);
		if (parameters.containsKey(UID)) {
			setUid(parameters.get(UID));
		}
		if (parameters.containsKey(STAMP)) {
			setStamp(parameters.get(STAMP));
		}
		if (parameters.containsKey(START)) {
			setStart(parameters.get(START));
		}
		if (parameters.containsKey(END)) {
			setEnd(parameters.get(END));
		}
		if (parameters.containsKey(SUMMARY)) {
			setSummary(parameters.get(SUMMARY));
		}
		Map<String, String> param = getParameters(code);
		// TODO
		return this;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN_EVENT).append(LINE_FEED);
		if (uid != null) {
			sb.append(UID).append(":").append(uid).append(LINE_FEED);
		}if (stamp != null) {
			sb.append(STAMP).append(":").append(stamp).append(LINE_FEED);
		}if (organizer != null) {
			sb.append(ORGANIZER).append(";").append(organizer).append(LINE_FEED);
		}if (start != null) {
			sb.append(START).append(":").append(start).append(LINE_FEED);
		}if (end != null) {
			sb.append(END).append(":").append(end).append(LINE_FEED);
		}if (summary != null) {
			sb.append(SUMMARY).append(":").append(summary).append(LINE_FEED);
		}
		sb.append(LINE_FEED).append("END:VEVENT");
		return sb.toString();
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static IEvent parse( final String code) {
		IEvent event = new IEvent();
		event.parseSchema(code);
		return event;
	}
}
