package cn.kherrisan.bifrostex_engine.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> CoroutineScope.cache(ds: suspend () -> T): Cache<T> = Cache(ds, this)

class Cache<T>(
        private val dataSource: suspend () -> T,
        private val coroutineScope: CoroutineScope
) {

    private var schedule: Job? = null
    private var dirty: Boolean = true
    private var data: T? = null

    fun touch() {
        dirty = true
    }

    suspend fun data(): T? {
        if (dirty) {
            refresh()
            dirty = false
        }
        return data
    }

    suspend fun refresh() {
        data = dataSource()
    }

    suspend fun periodRefresh(period: Long) {
        schedule = coroutineScope.launch {
            while (true) {
                delay(period)
                refresh()
            }
        }
    }

    fun release() {
        if (schedule != null && schedule!!.isActive) {
            schedule!!.cancel()
        }
        data = null
    }
}