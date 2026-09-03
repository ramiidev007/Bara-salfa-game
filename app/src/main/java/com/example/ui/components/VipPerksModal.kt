package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

data class VipPackCatalogItem(
  val id: String,
  val title: String,
  val icon: String,
  val modeName: String,
  val description: String
)

val VIP_CATALOG: List<VipPackCatalogItem> = listOf(
  VipPackCatalogItem(
    id = "tod_vip_secrets",
    title = "أسرار وفضائح VIP 🔮🔥",
    icon = "🔮",
    modeName = "صراحة أو جرأة",
    description = "أسئلة محرجة جداً وتحديات مجنونة تفجر الجلسة ضحك!"
  ),
  VipPackCatalogItem(
    id = "charades_vip_legends",
    title = "أساطير وسينما VIP 🌟",
    icon = "🌟",
    modeName = "تمثيل بدون كلام",
    description = "شخصيات خارقة وأفلام عالمية مشهورة للتحدي والتمثيل."
  ),
  VipPackCatalogItem(
    id = "bt_vip_speed",
    title = "تحدي العباقرة VIP ⚡",
    icon = "⚡",
    modeName = "القنبلة الموقوتة",
    description = "مخترعين وتقنيات وسرعة بديهة تحت ضغط انفجار القنبلة!"
  ),
  VipPackCatalogItem(
    id = "c_vip_travel",
    title = "سفر وسياحة ✈️",
    icon = "✈️",
    modeName = "برا السالفة",
    description = "معالم سياحية، مطارات، وجزر سياحية شهيرة حول العالم."
  ),
  VipPackCatalogItem(
    id = "c_vip_cinema",
    title = "سينما ومشاهير 🍿",
    icon = "🍿",
    modeName = "برا السالفة",
    description = "شخصيات وأفلام ومسلسلات عالمية وسجادة حمراء."
  ),
  VipPackCatalogItem(
    id = "c_vip_history",
    title = "شخصيات وتاريخ 👑",
    icon = "👑",
    modeName = "برا السالفة",
    description = "ملوك وقادة تاريخيين وأحداث مفصلية عبر التاريخ."
  )
)

