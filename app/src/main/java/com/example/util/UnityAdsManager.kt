package com.example.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds

object UnityAdsManager {
  private const val TAG = "UnityAdsManager"

  // Unity Ads Configuration from Unity Dashboard
  const val GAME_ID = "800366365"
  const val INTERSTITIAL_PLACEMENT_ID = "Interstitial_Android"
  const val REWARDED_PLACEMENT_ID = "Rewarded_Android"
  const val BANNER_PLACEMENT_ID = "Banner_Android"

  // Set to false for production release with real ads
  var testMode: Boolean = false

  private var isInitialized = false
  private var isInterstitialLoaded = false
  private var isRewardedLoaded = false

  // Counter to show interstitial ads at pleasant intervals (e.g., every 3 finished games)
  private var gamesFinishedCount = 0

  // Intelligent cooldown so players are never interrupted or spammed back-to-back
  private var lastInterstitialShowTime: Long = 0L
  private const val MIN_INTERSTITIAL_INTERVAL_MS = 150_000L // 2.5 minutes minimum between interstitials

  fun initialize(context: Context, onInitSuccess: () -> Unit = {}) {
    if (isInitialized) {
      loadInterstitial()
      loadRewarded()
      return
    }

    try {
      // Set mediation partner explicitly so SDK knows it's standalone Unity Ads (not mediation/header bidding)
      val metaData = com.unity3d.ads.metadata.MetaData(context.applicationContext)
      metaData.set("mediation.name", "None")
      metaData.set("mediation.adapter_version", "0.0.0")
      metaData.commit()
    } catch (e: Throwable) {
      Log.d(TAG, "MetaData setup: ${e.message}")
    }

    UnityAds.initialize(
      context.applicationContext,
      GAME_ID,
      testMode,
      object : IUnityAdsInitializationListener {
        override fun onInitializationComplete() {
          Log.d(TAG, "Unity Ads Initialization Complete for Game ID: $GAME_ID")
          isInitialized = true
          loadInterstitial()
          loadRewarded()
          onInitSuccess()
        }

        override fun onInitializationFailed(
          error: UnityAds.UnityAdsInitializationError?,
          message: String?
        ) {
          Log.w(TAG, "Unity Ads Initialization Failed: $error - $message")
        }
      }
    )
  }

  fun loadInterstitial() {
    if (!isInitialized || !UnityAds.isInitialized) return
    try {
      UnityAds.load(
        INTERSTITIAL_PLACEMENT_ID,
        object : IUnityAdsLoadListener {
          override fun onUnityAdsAdLoaded(placementId: String) {
            Log.d(TAG, "Interstitial Ad Loaded: $placementId")
            isInterstitialLoaded = true
          }

          override fun onUnityAdsFailedToLoad(
            placementId: String,
            error: UnityAds.UnityAdsLoadError,
            message: String
          ) {
            Log.w(TAG, "Interstitial Failed to Load: $placementId - $error: $message")
            isInterstitialLoaded = false
          }
        }
      )
    } catch (e: Exception) {
      Log.w(TAG, "Error loading interstitial: ${e.message}")
    }
  }

  fun loadRewarded() {
    if (!isInitialized || !UnityAds.isInitialized) return
    try {
      UnityAds.load(
        REWARDED_PLACEMENT_ID,
        object : IUnityAdsLoadListener {
          override fun onUnityAdsAdLoaded(placementId: String) {
            Log.d(TAG, "Rewarded Ad Loaded: $placementId")
            isRewardedLoaded = true
          }

          override fun onUnityAdsFailedToLoad(
            placementId: String,
            error: UnityAds.UnityAdsLoadError,
            message: String
          ) {
            Log.w(TAG, "Rewarded Ad Failed to Load: $placementId - $error: $message")
            isRewardedLoaded = false
          }
        }
      )
    } catch (e: Exception) {
      Log.w(TAG, "Error loading rewarded ad: ${e.message}")
    }
  }

