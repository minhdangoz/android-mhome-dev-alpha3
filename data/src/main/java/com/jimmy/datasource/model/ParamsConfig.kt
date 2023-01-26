package com.jimmy.datasource.model

data class ParamsConfig(
    val camId: String,
    val startTs: Long,
    val endTs: Long,
    val recordType: String = "NORMAL_RECORD", //MOTION_RECORD
)
