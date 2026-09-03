package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BombTopic
import com.example.model.GameMasterData
import com.example.ui.components.NeonCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun BombTopicSelectionScreen(
  uiState: GameUiState,
  onSelectTopic: (BombTopic) -> Unit,
  onPromptVipUnlock: (BombTopic) -> Unit,
  onBack: () -> Unit,
  onGoHome: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "bomb_selection_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
      .testTag("bomb_topic_selection_screen"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, CircleShape)
            .testTag("back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "رجوع",
            tint = TextPrimary
          )
        }

        IconButton(
          onClick = onGoHome,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(DarkSurfaceElevated)
            .border(1.dp, NeonPurple.copy(alpha = 0.6f), CircleShape)
            .testTag("bomb_topics_home_button")
        ) {
          Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "الرئيسية",
            tint = NeonPurple
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(NeonRed.copy(alpha = 0.15f))
          .border(1.dp, NeonRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Text(
          text = "💣 اختر موضوع القنبلة",
          color = NeonRed,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Random Topic Pick button
      IconButton(
        onClick = {
          val randomTopic = GameMasterData.bombTopics.random()
          onSelectTopic(randomTopic)
        },
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(DarkSurfaceElevated)
          .border(1.dp, NeonOrange.copy(alpha = 0.5f), CircleShape)
          .testTag("random_topic_button")
      ) {
        Icon(
          imageVector = Icons.Default.Casino,
          contentDescription = "اختيار عشوائي",
          tint = NeonOrange
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Header Title
    Text(
      text = "ما هو تحدي هذه الجولة؟",
      style = MaterialTheme.typography.displayMedium,
      color = TextPrimary,
      fontWeight = FontWeight.Black,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "اختر فئة الكلمات واضغط لتبدأ القنبلة بالاشتعال فوراً! 🔥",
      style = MaterialTheme.typography.bodyMedium,
      color = TextSecondary,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Topics Grid
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(GameMasterData.bombTopics) { topic ->
        val isUnlocked = !topic.isVip || uiState.isAllVipUnlocked || uiState.unlockedPackIds.contains(topic.id)
        BombTopicCard(
          topic = topic,
          isUnlocked = isUnlocked,
          onClick = {
            if (isUnlocked) {
              onSelectTopic(topic)
            } else {
              onPromptVipUnlock(topic)
            }
          }
        )
      }
    }
  }
}

@Composable
private fun BombTopicCard(
  topic: BombTopic,
  isUnlocked: Boolean,
  onClick: () -> Unit
) {
  NeonCard(
    borderColor = if (topic.isVip && !isUnlocked) NeonAmber.copy(alpha = 0.7f) else if (topic.id == "bt_random_letter") NeonRed.copy(alpha = 0.8f) else DarkSurfaceBorder,
    backgroundColor = DarkSurface,
    cornerRadius = 20.dp,
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("topic_${topic.id}")
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Icon Hub
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  if (topic.isVip) NeonAmber.copy(alpha = 0.25f) else if (topic.id == "bt_random_letter") NeonRed.copy(alpha = 0.25f) else NeonOrange.copy(alpha = 0.15f),
                  Color.Transparent
                )
              )
            )
            .border(
              1.5.dp,
              if (topic.isVip) NeonAmber else if (topic.id == "bt_random_letter") NeonRed else DarkSurfaceBorder,
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = topic.icon,
            fontSize = 32.sp
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = topic.title,
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (topic.isVip && !isUnlocked) {
          Text(
            text = "🔒 فتح الحزمة (إعلانين)",
            color = NeonAmber,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
          )
        } else {
          Text(
            text = topic.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            lineHeight = 14.sp
          )
        }
      }

      if (topic.isVip) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isUnlocked) Color(0xFF1B382B) else NeonAmber.copy(alpha = 0.25f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (isUnlocked) "👑 مفعل" else "👑 VIP",
            color = if (isUnlocked) Color(0xFF00E676) else NeonAmber,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
