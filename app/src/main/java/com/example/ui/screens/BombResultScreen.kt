package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeonCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonSecondaryButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.SoundEffectManager
import com.example.util.UnityAdsManager
import com.example.viewmodel.GameUiState

@Composable
fun BombResultScreen(
  uiState: GameUiState,
  onPlayAgain: () -> Unit,
  onChangeTopic: () -> Unit,
  onRerollPunishment: () -> Unit,
  onShieldPunishment: () -> Unit,
  onGoHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val loser = uiState.bombLoser ?: uiState.players.firstOrNull()

  val infiniteTransition = rememberInfiniteTransition(label = "explosion_blast_glow")
  val blastScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "blast_scale"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 24.dp, vertical = 20.dp)
      .verticalScroll(rememberScrollState())
      .testTag("bomb_result_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      // Explosion Graphic
      Box(
        modifier = Modifier
          .size(110.dp)
          .scale(blastScale)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(
                NeonRed.copy(alpha = 0.35f),
                NeonOrange.copy(alpha = 0.2f),
                Color.Transparent
              )
            )
          )
          .border(2.5.dp, NeonRed, CircleShape)
          .shadow(32.dp, shape = CircleShape, ambientColor = NeonRed, spotColor = NeonOrange),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "💥",
          fontSize = 54.sp
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "BOOOOOOM! 💣💥",
        style = MaterialTheme.typography.displayLarge,
        color = NeonRed,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "انفجرت القنبلة بوجه: ${loser?.name ?: "الخاسر"} 😱",
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Round Details Summary Card
      NeonCard(
        borderColor = NeonRed.copy(alpha = 0.5f),
        backgroundColor = DarkSurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "التحدي كان:",
              color = TextSecondary,
              fontSize = 14.sp
            )
            Text(
              text = uiState.currentBombChallengeText,
              color = NeonOrange,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "عدد التمريرات الناجحة:",
              color = TextSecondary,
              fontSize = 14.sp
            )
            Text(
              text = "${uiState.bombPassCount} تمريرات 🔄",
              color = NeonCyan,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "ضحية الانفجار:",
              color = TextSecondary,
              fontSize = 14.sp
            )
            Text(
              text = "💥 ${loser?.name ?: ""}",
              color = NeonRed,
              fontSize = 15.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Punishment Card
      NeonCard(
        borderColor = NeonAmber.copy(alpha = 0.7f),
        backgroundColor = Color(0xFF2B160A),
        cornerRadius = 18.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "⚖️ حكم الخاسر (${loser?.name}):",
            style = MaterialTheme.typography.titleMedium,
            color = NeonAmber,
            fontWeight = FontWeight.Black
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = uiState.currentPunishment,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .clickable { onRerollPunishment() }
                .padding(horizontal = 8.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "تغيير الحكم 🎲",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (!uiState.isPunishmentShielded) {
              Row(
                modifier = Modifier
                  .weight(1.2f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(NeonAmber.copy(alpha = 0.2f))
                  .border(1.dp, NeonAmber, RoundedCornerShape(12.dp))
                  .clickable {
                    val act = context as? Activity
                    if (act != null) {
                      SoundEffectManager.playTapSound(context)
                      UnityAdsManager.showRewardedAd(
                        activity = act,
                        onRewardEarned = {
                          SoundEffectManager.playSuccessSound(context)
                          onShieldPunishment()
                          Toast.makeText(context, "🛡️ تم تفعيل درع الإعفاء!", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                          onShieldPunishment()
                        }
                      )
                    } else {
                      onShieldPunishment()
                    }
                  }
                  .padding(horizontal = 8.dp, vertical = 8.dp)
                  .testTag("bomb_shield_punishment_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = null,
                  tint = NeonAmber,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "إعفاء من الحكم 🎬",
                  color = NeonAmber,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // Action Buttons
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      NeonPrimaryButton(
        text = "جولة جديدة بنفس الفئة 🔥",
        onClick = onPlayAgain,
        icon = Icons.Default.Refresh,
        accentColor = NeonRed,
        testTag = "bomb_play_again_button"
      )

      NeonSecondaryButton(
        text = "تغيير الموضوع والتحدي 🎯",
        onClick = onChangeTopic,
        icon = Icons.Default.Category,
        borderColor = NeonOrange,
        testTag = "bomb_change_topic_button"
      )

      NeonSecondaryButton(
        text = "الرئيسية 🏠",
        onClick = onGoHome,
        icon = Icons.Default.Home,
        borderColor = DarkSurfaceBorder,
        testTag = "bomb_go_home_button"
      )
    }
  }
}
