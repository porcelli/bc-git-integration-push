package porcelli.me.git.integration.common.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import porcelli.me.git.integration.common.model.Payload;
import porcelli.me.git.integration.common.model.PullRequestEvent;

public class MappingModule extends SimpleModule {

    private static class IOBuilder<M, T> {

        public static <M, T> IOBuilder<M, T> builder(Class<T> typeClass, Class<M> modelClass) {
            return new IOBuilder<>(typeClass, modelClass);
        }

        private final Class<T> typeClass;
        private final Class<M> modelClass;
        private final Map<T, Class<? extends M>> targetModelClasses;

        private IOBuilder(Class<T> typeClass, Class<M> modelClass) {
            this.typeClass = typeClass;
            this.modelClass = modelClass;
            this.targetModelClasses = new HashMap<>();
        }

        public IOBuilder<M, T> defineType(T type, Class<? extends M> modelType) {
            this.targetModelClasses.put(type, modelType);
            return this;
        }

        public SubtypeDeserializer<T, M> buildDeserializer() {
            return new SubtypeDeserializer<>(typeClass, modelClass, targetModelClasses);
        }
    }

    private static class SubtypeDeserializer<T, M> extends StdDeserializer<M> {

        private final Class<T> typeClass;
        private final Map<T, Class<? extends M>> targetModelClasses;

        SubtypeDeserializer(Class<T> typeClass, Class<M> modelClass, Map<T, Class<? extends M>> targetModelClasses) {
            super(modelClass);
            this.typeClass = typeClass;
            this.targetModelClasses = targetModelClasses;
        }

        @Override
        public M deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode root = mapper.readTree(jp);

            JsonNode typeNode = root.get("type");
            if (typeNode == null) {
                throw new IllegalArgumentException("Must specify a " + typeClass + " in the \"type\" field!");
            }

            String typeValue = typeNode.textValue();
            for (Entry<T, Class<? extends M>> entry : targetModelClasses.entrySet()) {
                T key = entry.getKey();
                Class<? extends M> value = entry.getValue();

                if (key.toString().equalsIgnoreCase(typeValue)) {
                    JsonParser traverse = root.traverse();
                    traverse.setCodec(mapper);
                    return mapper.readValue(traverse, value);
                }
            }
            throw new IllegalArgumentException(typeClass + ": " + typeValue + " is unsupported!");
        }
    }

    @Override
    public void setupModule(SetupContext context) {
        SimpleDeserializers deserializers = new SimpleDeserializers();
        context.addDeserializers(deserializers);
        addCustomPayloadDeserialization(deserializers);
    }

    private void addCustomPayloadDeserialization(SimpleDeserializers deserializers) {
        Class<Payload.EventType> typeClass = Payload.EventType.class;
        Class<Payload> modelClass = Payload.class;

        IOBuilder<Payload, Payload.EventType> io = IOBuilder.builder(typeClass, modelClass)
                .defineType(Payload.EventType.PULL_REQUEST, PullRequestEvent.class);
        deserializers.addDeserializer(modelClass, io.buildDeserializer());
    }
}