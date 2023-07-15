package com.inomera.benchmark.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.inomera.echo.domain.common.ResponseStatusProto;
import com.inomera.echo.domain.player.PlayerInfoProto;
import com.inomera.echo.domain.player.PlayerResponseProto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SerdePerformanceTest {

    private JsonSerializer jsonSerializer;
    private ProtobufSerializer<PlayerResponseProto> protobufSerializer;
    private final PlayerResponse playerResponse = createJavaObjectPlayer();
    private final PlayerResponseProto playerResponseProto = createProtobufObjectPlayer();
    private final Map<String, List<String>> jsonResultMap = new ConcurrentHashMap<>();
    private final Map<String, List<String>> protoResultMap = new ConcurrentHashMap<>();


    public SerdePerformanceTest() throws JsonProcessingException {
	jsonSerializer = new JsonSerializer();
	protobufSerializer = new ProtobufSerializer<>(PlayerResponseProto.class);

	jsonSerializer.serializeAndDeserializeJson(playerResponse);
	serializeAndDeserializeProtobuf(playerResponseProto);
    }

    @AfterAll
    public void done() {
	System.out.println(jsonResultMap);
	for (Map.Entry<String, List<String>> entries : jsonResultMap.entrySet()) {
	    final double average = entries
		    .getValue()
		    .stream()
		    .map(i -> Integer.valueOf(StringUtils.trim(i).replaceAll("json :", "").replaceAll("ms", "").trim()))
		    .collect(Collectors.toList())
		    .stream()
		    .mapToDouble(a -> a)
		    .average()
		    .getAsDouble();
	    System.out.println(entries.getKey() + " -> json average : " + average);
	}

	System.out.println(protoResultMap);
	for (Map.Entry<String, List<String>> entries : protoResultMap.entrySet()) {
	    final double average = entries
		    .getValue()
		    .stream()
		    .map(i -> Integer.valueOf(StringUtils.trim(i).replaceAll("protobuf :", "").replaceAll("ms", "").trim()))
		    .collect(Collectors.toList())
		    .stream()
		    .mapToDouble(a -> a)
		    .average()
		    .getAsDouble();
	    System.out.println(entries.getKey() + " -> proto average : " + average);
	}
    }

    @Execution(ExecutionMode.CONCURRENT)
    @RepeatedTest(5)
    public void executeJsonSerde() {
	IntStream.of(1_000_000, 5_000_000, 10_000_000).forEach(iteration -> {
	    final String result = testPerformance(() -> {
		try {
		    jsonSerializer.serializeAndDeserializeJson(playerResponse);
		} catch (JsonProcessingException e) {
		    System.out.printf("je -> " + e);
		}
	    }, "json", iteration);
	    final List<String> outputs = jsonResultMap.putIfAbsent("\nIteration : " + iteration, Lists.newArrayList("\n" + result));
	    if (outputs == null) {
		return;
	    }
	    outputs.add("\n" + result);
	    jsonResultMap.put("\nIteration : " + iteration, outputs);
	});
    }


    private PlayerResponse createJavaObjectPlayer() {
	final PlayerInfo playerInfo = new PlayerInfo(1l, "Inomera R&D", "ACTIVE");
	RestResponseStatus status = new RestResponseStatus("200", "Success");
	final PlayerResponse playerResponse = new PlayerResponse(playerInfo, status, "hbdj3nkslmgse3bdk32bsx292y3dvx2x");
	return playerResponse;
    }

    @Execution(ExecutionMode.CONCURRENT)
    @RepeatedTest(5)
    public void executeProtoSerde() {
	IntStream.of(1_000_000, 5_000_000, 10_000_000).forEach(iteration -> {
	    final String result = testPerformance(() -> serializeAndDeserializeProtobuf(playerResponseProto), "protobuf", iteration);
	    final List<String> outputs = protoResultMap.putIfAbsent("\nIteration : " + iteration, Lists.newArrayList("\n" + result));
	    if (outputs == null) {
		return;
	    }
	    outputs.add("\n" + result);
	    protoResultMap.put("\nIteration : " + iteration, outputs);
	});
    }

    public void serializeAndDeserializeProtobuf(PlayerResponseProto protobufObjectPlayer) {
	var bytes = protobufSerializer.serialize(protobufObjectPlayer);
	protobufSerializer.deserialize(bytes);
    }

    private PlayerResponseProto createProtobufObjectPlayer() {
	return PlayerResponseProto.newBuilder()
		.setData(PlayerInfoProto.newBuilder()
			.setId(1l)
			.setName("Inomera R&D")
			.setStatus("ACTIVE")
			.build())
		.setStatus(ResponseStatusProto.newBuilder()
			.setCode("200")
			.setDescription("Success")
			.build())
		.setTxKey("hbdj3nkslmgse3bdk32bsx292y3dvx2x")
		.build();
    }

    private String testPerformance(Runnable runnable, String method, int iterations) {
	long start = System.currentTimeMillis();
	IntStream.range(0, iterations).forEach(i -> runnable.run());
	long finish = System.currentTimeMillis();
	return method + " : " + (finish - start) + " ms";
    }

}

