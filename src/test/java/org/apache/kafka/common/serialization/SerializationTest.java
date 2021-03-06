package org.apache.kafka.common.serialization;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * 序列化测试
 *
 * @author wanggang
 *
 */
public class SerializationTest {

	private static class SerDeser<T> {

		final Serializer<T> serializer;
		final Deserializer<T> deserializer;

		public SerDeser(Serializer<T> serializer, Deserializer<T> deserializer) {
			this.serializer = serializer;
			this.deserializer = deserializer;
		}

	}

	@Test
	public void testStringSerializer() {
		String str = "my string";
		String mytopic = "testTopic";
		List<String> encodings = new ArrayList<>();
		encodings.add("UTF8");
		encodings.add("UTF-16");

		for (String encoding : encodings) {
			SerDeser<String> serDeser = getStringSerDeser(encoding);
			Serializer<String> serializer = serDeser.serializer;
			Deserializer<String> deserializer = serDeser.deserializer;

			assertEquals(
					"Should get the original string after serialization and deserialization with encoding "
							+ encoding, str,
					deserializer.deserialize(mytopic, serializer.serialize(mytopic, str)));

			assertEquals("Should support null in serialization and deserialization with encoding "
					+ encoding, null,
					deserializer.deserialize(mytopic, serializer.serialize(mytopic, null)));
		}
	}

	@Test
	public void testIntegerSerializer() {
		Integer[] integers = new Integer[] { 423412424, -41243432 };
		String mytopic = "testTopic";

		try (Serializer<Integer> serializer = new IntegerSerializer();
				Deserializer<Integer> deserializer = new IntegerDeserializer();) {
			for (Integer integer : integers) {
				assertEquals(
						"Should get the original integer after serialization and deserialization",
						integer,
						deserializer.deserialize(mytopic, serializer.serialize(mytopic, integer)));
			}
			assertEquals("Should support null in serialization and deserialization", null,
					deserializer.deserialize(mytopic, serializer.serialize(mytopic, null)));
		}
	}

	private SerDeser<String> getStringSerDeser(String encoder) {
		Map<String, Object> serializerConfigs = new HashMap<>();
		serializerConfigs.put("key.serializer.encoding", encoder);
		Serializer<String> serializer = new StringSerializer();
		serializer.configure(serializerConfigs, true);

		Map<String, Object> deserializerConfigs = new HashMap<>();
		deserializerConfigs.put("key.deserializer.encoding", encoder);
		Deserializer<String> deserializer = new StringDeserializer();
		deserializer.configure(deserializerConfigs, true);

		return new SerDeser<String>(serializer, deserializer);
	}

}
