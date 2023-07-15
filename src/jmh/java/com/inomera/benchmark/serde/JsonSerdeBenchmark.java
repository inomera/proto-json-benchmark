package com.inomera.benchmark.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class JsonSerdeBenchmark {

    public static void main(String[] args) throws Exception {
	Options options = new OptionsBuilder()
		.include(JsonSerdeBenchmark.class.getSimpleName())
		.result("json-results.json")
		.resultFormat(ResultFormatType.JSON)
		.build();
	new Runner(options)
		.run();
    }

    private JsonSerializer jsonSerializer = new JsonSerializer();
    private PlayerResponse playerResponse = createJavaObjectPlayer();

    private PlayerResponse createJavaObjectPlayer() {
	final PlayerInfo playerInfo = new PlayerInfo(1l, "Inomera R&D", "ACTIVE");
	RestResponseStatus status = new RestResponseStatus("200", "Success");
	final PlayerResponse playerResponse = new PlayerResponse(playerInfo, status, "hbdj3nkslmgse3bdk32bsx292y3dvx2x");
	return playerResponse;
    }


    @Benchmark
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 1, time = 30, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Measurement(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
    @Timeout(time = 3, timeUnit = TimeUnit.MINUTES)
    public void serdeJson() {
	IntStream.range(0, 1_000_000).parallel().forEach(i -> {
		    try {
			jsonSerializer.serializeAndDeserializeJson(playerResponse);
		    } catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		    }
		}
	);
    }
}
