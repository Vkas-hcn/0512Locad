package com.river.flows.eastward.waves.contens.bean

import androidx.annotation.Keep

@Keep
data class UserAdminBean(
    val ad: Ad,
    val user: User
)

@Keep
data class Ad(
    val delay: Delay,
    val identifiers: Identifiers,
    val timing: Timing,
    val web: Web
)

@Keep
data class User(
    val limits: Limits,
    val permissions: Permissions,
    val profile: Profile
)

@Keep
data class Delay(
    val random: Random
)

@Keep
data class Identifiers(
    val fallback: String,
    val main: String
)

@Keep
data class Timing(
    val installTime: Int,
    val maxFailures: Int,
    val scanInterval: Int,
    val showIntervalTime: Int
)

@Keep
data class Web(
    val internal: String
)

@Keep
data class Random(
    val max: Int,
    val min: Int
)



@Keep
data class Limits(
    val ad: AdX,
    val click: Click
)

@Keep
data class Permissions(
    val uploadEnabled: String
)

@Keep
data class Profile(
    val type: String
)

@Keep
data class AdX(
    val daily: Int,
    val hourly: Int
)

@Keep
data class Click(
    val daily: Int
)
