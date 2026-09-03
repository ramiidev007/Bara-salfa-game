package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
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
import com.example.model.TodChoiceType
import com.example.ui.components.NeonCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonSecondaryButton
import com.example.ui.components.RulesModal
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun TruthOrDarePromptScreen(
  uiState: GameUiState,
  onRerollPrompt: () -> Unit,
  onSwitchToOppositeChoice: () -> Unit,
  onNextSpin: () -> Unit,
  onShowRules: (Boolean) -> Unit,
  onBackToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isTruth = uiState.todChoice == TodChoiceType.TRUTH
  val themeColor = if (isTruth) NeonCyan else NeonPink
  val secondaryColor = if (isTruth) NeonPurple else NeonOrange
  val victimPlayerName = uiState.selectedTodPlayer?.name ?: "اللاعب"

  val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            DarkBackground,
            if (isTruth) Color(0xFF07212E) else Color(0xFF2C0A1E),
            DarkBackground
          )
        )
      )
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag("tod_prompt_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Header
      ScreenHeader(
        title = if (isTruth) "صراحة واعتراف 💬" else "تحدي وجرأة 🔥",
        subtitle = "دور اللاعب: $victimPlayerName",
        onBackClick = onNextSpin,
        rightActionIcon = Icons.Default.HelpOutline,
        onRightActionClick = { onShowRules(true) }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Victim Player Highlight Badge
      Box(
        modifier = Modifier
          .scale(pulseScale)
          .clip(RoundedCornerShape(20.dp))
          .background(DarkSurfaceElevated)
          .border(2.dp, themeColor, RoundedCornerShape(20.dp))
          .shadow(16.dp, shape = RoundedCornerShape(20.dp), ambientColor = themeColor, spotColor = secondaryColor)
          .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = if (isTruth) "🤫 " else "⚡ ", fontSize = 22.sp)
          Text(
            text = victimPlayerName,
            color = themeColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = if (isTruth) " (عليه الصراحة)" else " (عليه الجرأة)",
            color = TextSecondary,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Big Prompt Card
      NeonCard(
        borderColor = themeColor,
        backgroundColor = DarkSurface.copy(alpha = 0.92f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Icon badge
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(themeColor.copy(alpha = 0.35f), Color.Transparent)
                )
              )
              .border(1.5.dp, themeColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(text = if (isTruth) "💬" else "🔥", fontSize = 32.sp)
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = if (isTruth) "سؤال الصراحة:" else "تحدي الجرأة المطلوبة:",
            style = MaterialTheme.typography.titleMedium,
            color = themeColor,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(12.dp))

          // The Prompt Content
          Text(
            text = uiState.currentTodPromptText,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            modifier = Modifier.testTag("tod_prompt_text")
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Reroll Prompt Button
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(DarkSurfaceBorder)
              .border(1.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            NeonSecondaryButton(
              text = "تغيير السؤال / التحدي 🎲",
              onClick = onRerollPrompt,
              icon = Icons.Default.Refresh,
              borderColor = themeColor.copy(alpha = 0.7f),
              testTag = "reroll_tod_button"
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Switch to opposite choice (e.g. from Truth to Dare)
      NeonSecondaryButton(
        text = if (isTruth) "التحويل إلى تحدي وجرأة 🔥" else "التحويل إلى سؤال صراحة 💬",
        onClick = onSwitchToOppositeChoice,
        borderColor = if (isTruth) NeonPink else NeonCyan,
        testTag = "switch_tod_type_button"
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Bottom Action Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        NeonPrimaryButton(
          text = "تم التنفيذ بنجاح! لف القارورة للجولة التالية 🍾",
          onClick = onNextSpin,
          icon = Icons.Default.CheckCircle,
          accentColor = themeColor,
          testTag = "complete_and_next_spin_button"
        )

        NeonSecondaryButton(
          text = "العودة للرئيسية 🏠",
          onClick = onBackToHome,
          borderColor = DarkSurfaceBorder,
          testTag = "tod_home_button"
        )
      }
    }

    if (uiState.showRulesModal) {
      RulesModal(
        gameMode = GameMode.TRUTH_OR_DARE,
        onDismiss = { onShowRules(false) }
      )
    }
  }
}
