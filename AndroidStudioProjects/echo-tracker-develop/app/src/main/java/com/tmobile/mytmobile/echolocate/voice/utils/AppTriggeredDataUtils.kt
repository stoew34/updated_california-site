package com.tmobile.mytmobile.echolocate.voice.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog

/**
 * Class that provide info of package info, application version code, application version name and package label
 */
class AppTriggeredDataUtils {

    companion object {
        /**
         * @param context     valid context
         * @param packageName package name of the app to find
         * @return [android.content.pm.PackageInfo] object or `null` if
         * 'packageName' doesn't exist
         */
        private fun getPackageInfo(context: Context, packageName: String): PackageInfo? {
            val packageManager = context.packageManager
            return try {
                packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                EchoLocateLog.eLogE("PackageManager does not exist")
                null
            }

        }

        /**
         * Return the version code of the specified package.
         *
         * @param context     Context
         * @param packageName Package name
         * @return version code
         */
        fun getApplicationVersionCode(context: Context, packageName: String): String {
            val packageInfo = getPackageInfo(context, packageName)

            return if (packageInfo == null) {
                EchoLocateLog.eLogE("Error getting application's version code")
                ""
            } else {
                EchoLocateLog.eLogI("EchoApp : version code for package name $packageInfo.versionCode")
                packageInfo.versionCode.toString()
            }
        }

        /**
         * Return the version name of the specified package.
         *
         * @param context     Context
         * @param packageName Package name
         * @return version name
         */
        fun getApplicationVersionName(context: Context, packageName: String): String {
            val packageInfo = getPackageInfo(context, packageName)

            return if (packageInfo == null) {
                EchoLocateLog.eLogE("Error getting application's version name")
                ""
            } else {
                packageInfo.versionName
            }
        }

        /**
         * Gets package label.
         *
         * @param context     valid context
         * @param packageName name of the package
         * @return package label.
         */
        fun getPackageLabel(context: Context, packageName: String): String {
            val packageManager = context.packageManager
            return try {
                val ai = packageManager.getApplicationInfo(packageName, 0)
                val applicationLabel = packageManager.getApplicationLabel(ai)
                applicationLabel?.toString()?.replace("\n".toRegex(), " ") ?: ""
            } catch (e: PackageManager.NameNotFoundException) {
                EchoLocateLog.eLogE("Fetching application label for package '" + packageName + "' caused: " + e.message)
                packageName
            } catch (e: Resources.NotFoundException) {
                EchoLocateLog.eLogE("Fetching application label for package '" + packageName + "' caused: " + e.message)
                packageName
            }

        }

    }
}