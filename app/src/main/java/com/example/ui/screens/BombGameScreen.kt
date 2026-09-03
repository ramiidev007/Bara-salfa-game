package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.ui.components.NeonCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.RulesModal
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextDisabled
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun BombGameScreen(
  uiState: GameUiState,
  onPassBomb: () -> Unit,
  onRerollChallenge: () -> Unit,
  onBackToTopics: () -> Unit,
  onGoHome: () -> Unit,
  onShowRules: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val currentHolder = uiState.players.getOrNull(uiState.currentBombHolderIndex)
    ?: uiState.players.firstOrNull()

  // Calculate timer values
  val totalFuse = uiState.bombTotalFuseSeconds.coerceAtLeast(1)
  val elapsed = uiState.bombSecondsElapsed.coerceIn(0, totalFuse)
  val remainingSeconds = (totalFuse - elapsed).coerceAtLeast(0)
  val progressFactor = (elapsed.toFloat() / totalFuse.toFloat()).coerceIn(0f, 1f)
  val remainingProgress = 1f - progressFactor

  val pulseDuration = (900 - (progressFactor * 650)).toInt().coerceAtLeast(180)

  val infiniteTransition = rememberInfiniteTransition(label = "bomb_ticking_transition")

  val bombHeartbeat by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(pulseDuration, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bomb_heartbeat"
  )

  val sparkRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "spark_rotation"
  )

  val sparkGlow by infiniteTransition.animateFloat(
    initialValue = 0.75f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(280, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "spark_glow"
  )

  val timerPulseScale by infiniteTransition.animateFloat(
    initialValue = 0.97f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (remainingSeconds <= 10) 300 else 600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "timer_pulse_scale"
  )

  val dynamicGlowColor = when {
    remainingSeconds <= 10 -> NeonRed
    remainingSeconds <= 20 -> NeonOrange
    else -> NeonAmber
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 14.dp)
        .verticalScroll(rememberScrollState())
        .testTag("bomb_game_screen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Navigation Bar with Back, Help, and Direct Home Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Back to topics
          IconButton(
            onClick = onBackToTopics,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(DarkSurfaceElevated)
              .border(1.dp, DarkSurfaceBorder, CircleShape)
              .testTag("bomb_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "الرجوع للمواضيع",
              tint = TextPrimary
            )
          }

          // Return to Main Menu / Choose Game
          IconButton(
            onClick = onGoHome,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(DarkSurfaceElevated)
              .border(1.dp, NeonPurple.copy(alpha = 0.6f), CircleShape)
              .testTag("bomb_go_home_header_button")
          ) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = "القائمة الرئيسية",
              tint = NeonPurple
            )
          }
        }

        // Title Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(dynamicGlowColor.copy(alpha = 0.15f))
            .border(1.2.dp, dynamicGlowColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "💣 القنبلة الموقوتة",
            color = dynamicGlowColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Rules button
        IconButton(
          onClick = { onShowRules(true) },
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, CircleShape)
            .testTag("bomb_rules_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = "قواعد اللعبة",
            tint = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Timer Countdown Display & Burning Fuse Progress Bar
      NeonCard(
        borderColor = dynamicGlowColor.copy(alpha = 0.8f),
        backgroundColor = DarkSurface,
        cornerRadius = 20.dp,
        modifier = Modifier
          .fillMaxWidth()
          .shadow(16.dp, shape = RoundedCornerShape(20.dp), ambientColor = dynamicGlowColor)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Timer Badge Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (remainingSeconds <= 10) "⚠️ اقترب الانفجار!" else "⏱️ عداد الفتيل",
                color = dynamicGlowColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }

            // Digital Remaining Seconds Counter
            Box(
              modifier = Modifier
                .scale(if (remainingSeconds <= 10) timerPulseScale else 1f)
                .clip(RoundedCornerShape(12.dp))
                .background(dynamicGlowColor.copy(alpha = 0.2f))
                .border(1.2.dp, dynamicGlowColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${remainingSeconds} ثانية ⏳",
                color = dynamicGlowColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Visual Burning Fuse Progress Bar
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(14.dp)
              .clip(RoundedCornerShape(7.dp))
              .background(DarkSurfaceElevated)
              .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(7.dp))
          ) {
            // Fuse remaining line
            Box(
              modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(remainingProgress)
                .clip(RoundedCornerShape(7.dp))
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      dynamicGlowColor,
                      NeonOrange,
                      NeonAmber
                    )
                  )
                )
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = if (remainingSeconds <= 10) "🔥 الفتيل على وشك الانتهاء! أجب بسرعة! 🔥" else "اذكر إجابتك ومرر القنبلة قبل أن ينفد الوقت!",
            color = if (remainingSeconds <= 10) NeonRed else TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Challenge Target Card
      NeonCard(
        borderColor = NeonOrange.copy(alpha = 0.6f),
        backgroundColor = DarkSurfaceElevated,
        cornerRadius = 18.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "التحدي المطلوب 🎯",
              color = TextMuted,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )

            IconButton(
              onClick = onRerollChallenge,
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(DarkSurface)
                .border(1.dp, NeonOrange.copy(alpha = 0.5f), CircleShape)
                .testTag("reroll_challenge_button")
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "تغيير التحدي",
                tint = NeonOrange,
                modifier = Modifier.size(15.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = uiState.currentBombChallengeText,
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = uiState.selectedBombTopic?.description ?: "اذكر كلمة بدون تكرار!",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Centerpiece: Ticking Neon Bomb Animation
      Box(
        modifier = Modifier
          .size(210.dp)
          .testTag("animated_ticking_bomb"),
        contentAlignment = Alignment.Center
      ) {
        // Outer blast pulse
        Box(
          modifier = Modifier
            .size(190.dp)
            .scale(bombHeartbeat)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  dynamicGlowColor.copy(alpha = 0.28f),
                  Color.Transparent
                )
              )
            )
            .border(2.dp, dynamicGlowColor.copy(alpha = 0.4f), CircleShape)
        )

        // Inner bomb body
        Box(
          modifier = Modifier
            .size(140.dp)
            .scale(bombHeartbeat)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  Color(0xFF2E0C10),
                  DarkSurface
                )
              )
            )
            .border(2.5.dp, dynamicGlowColor, CircleShape)
            .shadow(28.dp, shape = CircleShape, ambientColor = dynamicGlowColor, spotColor = NeonRed)
        )

        // Bomb Emoji Icon
        Text(
          text = "💣",
          fontSize = 64.sp,
          modifier = Modifier.scale(bombHeartbeat)
        )

        // Burning Fuse Spark at top right
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-18).dp, y = 18.dp)
            .scale(sparkGlow)
            .rotate(sparkRotation)
        ) {
          Text(text = "✨", fontSize = 30.sp)
        }

        // Fire flame at top right
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-8).dp, y = 6.dp)
        ) {
          Text(text = "🔥", fontSize = 26.sp)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Current Player Holding Bomb Indicator & Pass Button
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Who holds the bomb
        AnimatedContent(
          targetState = currentHolder?.name ?: "",
          transitionSpec = {
            (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
          },
          label = "current_holder_transition"
        ) { holderName ->
          NeonCard(
            borderColor = dynamicGlowColor,
            backgroundColor = DarkSurfaceElevated,
            cornerRadius = 16.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "القنبلة بيدك الآن يا:",
                  color = TextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = "💣 $holderName",
                  color = dynamicGlowColor,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(DarkSurface)
                  .padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "تمريرات: ${uiState.bombPassCount} 🔄",
                  color = NeonCyan,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // Primary Action: Pass Bomb
        NeonPrimaryButton(
          text = "قلت الكلمة! مرر القنبلة 💥",
          onClick = onPassBomb,
          icon = Icons.Default.FastForward,
          accentColor = dynamicGlowColor,
          testTag = "pass_bomb_button",
          modifier = Modifier.height(58.dp)
        )

        // Direct return to home button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface.copy(alpha = 0.7f))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
            .clickable { onGoHome() }
            .padding(vertical = 10.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "إنهاء والعودة للقائمة الرئيسية واختيار لعبة 🏠",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Rules Modal
    if (uiState.showRulesModal) {
      RulesModal(
        gameMode = GameMode.WORD_BOMB,
        onDismiss = { onShowRules(false) }
      )
    }
  }
}

