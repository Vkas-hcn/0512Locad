package com.river.flows.eastward.waves.contens.bean

import android.content.Context
import android.content.SharedPreferences

class SPUtils private constructor() {
    companion object {
        private const val DEFAULT_SP_NAME = "default_sp"
        private lateinit var sharedPreferences: SharedPreferences

        /**
         * 初始化，建议在Application中调用
         */
        fun init(context: Context, spName: String = DEFAULT_SP_NAME) {
            sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
        }

        /**
         * 存储String数据
         */
        operator fun set(key: String, value: String) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        /**
         * 获取String数据
         */
        operator fun get(key: String, default: String = ""): String {
            return sharedPreferences.getString(key, default) ?: default
        }

        operator fun set(key: String, value: Int) {
            sharedPreferences.edit().putInt(key, value).apply()
        }

        /**
         * 获取Int数据 (操作符重载)
         */
        operator fun get(key: String, default: Int = 0): Int {
            return sharedPreferences.getInt(key, default)
        }

        operator fun set(key: String, value: Boolean) {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        /**
         * 获取Int数据 (操作符重载)
         */
        operator fun get(key: String, default: Boolean = false): Boolean {
            return sharedPreferences.getBoolean(key, default)
        }

        /**
         * 存储Int数据
         */
        fun putInt(key: String, value: Int) {
            sharedPreferences.edit().putInt(key, value).apply()
        }

        /**
         * 获取Int数据
         */
        fun getInt(key: String, default: Int = 0): Int {
            return sharedPreferences.getInt(key, default)
        }

        /**
         * 存储Boolean数据
         */
        fun putBoolean(key: String, value: Boolean) {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        /**
         * 获取Boolean数据
         */
        fun getBoolean(key: String, default: Boolean = false): Boolean {
            return sharedPreferences.getBoolean(key, default)
        }

        /**
         * 存储Float数据
         */
        fun putFloat(key: String, value: Float) {
            sharedPreferences.edit().putFloat(key, value).apply()
        }

        /**
         * 获取Float数据
         */
        fun getFloat(key: String, default: Float = 0f): Float {
            return sharedPreferences.getFloat(key, default)
        }

        /**
         * 存储Long数据
         */
        fun putLong(key: String, value: Long) {
            sharedPreferences.edit().putLong(key, value).apply()
        }

        /**
         * 获取Long数据
         */
        fun getLong(key: String, default: Long = 0L): Long {
            return sharedPreferences.getLong(key, default)
        }

        /**
         * 移除指定key的数据
         */
        fun remove(key: String) {
            sharedPreferences.edit().remove(key).apply()
        }

        /**
         * 清除所有数据
         */
        fun clear() {
            sharedPreferences.edit().clear().apply()
        }

        /**
         * 检查是否包含某个key
         */
        fun contains(key: String): Boolean {
            return sharedPreferences.contains(key)
        }
    }
}