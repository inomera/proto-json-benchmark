package com.inomera.benchmark.serde;

import com.inomera.echo.domain.common.ResponseStatusProto;
import com.inomera.echo.domain.player.PlayerInfoProto;
import com.inomera.echo.domain.player.PlayerResponseProto;
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
public class ProtobufSerdeBenchmark {

    public static void main(String[] args) throws Exception {
	Options options = new OptionsBuilder()
		.include(ProtobufSerdeBenchmark.class.getSimpleName())
		.result("proto-results.json")
		.resultFormat(ResultFormatType.JSON)
		.build();
	new Runner(options)
		.run();
    }

    private ProtobufSerializer<PlayerResponseProto> protobufSerializer = new ProtobufSerializer<>(PlayerResponseProto.class);
    private final PlayerResponseProto playerResponseProto = createProtobufObjectPlayer();

    @Benchmark
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 1, time = 30, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Measurement(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS)
    @Timeout(time = 3, timeUnit = TimeUnit.MINUTES)
    public void serdeProtobuf() {
	IntStream.range(0, 1_000_000)
		.parallel()
		.forEach(i -> serializeAndDeserializeProtobuf(playerResponseProto));
    }

    private int serializeAndDeserializeProtobuf(PlayerResponseProto protobufObjectPlayer) {
	var bytes = protobufSerializer.serialize(protobufObjectPlayer);
	protobufSerializer.deserialize(bytes);
	return 0;
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
}
