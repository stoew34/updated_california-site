package com.tmobile.mytmobile.echolocate.nr5g.core.utils

import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.UndeclaredThrowableException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Simple utility class for working with the reflection API and handling
 * reflection exceptions.
 */
class Nr5gReflectionUtil {

    companion object {
        /**
         * Cache for [Class.getDeclaredMethods], allowing for fast resolution.
         */
        private val declaredMethodsCache = ConcurrentHashMap<Class<*>, Array<Method>>()

        /**
         * Returns the java equivalent class name
         */
        fun getClass(className: Any): Class<*>? {
            val aClass: Class<*>
            val classpath = className.javaClass.name
            try {
                aClass =
                    findClassByName(
                        classpath
                    )
                val methods = aClass.methods
                val methodList = StringBuilder()
                for (method in methods) {
                    methodList.append(method.name)
                    methodList.append(",")
                }
                EchoLocateLog.eLogV("Methods $methodList was found in $classpath class")
                val fields = aClass.declaredFields
                val fieldsList = StringBuilder()
                for (field in fields) {
                    fieldsList.append(field.name)
                    fieldsList.append(",")
                }
                EchoLocateLog.eLogV("Fields $methodList was found in $classpath class")
            } catch (e: ClassNotFoundException) {
                EchoLocateLog.eLogE("Couldn't find class $classpath")
                return null
            }

            return aClass
        }

        /**
         * Invoke the specified [Method] against the supplied target object with no arguments.
         * The target object can be `null` when invoking a static [Method].
         *
         * Thrown exceptions are handled via a call to [.handleReflectionException].
         *
         * @param methodName the method to invoke
         * @param target the target object to invoke the method on
         * @return the invocation result, if any
         * @see .invokeMethod
         */
        fun callMethod(aClass: Class<*>?, methodName: String, target: Any): String? {
            try {
                return invokeMethod(
                    findMethod(
                        aClass,
                        methodName
                    ),
                    target
                ).toString()
            } catch (e: NoSuchMethodException) {
                EchoLocateLog.eLogE(e.toString())
            } catch (e: IllegalAccessException) {
                EchoLocateLog.eLogE(e.toString())
            }

            return null
        }

        /**
         * * Invoke the specified [Method] against the supplied target object with no arguments.
         * The target object can be `null` when invoking a static [Method].
         *
         * Thrown exceptions are handled via a call to [.handleReflectionException].
         *
         * @param methodName the method to invoke
         * @param target the target object to invoke the method on
         * @return the invocation result (Map<String,String>), if any
         */
        fun callCarrierConfigMethod(aClass: Class<*>?, methodName: String, target: Any): Any? {
            try {
                return invokeMethod(
                    findMethod(
                        aClass,
                        methodName
                    ),
                    target
                )
            } catch (e: NoSuchMethodException) {
                EchoLocateLog.eLogE(e.toString())
            } catch (e: IllegalAccessException) {
                EchoLocateLog.eLogE(e.toString())
            }

            return null
        }

        /**
         * @param clazz valid class name
         * @return `class` if found
         * @throws ClassNotFoundException
         */
        @Throws(ClassNotFoundException::class)
        fun findClassByName(clazz: String): Class<*> {
            return Class.forName(clazz)
        }

        /**
         * Attempt to find a [Method] on the supplied class with the supplied name
         * and no parameters. Searches all superclasses up to `Object`.
         *
         * Returns `null` if no [Method] can be found.
         *
         * @param clazz the class to introspect
         * @param name  the name of the method
         * @return the Method object
         * @throws NoSuchMethodException
         */
        @Throws(NoSuchMethodException::class)
        fun findMethod(clazz: Class<*>?, name: String): Method {
            val a = arrayOf<Class<*>>()
            return findMethod(
                clazz,
                name,
                *a
            )
        }

        /**
         * Attempt to find a [Method] on the supplied class with the supplied name
         * and parameter types. Searches all superclasses up to `Object`.
         *
         * Returns `null` if no [Method] can be found.
         *
         * @param clazz      the class to introspect
         * @param name       the name of the method
         * @param paramTypes the parameter types of the method
         * (may be `null` to indicate any signature)
         * @return the Method object
         * @throws NoSuchMethodException
         */
        @Throws(NoSuchMethodException::class)
        fun findMethod(clazz: Class<*>?, name: String, vararg paramTypes: Class<*>): Method {
            var searchType: Class<*>? = clazz
            while (searchType != null) {
                val methods =
                    if (searchType.isInterface) searchType.methods else getDeclaredMethods(
                        searchType
                    )
                for (method in methods) {
                    if (name == method.name && (paramTypes == null || Arrays.equals(
                            paramTypes,
                            method.parameterTypes
                        ))
                    ) {
                        return method
                    }
                }
                searchType = searchType.superclass
            }
            throw NoSuchMethodException()
        }

        /**
         * This method retrieves [Class.getDeclaredMethods] from a local cache
         * in order to avoid the JVM's SecurityManager check and defensive array copying.
         */
        private fun getDeclaredMethods(clazz: Class<*>): Array<Method> {
            var result = declaredMethodsCache.get(clazz)
            if (result == null) {
                result = clazz.declaredMethods
                declaredMethodsCache.put(clazz, result!!)
            }
            return result
        }

        /**
         * Invoke the specified [Method] against the supplied target object with no arguments.
         * The target object can be `null` when invoking a static [Method].
         *
         * Thrown exceptions are handled via a call to [.handleReflectionException].
         *
         * @param method the method to invoke
         * @param target the target object to invoke the method on
         * @return the invocation result, if any
         * @see .invokeMethod
         */
        @Throws(IllegalAccessException::class)
        fun invokeMethod(method: Method, target: Any): Any? {
            val a = arrayOf<Any>()
            return invokeMethod(
                method,
                target,
                *a
            )
        }

        /**
         * Invoke the specified [Method] against the supplied target object with the
         * supplied arguments. The target object can be `null` when invoking a
         * static [Method].
         *
         * Thrown exceptions are handled via a call to [.handleReflectionException].
         *
         * @param method the method to invoke
         * @param target the target object to invoke the method on
         * @param args   the invocation arguments (may be `null`)
         * @return the invocation result, if any
         * @throws IllegalStateException, if handleReflectionException won't handle exception
         */
        @Throws(IllegalAccessException::class)
        private fun invokeMethod(method: Method?, target: Any, vararg args: Any): Any? {
            try {
                return method?.invoke(target, *args)
            } catch (ex: Exception) {
                handleReflectionException(ex)
            }

            throw IllegalAccessException()
        }

        /**
         * Handle the given reflection exception. Should only be called if no
         * checked exception is expected to be thrown by the target method.
         *
         * Throws the underlying RuntimeException or Error in case of an
         * InvocationTargetException with such a root cause. Throws an
         * IllegalStateException with an appropriate message else.
         *
         * @param exception the reflection exception to handle
         */
        private fun handleReflectionException(exception: Exception) {
            if (exception is NoSuchMethodException) {
                throw IllegalStateException("Method not found: " + exception.message)
            } else if (exception is IllegalAccessException) {
                throw IllegalStateException("Could not access method: " + exception.message)
            } else (exception as? InvocationTargetException)?.let {
                rethrowRuntimeException(
                    it
                )
            }
                ?: if (exception is RuntimeException) {
                    throw exception
                } else {
                    throw UndeclaredThrowableException(exception)
                }
        }

        /**
         * Rethrow the given [exception][Throwable], which is presumably the
         * *target exception* of an [InvocationTargetException]. Should
         * only be called if no checked exception is expected to be thrown by the
         * target method.
         *
         * Rethrows the underlying exception cast to an [RuntimeException] or
         * [Error] if appropriate; otherwise, throws an
         * [IllegalStateException].
         *
         * @param throwable the exception to rethrow
         * @throws RuntimeException the rethrown exception
         */
        private fun rethrowRuntimeException(throwable: Throwable) {
            if (throwable is RuntimeException) {
                throw throwable
            }
            if (throwable is Error) {
                throw throwable
            }
            throw UndeclaredThrowableException(throwable)
        }
    }

}
