package com.antigravity.distractionshield.data.repository

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.antigravity.distractionshield.domain.model.AppInfo
import com.antigravity.distractionshield.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepositoryImpl(
    private val context: Context,
    private val packageManager: PackageManager
) : AppRepository {
    private var cachedApps: List<AppInfo>? = null

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        cachedApps?.let { return@withContext it }
        try {
            val packages = packageManager.getInstalledPackages(0)
            val apps = packages.mapNotNull { pkg ->
                val launchIntent = packageManager.getLaunchIntentForPackage(pkg.packageName)
                if (launchIntent != null && pkg.packageName != context.packageName) {
                    val name = pkg.applicationInfo?.loadLabel(packageManager)?.toString() ?: pkg.packageName
                    AppInfo(
                        name = name,
                        packageName = pkg.packageName,
                        isBlocked = false
                    )
                } else {
                    null
                }
            }.sortedBy { it.name }
            cachedApps = apps
            apps
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun isUsageStatsPermissionGranted(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = appOps.noteOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun openUsageStatsSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
        } catch (e: Exception) {
            intent.data = null
            context.startActivity(intent)
        }
    }
}
