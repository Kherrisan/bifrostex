package cn.kherrisan.bifrostex_engine

class EntityNotFoundException(entity: String, vararg c: Any) : RuntimeException("Cannot find the ${entity} by ${c.joinToString(",")}")

class InvalidArgumentException(vararg c: Any) : java.lang.RuntimeException("Invalid argument: ${c.joinToString(",")}")