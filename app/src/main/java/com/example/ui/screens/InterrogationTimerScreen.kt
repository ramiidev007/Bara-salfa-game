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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
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
import com.example.model.GameMasterData
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
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun InterrogationTimerScreen(
  uiState: GameUiState,
  onNextQuestionTurn: () -> Unit,
  onRandomizeQuestionPair: () -> Unit,
  onNextHint: () -> Unit,
  onStartVoting: () -> Unit,
  modifier: Modifier = Modifier
) {
  val askerPlayer = uiState.players.getOrNull(uiState.currentAskerIndex)
    ?: uiState.players.firstOrNull()
  val targetPlayer = uiState.players.getOrNull(uiState.currentTargetIndex)
    ?: uiState.players.getOrNull(1) ?: uiState.players.firstOrNull()

  // Multi-layer infinite animations for cool emojis
  val infiniteTransition = rememberInfiniteTransition(label = "investigation_animations")

  val detectivePulse by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "detective_pulse"
  )

  val floatOffset by infiniteTransition.animateFloat(
    initialValue = -8f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "float_offset"
  )

  val glassRotation by infiniteTransition.animateFloat(
    initialValue = -15f,
    targetValue = 25f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glass_rotation"
  )

  val eyesScale by infiniteTransition.animateFloat(
    initialValue = 0.9f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "eyes_scale"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
      .verticalScroll(rememberScrollState())
      .testTag("interrogation_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Header
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceElevated)
          .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "🔥 مرحلة التحقيق والأسئلة 🔥",
          color = NeonCyan,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "مين اللي برا السالفة؟",
        style = MaterialTheme.typography.displayMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Interactive Turn-by-Turn Asking Card
    NeonCard(
      borderColor = NeonCyan.copy(alpha = 0.7f),
      backgroundColor = DarkSurfaceElevated,
      cornerRadius = 20.dp,
      modifier = Modifier
        .fillMaxWidth()
        .shadow(16.dp, shape = RoundedCornerShape(20.dp), ambientColor = NeonCyan)
        .testTag("question_pair_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Question number indicator
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "السؤال رقم ${uiState.questionTurnCount} 💬",
            color = NeonPurple,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )

          IconButton(
            onClick = onRandomizeQuestionPair,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(DarkSurface)
              .border(1.dp, NeonPurple.copy(alpha = 0.4f), CircleShape)
              .testTag("randomize_pair_button")
          ) {
            Icon(
              imageVector = Icons.Default.Casino,
              contentDescription = "اختيار عشوائي",
              tint = NeonPurple,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Animated Asker -> Target presentation
        AnimatedContent(
          targetState = Pair(askerPlayer?.name ?: "", targetPlayer?.name ?: ""),
          transitionSpec = {
            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
              slideOutHorizontally { width -> -width } + fadeOut()
            )
          },
          label = "asking_pair_transition"
        ) { (asker, target) ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Asker Player Box
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "السائل 🗣️",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(NeonCyan.copy(alpha = 0.15f))
                  .border(1.5.dp, NeonCyan, RoundedCornerShape(12.dp))
                  .padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Text(
                  text = asker,
                  color = NeonCyan,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  textAlign = TextAlign.Center
                )
              }
            }

            // Arrow
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(horizontal = 4.dp)
            ) {
              Text(
                text = "يسأل",
                color = NeonAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "👉",
                fontSize = 20.sp
              )
            }

            // Target Player Box
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "المسؤول 🎯",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(NeonPink.copy(alpha = 0.15f))
                  .border(1.5.dp, NeonPink, RoundedCornerShape(12.dp))
                  .padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Text(
                  text = target,
                  color = NeonPink,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Next Question Turn Button
        NeonSecondaryButton(
          text = "السؤال التالي ⏭️",
          onClick = onNextQuestionTurn,
          icon = Icons.Default.SkipNext,
          borderColor = NeonCyan,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Animated Cool Emoji Hub
    Box(
      modifier = Modifier
        .size(190.dp)
        .testTag("animated_emoji_hub"),
      contentAlignment = Alignment.Center
    ) {
      // Outer Glowing Ring 1
      Box(
        modifier = Modifier
          .size(180.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(NeonPurple.copy(alpha = 0.2f), Color.Transparent)
            )
          )
          .border(1.5.dp, NeonPurple.copy(alpha = 0.35f), CircleShape)
      )

      // Outer Glowing Ring 2
      Box(
        modifier = Modifier
          .size(140.dp)
          .scale(detectivePulse)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(NeonCyan.copy(alpha = 0.25f), DarkSurface)
            )
          )
          .border(2.dp, NeonCyan.copy(alpha = 0.6f), CircleShape)
          .shadow(20.dp, shape = CircleShape, ambientColor = NeonCyan)
      )

      // Center Detective Emoji
      Text(
        text = "🕵️‍♂️",
        fontSize = 58.sp,
        modifier = Modifier
          .scale(detectivePulse)
          .offset(y = floatOffset.dp)
      )

      // Orbiting Animated Emoji: Magnifying Glass 🔍
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .offset(x = (-4).dp, y = 4.dp)
          .rotate(glassRotation)
      ) {
        Text(text = "🔍", fontSize = 28.sp)
      }

      // Orbiting Animated Emoji: Eyes 👁️
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .offset(x = 8.dp, y = 10.dp)
          .scale(eyesScale)
      ) {
        Text(text = "👁️", fontSize = 24.sp)
      }

      // Orbiting Animated Emoji: Thinking Face 🤔
      Box(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .offset(x = 8.dp, y = (-8).dp)
          .offset(y = (-floatOffset).dp)
      ) {
        Text(text = "🤔", fontSize = 26.sp)
      }

      // Orbiting Animated Emoji: Secret Whisper 🤫
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .offset(x = (-8).dp, y = (-6).dp)
          .scale(detectivePulse)
      ) {
        Text(text = "🤫", fontSize = 26.sp)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Smart Question Prompts Card
    val hints = GameMasterData.smartQuestionHints
    val currentHint = hints.getOrElse(uiState.currentHintIndex) { hints.first() }

    NeonCard(
      borderColor = NeonAmber.copy(alpha = 0.4f),
      backgroundColor = DarkSurface,
      cornerRadius = 16.dp,
      onClick = onNextHint,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("question_hint_card")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(NeonAmber.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = NeonAmber,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "فكرة سؤال ذكي (اضغط للتغيير):",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = currentHint,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "سؤال آخر",
          tint = NeonAmber.copy(alpha = 0.8f),
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Start Voting Button
    NeonPrimaryButton(
      text = "جاهزون؟ بدء تصويت اللاعبين 🗳️",
      onClick = onStartVoting,
      icon = Icons.Default.HowToVote,
      accentColor = NeonPurple,
      testTag = "start_voting_button"
    )
  }
}
