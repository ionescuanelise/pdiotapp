package com.specknet.pdiotapp

import java.sql.Timestamp
import java.util.*

data class Activity(val id: Int = -1, val activity_name: String, val duration: Long, val date: String)