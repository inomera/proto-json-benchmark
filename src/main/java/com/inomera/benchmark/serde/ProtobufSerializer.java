package com.inomera.benchmark.serde;

import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import org.apache.commons.lang3.SerializationException;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

interface Serializer<T> {
    byte[] serialize(T t);

    T deserialize(byte[] bytes);
}

public class ProtobufSerializer<T extends Message> implements Serializer<T> {
    private static final ConcurrentHashMap<Class<?>, Method> methodCache = new ConcurrentHashMap<>();

    private final Class<T> clazz;

    public ProtobufSerializer(Class<T> clazz) {
	this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
	return Optional.ofNullable(t)
		.map(MessageLite::toByteArray)
		.orElse(null);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
	return Optional.ofNullable(bytes)
		.map(e -> {
		    try {
			return parseFrom(clazz, bytes);
		    } catch (Exception ex) {
			throw new RuntimeException(ex);
		    }
		}).orElse(null);
    }

    private T parseFrom(Class<? extends Message> clazz, byte[] bytes) throws Exception {
	Method method = methodCache.get(clazz);
	if (method == null) {
	    method = clazz.getMethod("parseFrom", byte[].class);
	    methodCache.put(clazz, method);
	}
	return (T) method.invoke(clazz, bytes);
    }

}
