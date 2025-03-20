package com.zx1316.memesmanager.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zx1316.memesmanager.model.MemeTag;

import java.io.IOException;
import java.util.Set;

public class MemeTagSetSerializer extends JsonSerializer<Set<MemeTag>> {
    @Override
    public void serialize(Set<MemeTag> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 开始数组
        gen.writeStartArray();
        for (MemeTag tag : value) {
            gen.writeString(tag.getTagName());
        }
        gen.writeEndArray();
    }
}
