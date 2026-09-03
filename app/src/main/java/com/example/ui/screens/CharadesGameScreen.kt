package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.model.GameMode
import com.example.ui.components.NeonCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonSecondaryButton
import com.example.ui.components.RulesModal
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun CharadesGameScreen(
  uiState: GameUiState,
  onStartTimer: () -> Unit,
  onPauseTimer: () -> Unit,
  onToggleWordReveal: () -> Unit,
  onCorrectGuess: () -> Unit,
  onSkipWord: () -> Unit,
  onEndTurn: () -> Unit,
  onBackToSetup: () -> Unit,
  onGoHome: () -> Unit,
  onShowRules: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val isTimerRunning = uiState.charadesIsTimerRunning
  val secondsRemaining = uiState.charadesTimerSecondsRemaining
  val totalDuration = uiState.charadesDurationSeconds
  val isWordRevealed = uiState.charadesIsWordRevealed || isTimerRunning

  val currentTeam = if (uiState.charadesCurrentTeamTurnIndex == 0) uiState.charadesTeamA else uiState.charadesTeamB
  val teamColor = if (uiState.charadesCurrentTeamTurnIndex == 0) NeonAmber else NeonCyan

  val actorPlayer = if (uiState.players.isNotEmpty()) {
    uiState.players[uiState.charadesCurrentActorPlayerIndex % uiState.players.size]
  } else null

  val isUrgent = secondsRemaining <= 10 && isTimerRunning

  val infiniteTransition = rememberInfiniteTransition(label = "urgent_pulse")
  val urgentScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "urgent_scale"
  )

  val timerProgress = (secondsRemaining.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)

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
        .testTag("charades_game_screen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Bar Navigation
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
            onClick = onBackToSetup,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(DarkSurfaceElevated)
              .border(1.dp, DarkSurfaceBorder, CircleShape)
              .testTag("charades_game_back_button")
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
              .size(40.dp)
              .clip(CircleShape)
              .background(DarkSurfaceElevated)
              .border(1.dp, NeonPurple.copy(alpha = 0.6f), CircleShape)
              .testTag("charades_game_home_button")
          ) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = "الرئيسية",
              tint = NeonPurple
            )
          }
        }

        // Active Turn Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(teamColor.copy(alpha = 0.15f))
            .border(1.2.dp, teamColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = if (uiState.charadesIsTeamMode) "دور ${currentTeam.name}" else "الممثل: ${actorPlayer?.name ?: "اللاعب"}",
            color = teamColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(
          onClick = { onShowRules(true) },
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, CircleShape)
            .testTag("charades_game_rules_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = "القواعد",
            tint = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Scoreboard Bar
      if (uiState.charadesIsTeamMode) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = uiState.charadesTeamA.name,
              color = NeonAmber,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${uiState.charadesTeamA.score} نقطة",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(30.dp)
              .background(DarkSurfaceBorder)
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = uiState.charadesTeamB.name,
              color = NeonCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${uiState.charadesTeamB.score} نقطة",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Central Timer Circle & Display
      Box(
        modifier = Modifier
          .size(110.dp)
          .scale(if (isUrgent) urgentScale else 1f),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          progress = { timerProgress },
          modifier = Modifier.fillMaxSize(),
          color = when {
            isUrgent -> NeonRed
            secondsRemaining <= 20 -> NeonOrange
            else -> teamColor
          },
          strokeWidth = 7.dp,
          trackColor = DarkSurfaceElevated
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "$secondsRemaining",
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            color = if (isUrgent) NeonRed else TextPrimary
          )
          Text(
            text = "ثانية",
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Word Card Centerpiece
      NeonCard(
        borderColor = if (isUrgent) NeonRed else teamColor.copy(alpha = 0.8f),
        backgroundColor = DarkSurface,
        cornerRadius = 24.dp,
        modifier = Modifier
          .fillMaxWidth()
          .shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(24.dp),
            ambientColor = teamColor.copy(alpha = 0.3f),
            spotColor = teamColor.copy(alpha = 0.5f)
          )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Category Tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(teamColor.copy(alpha = 0.15f))
              .border(1.dp, teamColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = "${uiState.selectedCharadesCategory.icon} ${uiState.selectedCharadesCategory.name}",
              color = teamColor,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Word Display or Secret Placeholder
          if (isWordRevealed) {
            AnimatedContent(
              targetState = uiState.charadesCurrentWord,
              transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
              label = "word_transition"
            ) { targetWord ->
              Text(
                text = targetWord,
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
              )
            }
          } else {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { onToggleWordReveal() }
                .padding(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                tint = teamColor,
                modifier = Modifier.size(42.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "اضغط لكشف الكلمة للممثل 🤫",
                color = teamColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Strict Rule Banner
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(DarkSurfaceElevated)
              .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.VolumeOff,
              contentDescription = null,
              tint = NeonRed,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "🤫 لا كلام، لا أصوات! إشارات وحركات الجسد فقط!",
              color = TextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Guessed this turn badge
          if (uiState.charadesWordsGuessedThisTurn > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "🎉 تم تخمين ${uiState.charadesWordsGuessedThisTurn} كلمات في هذا الدور!",
              color = NeonGreen,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 5. Interactive Game Controls
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (!isTimerRunning && secondsRemaining == totalDuration) {
          // Pre-start Primary Action
          NeonPrimaryButton(
            text = "ابدأ وقت التمثيل ⏱️ (انطلاق!)",
            onClick = onStartTimer,
            icon = Icons.Default.PlayArrow,
            accentColor = teamColor,
            testTag = "start_charades_timer_button",
            modifier = Modifier.height(56.dp)
          )
        } else {
          // In-game Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Correct Guess Button (+1 point)
            NeonPrimaryButton(
              text = "صح! خمنوها 🎉 (+1)",
              onClick = onCorrectGuess,
              icon = Icons.Default.Check,
              accentColor = NeonGreen,
              testTag = "correct_charades_guess_button",
              modifier = Modifier
                .weight(1.3f)
                .height(56.dp)
            )

            // Skip Word Button
            NeonSecondaryButton(
              text = "تخطي ⏭️",
              onClick = onSkipWord,
              icon = Icons.Default.FastForward,
              borderColor = TextSecondary,
              testTag = "skip_charades_word_button",
              modifier = Modifier
                .weight(0.7f)
                .height(56.dp)
            )
          }

          // Stop / Finish Turn Button
          NeonSecondaryButton(
            text = "إنهاء دور التمثيل 🛑",
            onClick = onEndTurn,
            icon = Icons.Default.Close,
            borderColor = NeonRed,
            testTag = "end_charades_turn_button",
            modifier = Modifier.fillMaxWidth().height(48.dp)
          )
        }
      }
    }

    if (uiState.showRulesModal) {
      RulesModal(
        gameMode = GameMode.CHARADES,
        onDismiss = { onShowRules(false) }
      )
    }
  }
}
