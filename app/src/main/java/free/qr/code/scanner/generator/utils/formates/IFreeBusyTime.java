package free.qr.code.scanner.generator.utils.formates;


import java.util.Map;

/**
 *
 *
 */
public class IFreeBusyTime extends SubSchema {

	public static final String NAME = "VFREEBUSY";
	private static final String BEGIN_TODO = "BEGIN:VFREEBUSY";

	public IFreeBusyTime() {
		super();
	}

	@Override
	public SubSchema parseSchema(Map<String, String> parameters, String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BEGIN_TODO).append(SchemeUtil.LINE_FEED);

		sb.append(SchemeUtil.LINE_FEED).append("END:VFREEBUSY");
		return sb.toString();
	}

	@Override
	public String toString() {
		return generateString();
	}

	public static SubSchema parse(Map<String, String> parameters, String code) {
		// TODO Auto-generated method stub
		return null;
	}

}
