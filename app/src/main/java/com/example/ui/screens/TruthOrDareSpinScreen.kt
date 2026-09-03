package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMasterData
import com.example.model.GameMode
import com.example.model.Player
import com.example.model.TodChoiceType
import com.example.model.TodPack
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TruthOrDareSpinScreen(
  uiState: GameUiState,
  onSpinBottle: () -> Unit,
  onChooseTruth: () -> Unit,
  onChooseDare: () -> Unit,
  onSelectPack: (TodPack) -> Unit,
  onPromptVipUnlock: (TodPack) -> Unit,
  onShowRules: (Boolean) -> Unit,
  onBackToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isSpinning = uiState.isBottleSpinning
  val selectedPlayer = uiState.selectedTodPlayer

  // Smooth realistic deceleration bottle spin physics
  val animatedRotation by animateFloatAsState(
    targetValue = uiState.bottleRotationDegrees,
    animationSpec = tween(
      durationMillis = 3500,
      easing = CubicBezierEasing(0.12f, 0.8f, 0.2f, 1.0f)
    ),
    label = "bottle_rotation_anim"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "bottle_glow")
  val pulseGlow by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.06f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_bottle"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            DarkBackground,
            Color(0xFF2B0A2E),
            DarkBackground
          )
        )
      )
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag("tod_spin_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Bar
      ScreenHeader(
        title = "صراحة أو جرأة 🍾",
        subtitle = "لف القارورة العجيبة واكتشف الضحية!",
        onBackClick = onBackToHome,
        rightActionIcon = Icons.Default.HelpOutline,
        onRightActionClick = { onShowRules(true) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Pack Selector Pills
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "باقة الأسئلة والتحديات:",
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          GameMasterData.todPacks.forEach { pack ->
            val isSelected = uiState.selectedTodPack.id == pack.id
            val isUnlocked = !pack.isVip || uiState.isAllVipUnlocked || uiState.unlockedPackIds.contains(pack.id)
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(
                  if (isSelected) NeonPink.copy(alpha = 0.22f)
                  else if (pack.isVip && !isUnlocked) NeonAmber.copy(alpha = 0.12f)
                  else DarkSurface
                )
                .border(
                  1.2.dp,
                  if (isSelected) NeonPink
                  else if (pack.isVip && !isUnlocked) NeonAmber.copy(alpha = 0.6f)
                  else DarkSurfaceBorder,
                  RoundedCornerShape(14.dp)
                )
                .clickable {
                  if (isUnlocked) {
                    onSelectPack(pack)
                  } else {
                    onPromptVipUnlock(pack)
                  }
                }
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .testTag("tod_pack_${pack.id}"),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = pack.icon, fontSize = 16.sp)
                  if (pack.isVip) {
                    Text(
                      text = if (isUnlocked) "👑" else "🔒",
                      fontSize = 10.sp,
                      modifier = Modifier.padding(start = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = pack.title.split(" ").firstOrNull() ?: pack.title,
                  color = if (isSelected) NeonPink else if (pack.isVip && !isUnlocked) NeonAmber else TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected || pack.isVip) FontWeight.Bold else FontWeight.Normal,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Spinning Bottle Stage with Orbiting Player Badges
      Box(
        modifier = Modifier
          .size(310.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(
                NeonPink.copy(alpha = 0.16f),
                NeonPurple.copy(alpha = 0.08f),
                Color.Transparent
              )
            )
          )
          .border(1.5.dp, NeonPink.copy(alpha = 0.35f), CircleShape)
          .testTag("bottle_stage"),
        contentAlignment = Alignment.Center
      ) {
        // Player circular orbital positions around the bottle
        val totalPlayers = uiState.players.size.coerceAtLeast(1)
        val orbitRadiusDp = 112.dp

        uiState.players.forEachIndexed { index, player ->
          // Calculate angle for player in radians (0 is top / 12 o'clock)
          val angleDeg = (index.toFloat() / totalPlayers) * 360f - 90f
          val angleRad = Math.toRadians(angleDeg.toDouble())
          val isThisPlayerSelected = selectedPlayer?.id == player.id

          val offsetX = (orbitRadiusDp.value * cos(angleRad)).dp
          val offsetY = (orbitRadiusDp.value * sin(angleRad)).dp

          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .offset(x = offsetX, y = offsetY)
              .clip(RoundedCornerShape(12.dp))
              .background(
                if (isThisPlayerSelected) NeonPink.copy(alpha = 0.35f) else DarkSurface.copy(alpha = 0.85f)
              )
              .border(
                if (isThisPlayerSelected) 2.dp else 1.dp,
                if (isThisPlayerSelected) NeonPink else DarkSurfaceBorder,
                RoundedCornerShape(12.dp)
              )
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("orbit_player_${player.id}"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isThisPlayerSelected) {
                Text(text = "🎯 ", fontSize = 12.sp)
              }
              Text(
                text = player.name,
                color = if (isThisPlayerSelected) NeonPink else TextPrimary,
                fontSize = 12.sp,
                fontWeight = if (isThisPlayerSelected) FontWeight.Black else FontWeight.Bold
              )
            }
          }
        }

        // Center spinning bottle asset
        Box(
          modifier = Modifier
            .size(190.dp)
            .scale(if (isSpinning) pulseGlow else 1f)
            .rotate(animatedRotation)
            .clickable(enabled = !isSpinning) { onSpinBottle() }
            .testTag("spinning_bottle_asset"),
          contentAlignment = Alignment.Center
        ) {
          StylizedNeonBottleCanvas()
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Status or Decision Banner
      if (selectedPlayer != null && !isSpinning) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          NeonCard(
            borderColor = NeonPink,
            backgroundColor = DarkSurfaceElevated,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "وقع الاختيار على الضحية 🎯",
                style = MaterialTheme.typography.titleMedium,
                color = NeonCyan,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = selectedPlayer.name,
                style = MaterialTheme.typography.headlineMedium,
                color = NeonPink,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "ماذا تختار يا ${selectedPlayer.name}؟ 🤔",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Truth or Dare Choice Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Truth Button
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF0D3B4C), Color(0xFF06222C))))
                .border(2.dp, NeonCyan, RoundedCornerShape(16.dp))
                .clickable { onChooseTruth() }
                .padding(vertical = 16.dp)
                .testTag("choose_truth_button"),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "💬", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "صراحة",
                  color = NeonCyan,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "سؤال صادق ومحرج",
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }
            }

            // Dare Button
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF4C0D26), Color(0xFF2C0616))))
                .border(2.dp, NeonPink, RoundedCornerShape(16.dp))
                .clickable { onChooseDare() }
                .padding(vertical = 16.dp)
                .testTag("choose_dare_button"),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🔥", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "جرأة وتحدي",
                  color = NeonPink,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "تحدي قوي ومضحك",
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      } else {
        // Spin action trigger button
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          NeonPrimaryButton(
            text = if (isSpinning) "القارورة تدور الآن... 🍾" else "لف القارورة الآن! 🌀",
            onClick = onSpinBottle,
            enabled = !isSpinning,
            icon = Icons.Default.Autorenew,
            accentColor = NeonPink,
            testTag = "spin_bottle_button"
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "اضغط على الزر أو على القارورة لتبدأ الدوران 👆",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center
          )
        }
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

