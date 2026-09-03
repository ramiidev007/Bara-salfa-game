package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.ui.components.NeonCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun VotingScreen(
  uiState: GameUiState,
  onVotePlayer: (Player) -> Unit,
  modifier: Modifier = Modifier
) {
  val totalVoters = uiState.players.size
  val currentVoter = uiState.players.getOrNull(uiState.currentVoterIndex) ?: uiState.players.first()
  val progress = (uiState.currentVoterIndex + 1).toFloat() / totalVoters.coerceAtLeast(1)

  // Subtle pulsing animation on voting icon
  val infiniteTransition = rememberInfiniteTransition(label = "voting_icon_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 24.dp, vertical = 20.dp)
      .testTag("voting_screen"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header & Voter Progress
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "تصويت اللاعبين 🗳️",
          style = MaterialTheme.typography.titleMedium,
          color = TextSecondary,
          fontWeight = FontWeight.SemiBold
        )

        Text(
          text = "المصوت ${uiState.currentVoterIndex + 1} من $totalVoters",
          color = NeonPurple,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = NeonPurple,
        trackColor = DarkSurfaceElevated,
        strokeCap = StrokeCap.Round
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Current Voter Callout Card
      AnimatedContent(
        targetState = currentVoter,
        transitionSpec = {
          (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
            slideOutHorizontally { width -> -width } + fadeOut()
          )
        },
        label = "current_voter_card"
      ) { voter ->
        NeonCard(
          borderColor = NeonCyan.copy(alpha = 0.6f),
          backgroundColor = DarkSurfaceElevated,
          cornerRadius = 18.dp,
          modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, shape = RoundedCornerShape(18.dp), ambientColor = NeonCyan)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "دور اللاعب في التصويت:",
              color = TextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = voter.name,
              style = MaterialTheme.typography.displayMedium,
              color = NeonCyan,
              fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "اختر من تشك أنه برا السالفة 👇",
              color = TextMuted,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Suspect Candidates Grid
    Text(
      text = "قائمة المشبوهين",
      color = TextSecondary,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 4.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Filter candidates (all other players, or all players including self)
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(bottom = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.weight(1f)
    ) {
      items(uiState.players, key = { it.id }) { candidate ->
        val isSelf = candidate.id == currentVoter.id
        VotingCandidateCard(
          candidate = candidate,
          isSelf = isSelf,
          onVote = { onVotePlayer(candidate) }
        )
      }
    }
  }
}

@Composable
private fun VotingCandidateCard(
  candidate: Player,
  isSelf: Boolean,
  onVote: () -> Unit
) {
  NeonCard(
    borderColor = if (isSelf) DarkSurfaceBorder else NeonPurple.copy(alpha = 0.5f),
    backgroundColor = DarkSurface,
    cornerRadius = 18.dp,
    onClick = onVote,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("vote_candidate_card_${candidate.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(
            if (isSelf) DarkSurfaceElevated else NeonPurple.copy(alpha = 0.2f)
          )
          .border(
            1.5.dp,
            if (isSelf) DarkSurfaceBorder else NeonPurple.copy(alpha = 0.6f),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (isSelf) "👤" else "🕵️",
          fontSize = 24.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = candidate.name,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = if (isSelf) "(أنت)" else "تصويت 👈",
        color = if (isSelf) TextMuted else NeonPink,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}
