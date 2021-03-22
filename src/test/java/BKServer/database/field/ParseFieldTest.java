package BKServer.database.field;

import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.database.field.WorkingModelParser;

public class ParseFieldTest {
	@Test
	public void test() {
		WorkingModelParser parser = new WorkingModelParser();
		parser.parse(new JSONObject("{\n"
				+ "		\"common\": [\n"
				+ "			{\n"
				+ "				\"common\": \"campaign_name\",\n"
				+ "				\"type\": \"string\"\n"
				+ "			},\n"
				+ "			{\n"
				+ "				\"common\": \"campaign_key\",\n"
				+ "				\"type\": \"genkey\"\n"
				+ "			},\n"
				+ "			{\n"
				+ "				\"common\": \"time_millisec\",\n"
				+ "				\"type\": \"time\"\n"
				+ "			}\n"
				+ "		],\n"
				+ "		\"different\": [\n"
				+ "			{\n"
				+ "				\"packet\": \"user_id_ab\",\n"
				+ "				\"storage\": \"user_id\",\n"
				+ "				\"type\": \"long\"\n"
				+ "			}\n"
				+ "		]\n"
				+ "	}"
				));
	}
}
