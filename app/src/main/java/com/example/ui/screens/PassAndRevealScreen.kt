package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
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
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PassAndRevealScreen(
  uiState: GameUiState,
  onHoldingChange: (Boolean) -> Unit,
  onNextPlayer: () -> Unit,
  onSkipCountdown: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedContent(
    targetState = uiState.isHandoffCountdownActive,
    transitionSpec = {
      fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
    },
    label = "pass_and_reveal_mode"
  ) { isHandoffActive ->
    if (isHandoffActive) {
      // 5-Second Reverse Countdown Screen between players
      HandoffCountdownView(
        uiState = uiState,
        onSkipCountdown = onSkipCountdown,
        modifier = modifier
      )
    } else {
      // Active Player Swipe-Up to Reveal Screen
      SwipeToRevealView(
        uiState = uiState,
        onHoldingChange = onHoldingChange,
        onNextPlayer = onNextPlayer,
        modifier = modifier
      )
    }
  }
}

/**
 * 5-Second Reverse Counter view played during passing phone to next player
 */
@Composable
private fun HandoffCountdownView(
  uiState: GameUiState,
  onSkipCountdown: () -> Unit,
  modifier: Modifier = Modifier
) {
  val nextPlayerIndex = uiState.currentRevealIndex + 1
  val nextPlayer = uiState.players.getOrNull(nextPlayerIndex)
    ?: uiState.players.getOrNull(uiState.currentRevealIndex)

  val infiniteTransition = rememberInfiniteTransition(label = "countdown_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val progressFraction = uiState.handoffCountdownSeconds / 5f

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 24.dp, vertical = 24.dp)
      .testTag("handoff_countdown_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Title & Notice
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceElevated)
          .border(1.dp, NeonAmber.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.PhoneAndroid,
            contentDescription = null,
            tint = NeonAmber,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "تمرير الجوال بسرية 🔒",
            color = NeonAmber,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "مرر الجوال إلى",
        style = MaterialTheme.typography.titleMedium,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = nextPlayer?.name ?: "",
        style = MaterialTheme.typography.displayMedium,
        color = NeonCyan,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
      )
    }

    // Center Big Reverse Countdown
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .size(200.dp)
          .scale(pulseScale),
        contentAlignment = Alignment.Center
      ) {
        // Circular Progress Ring
        CircularProgressIndicator(
          progress = { progressFraction },
          modifier = Modifier.fillMaxSize(),
          color = NeonPink,
          strokeWidth = 8.dp,
          trackColor = DarkSurfaceElevated,
          strokeCap = StrokeCap.Round
        )

        // Inner glowing circle
        Box(
          modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(NeonPink.copy(alpha = 0.25f), DarkSurface)
              )
            )
            .border(2.dp, NeonPink.copy(alpha = 0.6f), CircleShape)
            .shadow(20.dp, shape = CircleShape, ambientColor = NeonPink),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "${uiState.handoffCountdownSeconds}",
              fontSize = 64.sp,
              color = TextPrimary,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "ثوانٍ متبقية",
              color = TextMuted,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "لا تدع أحداً ينظر للشاشة حتى يستلم ${nextPlayer?.name} الجوال!",
        color = TextSecondary,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }

    // Bottom Skip Countdown Button
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      NeonSecondaryButton(
        text = "استلم الجوال؟ ابدأ فوراً ⚡",
        onClick = onSkipCountdown,
        icon = Icons.Default.FastForward,
        borderColor = NeonCyan.copy(alpha = 0.6f),
        testTag = "skip_countdown_button"
      )
    }
  }
}

/**
 * Active player screen with interactive Swipe-Up gesture to reveal secret word
 */
