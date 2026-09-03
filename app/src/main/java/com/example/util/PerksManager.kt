package com.example.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PerksManager {
  private const val PREFS_NAME = "salfa_perks_prefs"
  private const val KEY_UNLOCKED_PACKS = "unlocked_packs"
  private const val KEY_PACK_PROGRESS_PREFIX = "pack_progress_"
  const val REQUIRED_ADS_PER_PACK = 2

  private var prefs: SharedPreferences? = null

  private val _unlockedPacksState = MutableStateFlow<Set<String>>(emptySet())
  val unlockedPacksState: StateFlow<Set<String>> = _unlockedPacksState.asStateFlow()

  private val _packProgressState = MutableStateFlow<Map<String, Int>>(emptyMap())
  val packProgressState: StateFlow<Map<String, Int>> = _packProgressState.asStateFlow()

  fun initialize(context: Context) {
    if (prefs == null) {
      prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      val savedPacks = prefs?.getStringSet(KEY_UNLOCKED_PACKS, emptySet())?.toSet() ?: emptySet()
      _unlockedPacksState.value = savedPacks

      val progressMap = mutableMapOf<String, Int>()
      prefs?.all?.forEach { (key, value) ->
        if (key.startsWith(KEY_PACK_PROGRESS_PREFIX) && value is Int) {
          val packId = key.removePrefix(KEY_PACK_PROGRESS_PREFIX)
          progressMap[packId] = value
        }
      }
      _packProgressState.value = progressMap
    }
  }

  fun isPackUnlocked(packId: String, isVip: Boolean): Boolean {
    if (!isVip) return true
    return _unlockedPacksState.value.contains(packId)
  }

  fun getPackAdProgress(packId: String): Int {
    if (_unlockedPacksState.value.contains(packId)) return REQUIRED_ADS_PER_PACK
    return _packProgressState.value[packId] ?: 0
  }

  /**
   * Registers one rewarded ad watched for this specific pack.
   * Returns Pair(newProgress, isNowFullyUnlocked).
   */
  fun registerAdWatchedForPack(packId: String): Pair<Int, Boolean> {
    val currentProgress = getPackAdProgress(packId)
    val newProgress = (currentProgress + 1).coerceAtMost(REQUIRED_ADS_PER_PACK)

    val updatedProgressMap = _packProgressState.value.toMutableMap()
    updatedProgressMap[packId] = newProgress
    _packProgressState.value = updatedProgressMap

    prefs?.edit()?.putInt("$KEY_PACK_PROGRESS_PREFIX$packId", newProgress)?.apply()

    if (newProgress >= REQUIRED_ADS_PER_PACK) {
      val updatedPacks = _unlockedPacksState.value + packId
      _unlockedPacksState.value = updatedPacks
      prefs?.edit()?.putStringSet(KEY_UNLOCKED_PACKS, updatedPacks)?.apply()
      return Pair(newProgress, true)
    }

    return Pair(newProgress, false)
  }

  fun unlockPackDirectly(packId: String) {
    val updated = _unlockedPacksState.value + packId
    _unlockedPacksState.value = updated
    val updatedMap = _packProgressState.value.toMutableMap()
    updatedMap[packId] = REQUIRED_ADS_PER_PACK
    _packProgressState.value = updatedMap

    prefs?.edit()
      ?.putStringSet(KEY_UNLOCKED_PACKS, updated)
      ?.putInt("$KEY_PACK_PROGRESS_PREFIX$packId", REQUIRED_ADS_PER_PACK)
      ?.apply()
  }
}

