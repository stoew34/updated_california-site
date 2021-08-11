package com.tmobile.mytmobile.echolocate.reporting.utils

/**
 * Created by Divya Mittal on 5/19/21
 */
/**
 * this class provides the instance of single class provided
 * encapsulate the logic to lazily create and initialize
 * a singleton with argument inside a SingletonHolder class
 */
open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    /**
     * @param arg - argument
     * @return  T - generic object
     */
    fun getInstance(arg: A): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }
}