@Composable
fun VipPerksModal(
  unlockedPacks: Set<String>,
  packProgressMap: Map<String, Int>,
  onUnlockPackClicked: (packId: String, title: String, icon: String, description: String) -> Unit,
  onDismiss: () -> Unit
) {
  val totalPacks = VIP_CATALOG.size
  val unlockedCount = VIP_CATALOG.count { unlockedPacks.contains(it.id) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = true,
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
          brush = Brush.linearGradient(listOf(NeonAmber, NeonPurple)),
          shape = RoundedCornerShape(24.dp)
        )
        .shadow(32.dp, RoundedCornerShape(24.dp), ambientColor = NeonAmber, spotColor = NeonPurple)
        .padding(20.dp)
        .testTag("vip_perks_modal"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NeonAmber.copy(alpha = 0.2f))
                .border(1.dp, NeonAmber, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "👑", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "دليل حزم ومكافآت VIP",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "فتح فردي ومستقل لكل حزمة 🎮",
                style = MaterialTheme.typography.bodySmall,
                color = NeonAmber
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "إغلاق",
              tint = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Overview Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "الحزم المفتوحة: $unlockedCount من أصل $totalPacks",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "كل حزمة تتطلب مشاهدة إعلانين فقط وتظل مفتوحة لك دائماً!",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (unlockedCount == totalPacks) NeonGreen.copy(alpha = 0.2f) else NeonAmber.copy(alpha = 0.2f))
                .border(1.dp, if (unlockedCount == totalPacks) NeonGreen else NeonAmber, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "$unlockedCount / $totalPacks",
                color = if (unlockedCount == totalPacks) NeonGreen else NeonAmber,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: VIP Packs
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "اختر الحزمة لفتحها (إعلانين فقط لكل حزمة) ⭐",
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Catalog List
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          VIP_CATALOG.forEach { item ->
            val isUnlocked = unlockedPacks.contains(item.id)
            val progress = if (isUnlocked) PerksManager.REQUIRED_ADS_PER_PACK else (packProgressMap[item.id] ?: 0)

            VipCatalogItemCard(
              item = item,
              isUnlocked = isUnlocked,
              progress = progress,
              requiredAds = PerksManager.REQUIRED_ADS_PER_PACK,
              onCardClicked = {
                if (!isUnlocked) {
                  onDismiss()
                  onUnlockPackClicked(item.id, item.title, item.icon, item.description)
                }
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section Title: Instant In-Game Lifelines
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "مساعدات اللعب المجانية (أثناء الجولة) 🛡️",
            color = NeonAmber,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        PerkFeatureRow(
          icon = "🛡️",
          title = "درع الإعفاء من العقاب",
          subtitle = "يظهر في شاشة النتائج عند خسارة الجولة لإلغاء أي حكم فوراً بمشاهدة إعلان اختياري."
        )

        Spacer(modifier = Modifier.height(8.dp))

        PerkFeatureRow(
          icon = "💡",
          title = "مساعدة 50:50 للجاسوس",
          subtitle = "في برا السالفة، يستطيع الجاسوس حذف خيارين خاطئين لزيادة فرصته في الفوز والنجاة."
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Zero Disturbance Badge
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated.copy(alpha = 0.5f))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = NeonGreen,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "لعب ممتع ونظيف بدون إزعاج ✨",
                color = NeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "أنت تتحكم دائماً بما تشاهده، ولا توجد إعلانات أثناء التمرير أو وقت التوتر.",
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        NeonSecondaryButton(
          text = "تم 🎮",
          onClick = onDismiss,
          borderColor = DarkSurfaceBorder,
          modifier = Modifier.fillMaxWidth(),
          testTag = "close_vip_perks_modal_button"
        )
      }
    }
  }
}

@Composable
private fun VipCatalogItemCard(
  item: VipPackCatalogItem,
  isUnlocked: Boolean,
  progress: Int,
  requiredAds: Int,
  onCardClicked: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurfaceElevated)
      .border(
        width = 1.dp,
        color = when {
          isUnlocked -> NeonGreen.copy(alpha = 0.4f)
          progress > 0 -> NeonAmber.copy(alpha = 0.6f)
          else -> DarkSurfaceBorder
        },
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(enabled = !isUnlocked) { onCardClicked() }
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Icon
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(DarkSurface)
          .border(
            1.dp,
            if (isUnlocked) NeonGreen else if (progress > 0) NeonAmber else DarkSurfaceBorder,
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(text = item.icon, fontSize = 22.sp)
      }

      // Details
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = item.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(NeonPurple.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = item.modeName,
              color = NeonPurple,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = item.description,
          color = TextMuted,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }

      // Status / Action Button
      if (isUnlocked) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NeonGreen.copy(alpha = 0.15f))
            .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = NeonGreen,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "مفتوحة",
              color = NeonGreen,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      } else {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (progress > 0) NeonAmber.copy(alpha = 0.2f) else DarkSurface)
            .border(
              1.dp,
              if (progress > 0) NeonAmber else NeonCyan.copy(alpha = 0.5f),
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (progress > 0) Icons.Default.PlayArrow else Icons.Default.Lock,
              contentDescription = null,
              tint = if (progress > 0) NeonAmber else NeonCyan,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = if (progress > 0) "إكمال ($progress/$requiredAds) 🎬" else "فتح (إعلانين) 🎬",
              color = if (progress > 0) NeonAmber else NeonCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PerkFeatureRow(
  icon: String,
  title: String,
  subtitle: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceElevated.copy(alpha = 0.6f))
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = icon, fontSize = 20.sp)
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = subtitle,
        color = TextMuted,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )
    }
  }
}