@Composable
private fun SwipeToRevealView(
  uiState: GameUiState,
  onHoldingChange: (Boolean) -> Unit,
  onNextPlayer: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentPlayer = uiState.players.getOrNull(uiState.currentRevealIndex)
  val isImpostor = currentPlayer?.role == "impostor"
  val totalPlayers = uiState.players.size
  val progress = (uiState.currentRevealIndex + 1).toFloat() / totalPlayers.coerceAtLeast(1)

  // Swipe animation state
  var isSwipedUp by remember(uiState.currentRevealIndex) { mutableStateOf(false) }
  val offsetY = remember { Animatable(0f) }
  val coroutineScope = rememberCoroutineScope()

  // Bouncing chevron animation for the swipe up prompt
  val infiniteTransition = rememberInfiniteTransition(label = "swipe_bounce")
  val bounceOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = -12f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bounce"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 24.dp, vertical = 20.dp)
      .testTag("pass_and_reveal_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Progress & Header
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
          text = "كشف الكلمة السرية",
          style = MaterialTheme.typography.titleMedium,
          color = TextSecondary,
          fontWeight = FontWeight.SemiBold
        )

        Text(
          text = "اللاعب ${uiState.currentRevealIndex + 1} من $totalPlayers",
          color = NeonGreen,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = NeonGreen,
        trackColor = DarkSurfaceElevated,
        strokeCap = StrokeCap.Round
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Warning Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(DarkSurface)
          .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = NeonPink,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "تأكد ألا ينظر أحد إلى شاشتك!",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Player greeting & swipe up area
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "دور اللاعب",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = currentPlayer?.name ?: "",
        style = MaterialTheme.typography.displayMedium,
        color = NeonGreen,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Main Interactive Swipe Card Stage
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(270.dp),
        contentAlignment = Alignment.Center
      ) {
        if (!isSwipedUp && !uiState.isHolding) {
          // Closed state: Swipe up prompt card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(240.dp)
              .clip(RoundedCornerShape(24.dp))
              .background(DarkSurface)
              .border(2.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
              .shadow(16.dp, shape = RoundedCornerShape(24.dp), ambientColor = NeonGreen)
              .pointerInput(Unit) {
                detectDragGestures(
                  onDragEnd = {
                    coroutineScope.launch {
                      if (offsetY.value < -80f) {
                        isSwipedUp = true
                        onHoldingChange(true)
                      }
                      offsetY.animateTo(0f, spring())
                    }
                  },
                  onDragCancel = {
                    coroutineScope.launch { offsetY.animateTo(0f, spring()) }
                  },
                  onDrag = { change, dragAmount ->
                    change.consume()
                    val newOffset = (offsetY.value + dragAmount.y).coerceAtMost(0f)
                    coroutineScope.launch {
                      offsetY.snapTo(newOffset)
                      if (newOffset < -120f) {
                        isSwipedUp = true
                        onHoldingChange(true)
                      }
                    }
                  }
                )
              }
              .pointerInput(Unit) {
                detectTapGestures(
                  onTap = {
                    isSwipedUp = true
                    onHoldingChange(true)
                  }
                )
              }
              .offset { IntOffset(0, offsetY.value.roundToInt()) }
              .testTag("swipe_up_card"),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(20.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .offset(y = bounceOffset.dp)
                  .clip(CircleShape)
                  .background(NeonGreen.copy(alpha = 0.2f))
                  .border(2.dp, NeonGreen, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ArrowUpward,
                  contentDescription = "اسحب للأعلى",
                  tint = NeonGreen,
                  modifier = Modifier.size(28.dp)
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                text = "اسحب للأعلى لرؤية الكلمة",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "اسحب البطاقة أو اضغط عليها لكشف السر",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
              )
            }
          }
        } else {
          // Revealed State: Secret is visible!
          NeonCard(
            borderColor = if (isImpostor) NeonRed else NeonGreen,
            backgroundColor = DarkSurfaceElevated,
            cornerRadius = 24.dp,
            modifier = Modifier
              .fillMaxWidth()
              .height(260.dp)
              .shadow(24.dp, shape = RoundedCornerShape(24.dp), ambientColor = if (isImpostor) NeonRed else NeonGreen)
              .testTag("secret_revealed_card")
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              if (isImpostor) {
                Text(
                  text = "🤫",
                  fontSize = 46.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "أنت برا السالفة!",
                  style = MaterialTheme.typography.headlineMedium,
                  color = NeonRed,
                  fontWeight = FontWeight.Black,
                  textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "لا تدع أحداً يشك فيك!\nاسمع الأسئلة جيداً وحاول تخمين الكلمة.",
                  color = TextSecondary,
                  fontSize = 14.sp,
                  textAlign = TextAlign.Center,
                  lineHeight = 20.sp
                )
              } else {
                Text(
                  text = GameMasterData.getEmojiForWord(uiState.secretWord, uiState.selectedCategory?.icon ?: "🔑"),
                  fontSize = 44.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "التصنيف: ${uiState.selectedCategory?.name ?: ""}",
                  color = NeonPurple,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = uiState.secretWord,
                  style = MaterialTheme.typography.headlineLarge,
                  color = NeonGreen,
                  fontWeight = FontWeight.Black,
                  textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "أنت داخل السالفة!\nاسأل بذكاء لكشف المحتال دون كشف كلمتك.",
                  color = TextSecondary,
                  fontSize = 13.sp,
                  textAlign = TextAlign.Center,
                  lineHeight = 18.sp
                )
              }
            }
          }
        }
      }
    }

    // Bottom Navigation Actions
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val isLastPlayer = uiState.currentRevealIndex >= totalPlayers - 1

      if (isSwipedUp || uiState.hasRevealedCurrent) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // If still visible, allow hiding the card
          if (isSwipedUp) {
            NeonSecondaryButton(
              text = "إخفاء الكلمة 🔒",
              onClick = {
                isSwipedUp = false
                onHoldingChange(false)
              },
              icon = Icons.Default.VisibilityOff,
              borderColor = DarkSurfaceBorder,
              testTag = "hide_secret_button"
            )
          }

          NeonPrimaryButton(
            text = if (isLastPlayer) "الجميع شاهد الكلمة! بدء التحقيق ⏱️" else "تمت المشاهدة! التالي 👈",
            onClick = {
              isSwipedUp = false
              onNextPlayer()
            },
            accentColor = if (isLastPlayer) NeonGreen else NeonPurple,
            testTag = "next_reveal_button"
          )
        }
      } else {
        Text(
          text = "اسحب البطاقة للأعلى أو اضغط عليها لكشف الكلمة",
          color = TextMuted,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
