package com.alexandruleonte.demo.gallery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.lang.IllegalStateException


class PreferencesRepository (private val dataStore: DataStore<Preferences>) {
    val storedQuery: Flow<String> = dataStore.data.map { it->
        it[SEARCH_QUERY_KEY] ?: ""
    }

    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    val lastResultId: Flow<String> = dataStore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }.distinctUntilChanged()

    suspend fun setLastResultId(lastResultId: String) {
        dataStore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }

    val isPolling: Flow<Boolean> = dataStore.data.map {
        it[PREF_IS_POLLING] ?: false
    }.distinctUntilChanged()

    suspend fun setPolling(isPolling: Boolean) {
        dataStore.edit {
            it[PREF_IS_POLLING] = isPolling
        }
    }

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private var INSTANCE: PreferencesRepository? = null
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")
        private val PREF_IS_POLLING = booleanPreferencesKey("isPolling")

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }

                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE
                ?: throw IllegalStateException("PreferenceRepository must be initialized")
        }
    }
}