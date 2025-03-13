package com.irina.chat_app.mapper;

public interface Mapper<F, T> {
    default T map(F object) {
        return null;
    }

    default T map(F fromObject, T toObject) {
        return toObject;
    }
}
