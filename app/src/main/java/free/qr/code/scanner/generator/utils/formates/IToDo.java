package free.qr.code.scanner.generator.utils.formates;



import java.util.Map;

/**
 *
 */
public class IToDo extends SubSchema {

	public static final String NAME = "VTODO";
	private static final String BEGIN_TODO = "BEGIN:VTODO";

	public IToDo() {
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

		sb.append(SchemeUtil.LINE_FEED).append("END:VTODO");
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
