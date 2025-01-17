package com.baset.ai.dict.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class NetworkMonitor(context: Context) {
    private val connectivityManager by lazyFast {
        ContextCompat.getSystemService(context, ConnectivityManager::class.java)
    }

    enum class State {
        Available, NotAvailable, Unknown
    }

    val networkMonitorFlow
        get() = callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(State.Available)
                }

                override fun onLost(network: Network) {
                    trySend(State.NotAvailable)
                }
            }

            connectivityManager?.registerDefaultNetworkCallback(networkCallback)
            awaitClose { connectivityManager?.unregisterNetworkCallback(networkCallback) }
        }
}