/**
 * Custom stylized neon party bottle illustration using pure Canvas vector graphics
 */
@Composable
private fun StylizedNeonBottleCanvas(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(160.dp, 160.dp)) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Bottle geometry dimensions (pointing UP towards North / top)
    val bottleWidth = 36.dp.toPx()
    val bottleHeight = 130.dp.toPx()
    val neckWidth = 14.dp.toPx()
    val neckHeight = 35.dp.toPx()
    val capHeight = 12.dp.toPx()

    val topY = centerY - bottleHeight / 2
    val bottomY = centerY + bottleHeight / 2
    val neckStartY = topY + capHeight + neckHeight

    val bottlePath = Path().apply {
      // Bottle cap top
      moveTo(centerX - neckWidth / 2, topY + capHeight)
      lineTo(centerX - neckWidth / 2, topY)
      lineTo(centerX + neckWidth / 2, topY)
      lineTo(centerX + neckWidth / 2, topY + capHeight)

      // Neck
      lineTo(centerX + neckWidth / 2, neckStartY)

      // Shoulder right curve
      cubicTo(
        centerX + neckWidth / 2 + 6.dp.toPx(), neckStartY + 10.dp.toPx(),
        centerX + bottleWidth / 2, neckStartY + 15.dp.toPx(),
        centerX + bottleWidth / 2, neckStartY + 25.dp.toPx()
      )

      // Body right side
      lineTo(centerX + bottleWidth / 2, bottomY - 10.dp.toPx())

      // Bottom right round corner
      quadraticBezierTo(
        centerX + bottleWidth / 2, bottomY,
        centerX + bottleWidth / 2 - 10.dp.toPx(), bottomY
      )

      // Bottom base
      lineTo(centerX - bottleWidth / 2 + 10.dp.toPx(), bottomY)

      // Bottom left round corner
      quadraticBezierTo(
        centerX - bottleWidth / 2, bottomY,
        centerX - bottleWidth / 2, bottomY - 10.dp.toPx()
      )

      // Body left side
      lineTo(centerX - bottleWidth / 2, neckStartY + 25.dp.toPx())

      // Shoulder left curve
      cubicTo(
        centerX - bottleWidth / 2, neckStartY + 15.dp.toPx(),
        centerX - neckWidth / 2 - 6.dp.toPx(), neckStartY + 10.dp.toPx(),
        centerX - neckWidth / 2, neckStartY
      )

      close()
    }

    // Bottle Glass Body gradient fill
    drawPath(
      path = bottlePath,
      brush = Brush.linearGradient(
        colors = listOf(
          NeonPink.copy(alpha = 0.85f),
          NeonPurple.copy(alpha = 0.95f),
          Color(0xFF1E0B30)
        ),
        start = Offset(centerX - bottleWidth / 2, topY),
        end = Offset(centerX + bottleWidth / 2, bottomY)
      )
    )

    // Bottle Glowing Neon Border
    drawPath(
      path = bottlePath,
      color = NeonPink,
      style = Stroke(width = 3.dp.toPx())
    )

    // Glowing Cap
    drawRect(
      color = NeonAmber,
      topLeft = Offset(centerX - neckWidth / 2 - 2.dp.toPx(), topY),
      size = androidx.compose.ui.geometry.Size(neckWidth + 4.dp.toPx(), capHeight)
    )

    // Bottle Label in Center
    val labelHeight = 35.dp.toPx()
    val labelWidth = bottleWidth - 8.dp.toPx()
    val labelTop = centerY - 5.dp.toPx()

    drawRoundRect(
      color = Color(0xFF0F071B),
      topLeft = Offset(centerX - labelWidth / 2, labelTop),
      size = androidx.compose.ui.geometry.Size(labelWidth, labelHeight),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
    )

    drawRoundRect(
      color = NeonCyan,
      topLeft = Offset(centerX - labelWidth / 2, labelTop),
      size = androidx.compose.ui.geometry.Size(labelWidth, labelHeight),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx()),
      style = Stroke(width = 1.5.dp.toPx())
    )

    // Pointer indicator tip at top of bottle
    drawCircle(
      color = NeonCyan,
      radius = 4.dp.toPx(),
      center = Offset(centerX, topY - 6.dp.toPx())
    )
  }
}
