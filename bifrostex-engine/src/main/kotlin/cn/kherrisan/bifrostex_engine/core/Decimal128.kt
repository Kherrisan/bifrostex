package cn.kherrisan.bifrostex_engine.core

import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Field(targetType = FieldType.DECIMAL128)
annotation class Decimal128 {
}