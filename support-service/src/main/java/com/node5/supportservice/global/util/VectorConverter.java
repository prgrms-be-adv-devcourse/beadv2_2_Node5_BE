package com.node5.supportservice.global.util;

import com.pgvector.PGvector;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.SQLException;
@Converter(autoApply = false)
public class VectorConverter implements AttributeConverter<float[], Object> {

    @Override
    public Object convertToDatabaseColumn(float[] attribute) {
        if (attribute == null || attribute.length == 0) {
            return null;
        }
        // DB에 저장할 때 PGvector 객체로 변환 (JDBC 드라이버가 이를 인식함)
        return new PGvector(attribute);
    }

    @Override
    public float[] convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }

        // DB에서 읽어올 때 PGvector 타입이면 toArray() 호출
        if (dbData instanceof PGvector pgvector) {
            return pgvector.toArray();
        }

        // 만약 문자열로 넘어올 경우를 대비한 방어 코드
        if (dbData instanceof String str) {
            try {
                return new PGvector(str).toArray();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}