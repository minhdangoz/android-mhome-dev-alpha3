package com.jimmy.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore("APP_PREFERENCES")

//    private suspend fun <T> DataStore<Preferences>.getFromLocalStorage(
//        PreferencesKey: Preferences.Key<T>, func: T.() -> Unit
//    ) {
//        data.catch { exception ->
//            if (exception is IOException) {
//                emit(emptyPreferences())
//            } else {
//                throw exception
//            }
//        }.map {
//            it[PreferencesKey]
//        }.collect {
//            it?.let { func.invoke(it as T) }
//        }
//    }
//
//    suspend fun <T> storeValue(key: Preferences.Key<T>, value: T) {
//        context.dataStore.edit {
//            it[key] = value
//        }
//    }
//
//    suspend fun <T> readValue(key: Preferences.Key<T>, responseFunc: T.() -> Unit) {
//        context.dataStore.getFromLocalStorage(key) {
//            responseFunc.invoke(this)
//        }
//    }

    fun <U> getSyncData(key: String, default: U): U {
        val res = when (default) {
            is Long -> readLongData(key, default)
            is String -> readStringData(key, default)
            is Int -> readIntData(key, default)
            is Boolean -> readBooleanData(key, default)
            is Float -> readFloatData(key, default)
            else -> throw IllegalArgumentException("This type can be saved into DataStore")
        }
        return res as U
    }

    fun <U> getData(key: String, default: U): Flow<U> {
        val data = when (default) {
            is Long -> readLongFlow(key, default)
            is String -> readStringFlow(key, default)
            is Int -> readIntFlow(key, default)
            is Boolean -> readBooleanFlow(key, default)
            is Float -> readFloatFlow(key, default)
            else -> throw IllegalArgumentException("This type can be saved into DataStore")
        }
        return data as Flow<U>
    }

    suspend fun <U> putData(key: String, value: U) {
        when (value) {
            is Long -> saveLongData(key, value)
            is String -> saveStringData(key, value)
            is Int -> saveIntData(key, value)
            is Boolean -> saveBooleanData(key, value)
            is Float -> saveFloatData(key, value)
            else -> throw IllegalArgumentException("This type can be saved into DataStore")
        }
    }

    fun <U> putSyncData(key: String, value: U) {
        when (value) {
            is Long -> saveSyncLongData(key, value)
            is String -> saveSyncStringData(key, value)
            is Int -> saveSyncIntData(key, value)
            is Boolean -> saveSyncBooleanData(key, value)
            is Float -> saveSyncFloatData(key, value)
            else -> throw IllegalArgumentException("This type can be saved into DataStore")
        }
    }

    fun readBooleanFlow(key: String, default: Boolean = false): Flow<Boolean> =
        context.dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[booleanPreferencesKey(key)] ?: default
            }

    fun readBooleanData(key: String, default: Boolean = false): Boolean {
        var value = false
        runBlocking {
            context.dataStore.data.first {
                value = it[booleanPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    fun readIntFlow(key: String, default: Int = 0): Flow<Int> =
        context.dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[intPreferencesKey(key)] ?: default
            }

    fun readIntData(key: String, default: Int = 0): Int {
        var value = 0
        runBlocking {
            context.dataStore.data.first {
                value = it[intPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    fun readStringFlow(key: String, default: String = ""): Flow<String> =
        context.dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[stringPreferencesKey(key)] ?: default
            }

    fun readStringData(key: String, default: String = ""): String {
        var value = ""
        runBlocking {
            context.dataStore.data.first {
                value = it[stringPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    fun readFloatFlow(key: String, default: Float = 0f): Flow<Float> =
        context.dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[floatPreferencesKey(key)] ?: default
            }

    fun readFloatData(key: String, default: Float = 0f): Float {
        var value = 0f
        runBlocking {
            context.dataStore.data.first {
                value = it[floatPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    fun readLongFlow(key: String, default: Long = 0L): Flow<Long> =
        context.dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map {
                it[longPreferencesKey(key)] ?: default
            }

    fun readLongData(key: String, default: Long = 0L): Long {
        var value = 0L
        runBlocking {
            context.dataStore.data.first {
                value = it[longPreferencesKey(key)] ?: default
                true
            }
        }
        return value
    }

    suspend fun saveBooleanData(key: String, value: Boolean) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[booleanPreferencesKey(key)] = value
        }
    }

    fun saveSyncBooleanData(key: String, value: Boolean) =
        runBlocking { saveBooleanData(key, value) }

    suspend fun saveIntData(key: String, value: Int) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[intPreferencesKey(key)] = value
        }
    }

    fun saveSyncIntData(key: String, value: Int) = runBlocking { saveIntData(key, value) }

    suspend fun saveStringData(key: String, value: String) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[stringPreferencesKey(key)] = value
        }
    }

    fun saveSyncStringData(key: String, value: String) = runBlocking { saveStringData(key, value) }

    suspend fun saveFloatData(key: String, value: Float) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[floatPreferencesKey(key)] = value
        }
    }

    fun saveSyncFloatData(key: String, value: Float) = runBlocking { saveFloatData(key, value) }

    suspend fun saveLongData(key: String, value: Long) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[longPreferencesKey(key)] = value
        }
    }

    fun saveSyncLongData(key: String, value: Long) = runBlocking { saveLongData(key, value) }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }

    fun clearSync() {
        runBlocking {
            context.dataStore.edit {
                it.clear()
            }
        }
    }
}