  /**
   * Shows an interstitial ad between game rounds.
   * Only displays if enough rounds passed AND minimum interval has elapsed, ensuring non-disturbing UX.
   */
  fun showInterstitialAfterGame(
    activity: Activity,
    forceShow: Boolean = false,
    onAdDismissed: () -> Unit = {}
  ) {
    gamesFinishedCount++
    val now = System.currentTimeMillis()
    val elapsedSinceLast = now - lastInterstitialShowTime

    if (forceShow || (gamesFinishedCount % 3 == 0 && elapsedSinceLast >= MIN_INTERSTITIAL_INTERVAL_MS)) {
      showInterstitial(activity, onAdDismissed)
    } else {
      onAdDismissed()
    }
  }

  fun showInterstitial(
    activity: Activity,
    onAdDismissed: () -> Unit = {}
  ) {
    if (!isInitialized) {
      onAdDismissed()
      return
    }

    lastInterstitialShowTime = System.currentTimeMillis()

    try {
      UnityAds.show(
        activity,
        INTERSTITIAL_PLACEMENT_ID,
        object : IUnityAdsShowListener {
          override fun onUnityAdsShowFailure(
            placementId: String,
            error: UnityAds.UnityAdsShowError,
            message: String
          ) {
            Log.e(TAG, "Unity Ads Show Failure: $placementId - $error: $message")
            loadInterstitial() // preload for next time
            onAdDismissed()
          }

          override fun onUnityAdsShowStart(placementId: String) {
            Log.d(TAG, "Unity Ads Show Start: $placementId")
          }

          override fun onUnityAdsShowClick(placementId: String) {
            Log.d(TAG, "Unity Ads Clicked: $placementId")
          }

          override fun onUnityAdsShowComplete(
            placementId: String,
            state: UnityAds.UnityAdsShowCompletionState
          ) {
            Log.d(TAG, "Unity Ads Show Complete: $placementId state=$state")
            loadInterstitial() // preload for next time
            onAdDismissed()
          }
        }
      )
    } catch (e: Exception) {
      Log.e(TAG, "Exception showing interstitial: ${e.message}")
      onAdDismissed()
    }
  }

  /**
   * Shows a rewarded video ad. Invokes [onRewardEarned] if the user watched the full video.
   * Player-friendly: if in test mode or ad fails, grants reward so player is not disappointed.
   */
  fun showRewardedAd(
    activity: Activity,
    onRewardEarned: () -> Unit,
    onAdClosed: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    if (!isInitialized) {
      Log.w(TAG, "Unity Ads not initialized yet. Granting test reward for smooth play.")
      onRewardEarned()
      onAdClosed()
      return
    }

    try {
      UnityAds.show(
        activity,
        REWARDED_PLACEMENT_ID,
        object : IUnityAdsShowListener {
          override fun onUnityAdsShowFailure(
            placementId: String,
            error: UnityAds.UnityAdsShowError,
            message: String
          ) {
            Log.e(TAG, "Rewarded Ad Show Failure: $placementId - $error: $message")
            loadRewarded() // retry preload
            // Don't penalize user for network/SDK load failures
            onRewardEarned()
            onError(message)
            onAdClosed()
          }

          override fun onUnityAdsShowStart(placementId: String) {
            Log.d(TAG, "Rewarded Ad Show Start: $placementId")
          }

          override fun onUnityAdsShowClick(placementId: String) {
            Log.d(TAG, "Rewarded Ad Click: $placementId")
          }

          override fun onUnityAdsShowComplete(
            placementId: String,
            state: UnityAds.UnityAdsShowCompletionState
          ) {
            Log.d(TAG, "Rewarded Ad Completed with state: $state")
            loadRewarded() // preload next
            if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
              onRewardEarned()
            }
            onAdClosed()
          }
        }
      )
    } catch (e: Exception) {
      Log.e(TAG, "Exception showing rewarded ad: ${e.message}")
      onRewardEarned()
      onAdClosed()
    }
  }
}
