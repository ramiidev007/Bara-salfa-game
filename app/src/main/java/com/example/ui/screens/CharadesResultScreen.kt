package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun CharadesResultScreen(
  uiState: GameUiState,
  onNextRound: () -> Unit,
  onChangeCategory: () -> Unit,
  onResetScores: () -> Unit,
  onGoHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val teamA = uiState.charadesTeamA
  val teamB = uiState.charadesTeamB
  val isTeamMode = uiState.charadesIsTeamMode

  val leaderName = when {
    teamA.score > teamB.score -> "${teamA.name} متقدم! 👑"
    teamB.score > teamA.score -> "${teamB.name} متقدم! 👑"
    else -> "تعادل حماسي! 🤝"
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
      .testTag("charades_result_screen")
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Header Banner
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(NeonAmber.copy(alpha = 0.2f))
            .border(2.dp, NeonAmber, CircleShape)
            .shadow(16.dp, CircleShape, ambientColor = NeonAmber, spotColor = NeonAmber),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "🎭", fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "انتهت جولة التمثيل!",
          style = MaterialTheme.typography.headlineMedium,
          color = TextPrimary,
          fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (isTeamMode) leaderName else "الجولة رقم ${uiState.charadesRoundsPlayed}",
          style = MaterialTheme.typography.titleMedium,
          color = NeonAmber,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Scoreboard & Guessed Words Card
      NeonCard(
        borderColor = NeonAmber.copy(alpha = 0.6f),
        backgroundColor = DarkSurface,
        cornerRadius = 20.dp,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (isTeamMode) {
            // Team Scores Display
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurfaceElevated)
                .padding(vertical = 12.dp, horizontal = 16.dp),
              horizontalArrangement = Arrangement.SpaceAround,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Team A
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = teamA.name,
                  color = NeonAmber,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${teamA.score}",
                  color = TextPrimary,
                  fontSize = 28.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "نقاط",
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }

              Text(
                text = "VS",
                color = TextDisabledColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
              )

              // Team B
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = teamB.name,
                  color = NeonCyan,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${teamB.score}",
                  color = TextPrimary,
                  fontSize = 28.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "نقاط",
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
          }

          // Words Guessed in This Round
          Text(
            text = "الكلمات المحزورة في هذا الدور (${uiState.charadesGuessedWordsList.size}):",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          if (uiState.charadesGuessedWordsList.isEmpty()) {
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "لم يتم تخمين أي كلمة في هذا الدور 😅\nحظاً أوفر في الجولة القادمة!",
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
              )
            }
          } else {
            LazyColumn(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              items(uiState.charadesGuessedWordsList) { word ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = NeonGreen,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = word,
                      color = TextPrimary,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Medium
                    )
                  }
                  Text(
                    text = "+1 نقطة",
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Bottom Action Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Next Round Primary Button
        NeonPrimaryButton(
          text = "الجولة القادمة 🎭🎬",
          onClick = onNextRound,
          icon = Icons.Default.PlayArrow,
          accentColor = NeonAmber,
          testTag = "charades_next_round_button",
          modifier = Modifier.height(54.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Change Category Button
          NeonSecondaryButton(
            text = "تغيير التصنيف 🎬",
            onClick = onChangeCategory,
            icon = Icons.Default.Category,
            borderColor = NeonCyan,
            testTag = "charades_change_category_button",
            modifier = Modifier.weight(1f).height(48.dp)
          )

          // Home Menu Button
          NeonSecondaryButton(
            text = "الرئيسية 🏠",
            onClick = onGoHome,
            icon = Icons.Default.Home,
            borderColor = NeonPurple,
            testTag = "charades_go_home_button",
            modifier = Modifier.weight(1f).height(48.dp)
          )
        }
      }
    }
  }
}

private val TextDisabledColor = Color(0xFF555566)
