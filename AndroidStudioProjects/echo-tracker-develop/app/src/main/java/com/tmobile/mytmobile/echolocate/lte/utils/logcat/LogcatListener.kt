package com.tmobile.mytmobile.echolocate.lte.utils.logcat

import android.annotation.SuppressLint
import com.tmobile.mytmobile.echolocate.lte.utils.LogcatlistenerItem
import com.tmobile.mytmobile.echolocate.lte.utils.LteUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * This class responsible to provide the implementation for listener
 * It creates the listener thorough runnable interface,and provides the functions to start and stop the listener
 */
class LogcatListener {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LogcatListener? = null

        private var logcatReader: LogcatReader? = null

        /***
         * creates [LogcatListener] instance
         */
        fun getInstance(): LogcatListener {
            return INSTANCE
                ?: synchronized(this) {
                val instance =
                    LogcatListener()
                INSTANCE = instance
                instance
            }
        }

        const val LINE_EXTRA = "LINE_EXTRA"
        const val TRIGGER_ID_EXTRA = "TRIGGER_ID_EXTRA"
    }

    private val listenerExecutor = Executors.newSingleThreadExecutor()
    private val isRunning = AtomicBoolean(false)
    private val shouldBeAlive = AtomicBoolean(false)
    private val listenerItems = CopyOnWriteArrayList<LogcatlistenerItem>()
    private var listener: Listener? = null


    /**
     * @return true if listener is running
     */
    fun isRunning(): Boolean {
        return isRunning.get()
    }

    /**
     * Add listeners to list
     *
     * @param listeners to add to listeners list
     */
    fun addListeners(listeners: List<LogcatlistenerItem>) {
        for (listener in listeners) {
            addListener(listener)
        }
    }

    /**
     * Add listener to list
     *
     * @param listener to add to listeners list
     */
    fun addListener(listener: LogcatlistenerItem) {
        if (!listenerItems.contains(listener)) {
            EchoLocateLog.eLogV("Diagnostic : Add item: ${listener.id}")
            listenerItems.add(listener)
        }
    }

    /**
     * If last listener is removed then stop reader
     *
     * @param listener to remove
     */
    fun removeListener(listener: LogcatlistenerItem) {
        EchoLocateLog.eLogV("Diagnostic : Remove listener: $listener")
        EchoLocateLog.eLogV(
            "Diagnostic : Before remove -> Listeners list: " +
                    listenerItems.toTypedArray().contentToString()
        )

        listenerItems.remove(listener)

        EchoLocateLog.eLogV(
            "Diagnostic : After remove -> Listeners list: " +
                    listenerItems.toTypedArray().contentToString()
        )
        if (isRunning.get() && isEmptyListenersList() && !shouldBeAlive.get()) {
            stop()
        }
    }

    /**
     * If last listener is removed then stop reader
     *
     * @param id to remove
     */
    fun removeListener(id: String) {
        val filteredListeners = ArrayList<LogcatlistenerItem>()
        for (item in listenerItems) {
            if (item.id.equals(id)) {
                filteredListeners.add(item)
            }
        }
        for (item in filteredListeners) {
            removeListener(item)
        }
    }


    /**
     * remove all listeners and stop reader
     */
    fun removeAllListeners() {
        listenerItems.clear()
        if (!shouldBeAlive.get()) {
            stop()
        }
    }

    /**
     * Start reading logcat entries
     */
    fun start() {
        EchoLocateLog.eLogV("Diagnostic : Start logcat listener")
        if (isRunning.get()) {
            EchoLocateLog.eLogV("Diagnostic : Logcat listener already started")
            return
        }
        if (listener == null) {
            listener = Listener()
        }

        listener!!.start()
        listenerExecutor.execute(listener!!)

    }

    /**
     * Stop reading logcat
     */
    fun stop() {
        EchoLocateLog.eLogV("Diagnostic : Stop listener")
        if (listener != null) {
            listener!!.stop()
        }
    }

    /**
     * Listener won't stop when listener list is empty
     */
    fun shouldBeAliveWhenEmpty() {
        EchoLocateLog.eLogV("Diagnostic : Should be alive when empty")
        shouldBeAlive.set(true)
    }

    /**
     * Listener will stop when listener list is empty
     */
    fun shouldBeStoppedWhenEmpty() {
        EchoLocateLog.eLogV("Diagnostic : Should be stopped when empty")
        shouldBeAlive.set(false)
    }


    internal fun isEmptyListenersList(): Boolean {
        return listenerItems.isEmpty()
    }

    internal fun listenersCount(): Int {
        return listenerItems.size
    }

    /**
     * Listener type
     */
    enum class Type {
        /**
         * Remove listener after found occurrence in logs
         */
        ONE_SHOT,

        /**
         * Process item many times
         */
        CONTINUOUS
    }

    private inner class Listener : Runnable {

        private val interrupt = AtomicBoolean(false)
        private var time: Long = 0

        override fun run() {
            try {
                val shell =
                    Shell.getInstance()

                logcatReader = LogcatReader.getInstance()
                    .clearLogcat(shell)

                logcatReader?.restartLogcatProcess(shell)

                var line: String?
                time = System.currentTimeMillis()
                EchoLocateLog.eLogV("MdSs:Started reading @ $time")
                while (!interrupt.get()) {
                    isRunning.set(true)
                    line = logcatReader!!.readLine()

                    if (line == null) {
                        continue
                    }

                    for (item in listenerItems) {
                        if (!item.regexToSearch
                                .matcher(line)
                                .matches()
                        ) {
                            continue
                        }
                        if (Type.ONE_SHOT == item.type) {
                            EchoLocateLog.eLogV("Remove " + item.id)
                            removeListener(item.id)
                        }
                        LteUtils.postIntent(item, line)
                        EchoLocateLog.eLogV("LTE Logcat send matched event: " + item.resultAction)
                    }
                }
            } catch (e: Exception) {
                EchoLocateLog.eLogE("error: ${e.localizedMessage}")
            } finally {
                destroy()
            }
        }

        internal fun stop() {
            interrupt.set(true)
            isRunning.set(false)
        }

        internal fun start() {
            EchoLocateLog.eLogV("Reset logcat listener")
            interrupt.set(false)
        }

        private fun destroy() {
            EchoLocateLog.eLogV("Destroy logcat listener in thread")
            logcatReader?.destroy()
        }
    }
}
