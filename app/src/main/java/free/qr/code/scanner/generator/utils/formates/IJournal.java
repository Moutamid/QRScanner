package free.qr.code.scanner.generator.utils.formates;



import java.util.Map;

import static free.qr.code.scanner.generator.utils.formates.SchemeUtil.LINE_FEED;

/**
 *
 */
public class IJournal extends SubSchema {

	public static final String NAME = "VJOURNAL";
	private static final String BEGIN_TODO = "BEGIN:VJOURNAL";

	public IJournal() {
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
		sb.append(BEGIN_TODO).append(LINE_FEED);
		// TODO
		sb.append(LINE_FEED).append("END:VJOURNAL");
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
