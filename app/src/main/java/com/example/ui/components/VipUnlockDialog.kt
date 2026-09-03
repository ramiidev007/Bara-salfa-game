package com.example.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.PerksManager
import com.example.util.SoundEffectManager
import com.example.util.UnityAdsManager

@Composable
fun VipUnlockDialog(
  packId: String,
  title: String,
  icon: String,
  description: String,
  currentProgress: Int = 0,
  requiredAds: Int = PerksManager.REQUIRED_ADS_PER_PACK,
  onWatchAdSuccess: (String) -> Pair<Int, Boolean>,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val activity = context as? Activity
  var isLoadingAd by remember { mutableStateOf(false) }
  var localProgress by remember(packId, currentProgress) { mutableStateOf(currentProgress) }
  var isFullyUnlocked by remember(packId, currentProgress) { mutableStateOf(currentProgress >= requiredAds) }

  val animatedProgressFraction by animateFloatAsState(
    targetValue = (localProgress.toFloat() / requiredAds).coerceIn(0f, 1f),
    label = "unlock_progress_anim"
  )

  Dialog(
    onDismissRequest = {
      if (!isLoadingAd) onDismiss()
    },
    properties = DialogProperties(
      dismissOnBackPress = !isLoadingAd,
      dismissOnClickOutside = !isLoadingAd,
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .background(DarkSurface)
        .border(
          width = 1.5.dp,
          brush = Brush.linearGradient(listOf(NeonAmber, NeonOrange)),
          shape = RoundedCornerShape(24.dp)
        )
        .shadow(32.dp, RoundedCornerShape(24.dp), ambientColor = NeonAmber, spotColor = NeonOrange)
        .padding(22.dp)
        .testTag("vip_unlock_dialog"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Icon Badge
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(NeonAmber.copy(alpha = 0.35f), Color.Transparent)
              )
            )
            .border(2.dp, NeonAmber, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = icon, fontSize = 34.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // VIP Tag
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(NeonAmber.copy(alpha = 0.15f))
            .border(1.dp, NeonAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = NeonAmber,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "حزمة VIP حصرية",
            color = NeonAmber,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Description
        Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp,
          modifier = Modifier.padding(horizontal = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2-Stage Progress Tracker Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isFullyUnlocked) "الحزمة مفتوحة بالكامل! ✅" else "التقدم: $localProgress من $requiredAds إعلانات",
                color = if (isFullyUnlocked) NeonGreen else NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (isFullyUnlocked) "100%" else "${(animatedProgressFraction * 100).toInt()}%",
                color = if (isFullyUnlocked) NeonGreen else NeonAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkSurfaceBorder)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(animatedProgressFraction)
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(
                    Brush.horizontalGradient(
                      if (isFullyUnlocked) listOf(NeonGreen, NeonCyan) else listOf(NeonAmber, NeonOrange)
                    )
                  )
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Step Badges
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Step 1
              val step1Complete = localProgress >= 1
              Row(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (step1Complete) NeonGreen.copy(alpha = 0.15f) else DarkSurface.copy(alpha = 0.6f))
                  .border(
                    1.dp,
                    if (step1Complete) NeonGreen.copy(alpha = 0.6f) else DarkSurfaceBorder,
                    RoundedCornerShape(10.dp)
                  )
                  .padding(vertical = 8.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                if (step1Complete) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "إعلان 1 (تم ✓)",
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                } else {
                  Text(
                    text = "1️⃣ إعلان 1",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              // Step 2
              val step2Complete = localProgress >= 2
              Row(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    when {
                      step2Complete -> NeonGreen.copy(alpha = 0.15f)
                      step1Complete -> NeonAmber.copy(alpha = 0.15f)
                      else -> DarkSurface.copy(alpha = 0.6f)
                    }
                  )
                  .border(
                    1.dp,
                    when {
                      step2Complete -> NeonGreen.copy(alpha = 0.6f)
                      step1Complete -> NeonAmber.copy(alpha = 0.6f)
                      else -> DarkSurfaceBorder
                    },
                    RoundedCornerShape(10.dp)
                  )
                  .padding(vertical = 8.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                if (step2Complete) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "إعلان 2 (تم ✓)",
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                } else {
                  Text(
                    text = if (step1Complete) "🔥 إعلان 2 (الأخير)" else "2️⃣ إعلان 2",
                    color = if (step1Complete) NeonAmber else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = when {
                isFullyUnlocked -> "🎉 استمتع بالحزمة! ستبقى مفتوحة لك ولأصدقائك دائماً."
                localProgress == 1 -> "⚡ خطوة واحدة متبقية! شاهد الإعلان الثاني وسيتم فتح الحزمة فوراً!"
                else -> "💡 كل حزمة تتطلب إعلانين فقط. يمكنك إكمالها الآن أو حفظ تقدمك للعودة لاحقاً."
              },
              color = if (localProgress == 1 && !isFullyUnlocked) NeonAmber else TextMuted,
              fontSize = 11.sp,
              lineHeight = 16.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons
        if (isLoadingAd) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp)
          ) {
            CircularProgressIndicator(
              color = NeonAmber,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "جاري تشغيل الإعلان وتجهيز التقدم...",
              color = TextSecondary,
              fontSize = 13.sp
            )
          }
        } else if (isFullyUnlocked) {
          NeonPrimaryButton(
            text = "ابدأ اللعب بالحزمة الآن ✅",
            onClick = {
              SoundEffectManager.playTapSound(context)
              onDismiss()
            },
            accentColor = NeonGreen,
            modifier = Modifier.fillMaxWidth(),
            testTag = "start_playing_unlocked_pack_button"
          )
        } else {
          val buttonText = if (localProgress == 0) {
            "مشاهدة الإعلان الأول (1/2) 🎬"
          } else {
            "مشاهدة الإعلان الأخير (2/2) وفتح الحزمة! 🔥🎬"
          }

          NeonPrimaryButton(
            text = buttonText,
            onClick = {
              if (activity != null) {
                isLoadingAd = true
                SoundEffectManager.playTapSound(context)
                UnityAdsManager.showRewardedAd(
                  activity = activity,
                  onRewardEarned = {
                    isLoadingAd = false
                    val (newProg, unlocked) = onWatchAdSuccess(packId)
                    localProgress = newProg
                    isFullyUnlocked = unlocked
                    SoundEffectManager.playSuccessSound(context)
                    if (unlocked) {
                      Toast.makeText(context, "🎉 مبروك! تم فتح $title بالكامل!", Toast.LENGTH_SHORT).show()
                    } else {
                      Toast.makeText(context, "🌟 أحسنت! شاهد إعلاناً واحداً إضافياً فقط لفتح الحزمة نهائياً!", Toast.LENGTH_SHORT).show()
                    }
                  },
                  onAdClosed = {
                    isLoadingAd = false
                  },
                  onError = { _ ->
                    isLoadingAd = false
                    val (newProg, unlocked) = onWatchAdSuccess(packId)
                    localProgress = newProg
                    isFullyUnlocked = unlocked
                    SoundEffectManager.playSuccessSound(context)
                    if (unlocked) {
                      Toast.makeText(context, "🎉 تم فتح $title لك ولأصدقائك!", Toast.LENGTH_SHORT).show()
                    } else {
                      Toast.makeText(context, "🌟 تم تسجيل تقدمك! شاهد إعلاناً واحداً إضافياً لفتح الحزمة!", Toast.LENGTH_SHORT).show()
                    }
                  }
                )
              } else {
                val (newProg, unlocked) = onWatchAdSuccess(packId)
                localProgress = newProg
                isFullyUnlocked = unlocked
              }
            },
            icon = Icons.Default.PlayCircle,
            accentColor = if (localProgress == 1) NeonAmber else NeonCyan,
            modifier = Modifier.fillMaxWidth(),
            testTag = "watch_ad_unlock_button"
          )

          Spacer(modifier = Modifier.height(10.dp))

          NeonSecondaryButton(
            text = if (localProgress > 0) "إغلاق (تقدمك محفوظ 💾)" else "لاحقاً",
            onClick = onDismiss,
            borderColor = DarkSurfaceBorder,
            modifier = Modifier.fillMaxWidth(),
            testTag = "dismiss_vip_dialog_button"
          )
        }
      }
    }
  }
}
