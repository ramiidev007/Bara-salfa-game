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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.model.GameMode
import com.example.util.SoundEffectManager
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
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun HomeScreen(
  uiState: GameUiState,
  onSelectGameMode: (GameMode) -> Unit,
  onPlayNow: () -> Unit,
  onShowRules: (Boolean) -> Unit,
  onOpenPerksHub: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val currentMode = uiState.selectedGameMode

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.97f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(1300, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val heroAccentColor = when (currentMode) {
    GameMode.WORD_BOMB -> NeonRed
    GameMode.TRUTH_OR_DARE -> NeonPink
    GameMode.SALFA_BARRA -> NeonGreen
    GameMode.CHARADES -> NeonAmber
  }
  val heroSecondaryColor = when (currentMode) {
    GameMode.WORD_BOMB -> NeonOrange
    GameMode.TRUTH_OR_DARE -> NeonPurple
    GameMode.SALFA_BARRA -> NeonCyan
    GameMode.CHARADES -> NeonCyan
  }

  val backgroundGradients = when (currentMode) {
    GameMode.WORD_BOMB -> listOf(DarkBackground, Color(0xFF260D12), DarkBackground)
    GameMode.TRUTH_OR_DARE -> listOf(DarkBackground, Color(0xFF290E26), DarkBackground)
    GameMode.SALFA_BARRA -> listOf(DarkBackground, Color(0xFF140D24), DarkBackground)
    GameMode.CHARADES -> listOf(DarkBackground, Color(0xFF2B1C0B), DarkBackground)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Brush.verticalGradient(backgroundGradients))
      .padding(horizontal = 16.dp, vertical = 16.dp)
      .testTag("home_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Game Mode Selector Menu (4-way Grid Switcher)
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.2.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
            .padding(4.dp)
            .testTag("game_mode_selector"),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Mode 1: Salfa Barra
            GameModeTabButton(
              title = "برا السالفة 🕵️‍♂️",
              isSelected = currentMode == GameMode.SALFA_BARRA,
              selectedColor = NeonPurple,
              onClick = { onSelectGameMode(GameMode.SALFA_BARRA) },
              modifier = Modifier.weight(1f),
              testTag = "mode_salfa_barra"
            )

            // Mode 2: Word Bomb
            GameModeTabButton(
              title = "القنبلة 💣",
              isSelected = currentMode == GameMode.WORD_BOMB,
              selectedColor = NeonRed,
              onClick = { onSelectGameMode(GameMode.WORD_BOMB) },
              modifier = Modifier.weight(1f),
              testTag = "mode_word_bomb"
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Mode 3: Truth or Dare (Spinning Bottle)
            GameModeTabButton(
              title = "صراحة وجرأة 🍾",
              isSelected = currentMode == GameMode.TRUTH_OR_DARE,
              selectedColor = NeonPink,
              onClick = { onSelectGameMode(GameMode.TRUTH_OR_DARE) },
              modifier = Modifier.weight(1f),
              testTag = "mode_truth_or_dare"
            )

            // Mode 4: Charades (تمثيل بدون كلام)
            GameModeTabButton(
              title = "تمثيل بدون كلام 🎭",
              isSelected = currentMode == GameMode.CHARADES,
              selectedColor = NeonAmber,
              onClick = { onSelectGameMode(GameMode.CHARADES) },
              modifier = Modifier.weight(1f),
              testTag = "mode_charades"
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Neon Logo Centerpiece with Dynamic Transitions
        AnimatedContent(
          targetState = currentMode,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "hero_logo_transition"
        ) { mode ->
          when (mode) {
            GameMode.WORD_BOMB -> {
              Box(
                modifier = Modifier
                  .size(125.dp)
                  .scale(pulseScale)
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
                  .shadow(28.dp, shape = CircleShape, ambientColor = NeonRed, spotColor = NeonOrange),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "💣",
                  fontSize = 62.sp
                )
              }
            }
            GameMode.TRUTH_OR_DARE -> {
              Box(
                modifier = Modifier
                  .size(125.dp)
                  .scale(pulseScale)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      listOf(
                        NeonPink.copy(alpha = 0.35f),
                        NeonPurple.copy(alpha = 0.25f),
                        Color.Transparent
                      )
                    )
                  )
                  .border(2.5.dp, NeonPink, CircleShape)
                  .shadow(28.dp, shape = CircleShape, ambientColor = NeonPink, spotColor = NeonPurple),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "🍾",
                  fontSize = 62.sp
                )
              }
            }
            GameMode.SALFA_BARRA -> {
              Box(
                modifier = Modifier
                  .size(125.dp)
                  .scale(pulseScale)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      listOf(
                        NeonPurple.copy(alpha = 0.35f),
                        NeonGreen.copy(alpha = 0.15f),
                        Color.Transparent
                      )
                    )
                  )
                  .border(2.5.dp, NeonGreen, CircleShape)
                  .shadow(28.dp, shape = CircleShape, ambientColor = NeonGreen, spotColor = NeonPurple),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "🤫",
                  fontSize = 62.sp
                )
              }
            }
            GameMode.CHARADES -> {
              Box(
                modifier = Modifier
                  .size(125.dp)
                  .scale(pulseScale)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      listOf(
                        NeonAmber.copy(alpha = 0.4f),
                        NeonOrange.copy(alpha = 0.2f),
                        Color.Transparent
                      )
                    )
                  )
                  .border(2.5.dp, NeonAmber, CircleShape)
                  .shadow(28.dp, shape = CircleShape, ambientColor = NeonAmber, spotColor = NeonOrange),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "🎭",
                  fontSize = 62.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Title & Description
        AnimatedContent(
          targetState = currentMode,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "title_transition"
        ) { mode ->
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (mode) {
              GameMode.WORD_BOMB -> {
                Text(
                  text = "القنبلة الموقوتة",
                  style = MaterialTheme.typography.displayLarge,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  letterSpacing = 1.sp,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "الفتيل يشتعل بسرعة عشوائية!\nاذكر كلمة تطابق التحدي ومرر قبل الانفجار!",
                  style = MaterialTheme.typography.bodyLarge,
                  color = TextSecondary,
                  textAlign = TextAlign.Center,
                  lineHeight = 22.sp
                )
              }
              GameMode.TRUTH_OR_DARE -> {
                Text(
                  text = "صراحة أو جرأة",
                  style = MaterialTheme.typography.displayLarge,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  letterSpacing = 1.sp,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "لف القارورة العجيبة واكتشف الضحية!\nإما الاعتراف الصادق أو تنفيذ التحدي الجريء!",
                  style = MaterialTheme.typography.bodyLarge,
                  color = TextSecondary,
                  textAlign = TextAlign.Center,
                  lineHeight = 22.sp
                )
              }
              GameMode.SALFA_BARRA -> {
                Text(
                  text = "برا السالفة",
                  style = MaterialTheme.typography.displayLarge,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  letterSpacing = 1.sp,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "مين اللي داخل السالفة ومين اللي برا؟\nاكتشف المحتال بالذكاء والأسئلة!",
                  style = MaterialTheme.typography.bodyLarge,
                  color = TextSecondary,
                  textAlign = TextAlign.Center,
                  lineHeight = 22.sp
                )
              }
              GameMode.CHARADES -> {
                Text(
                  text = "تمثيل بدون كلام",
                  style = MaterialTheme.typography.displayLarge,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  letterSpacing = 1.sp,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "أفلام، أمثال، ومواقف كوميدية ويومية!\nمثّل بدون أي كلام وخمنوا قبل انتهاء الوقت!",
                  style = MaterialTheme.typography.bodyLarge,
                  color = TextSecondary,
                  textAlign = TextAlign.Center,
                  lineHeight = 22.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Feature highlights card
      NeonCard(
        borderColor = heroAccentColor.copy(alpha = 0.4f),
        backgroundColor = DarkSurface.copy(alpha = 0.85f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          when (currentMode) {
            GameMode.WORD_BOMB -> {
              FeaturePill(icon = "⚡", label = "سرعة وبديهة", color = NeonOrange)
              FeaturePill(icon = "💥", label = "انفجار مفاجئ", color = NeonRed)
              FeaturePill(icon = "🔄", label = "تمرير حماسي", color = NeonAmber)
            }
            GameMode.TRUTH_OR_DARE -> {
              FeaturePill(icon = "🍾", label = "قارورة دوارة", color = NeonPink)
              FeaturePill(icon = "💬", label = "صراحة واعتراف", color = NeonCyan)
              FeaturePill(icon = "🔥", label = "تحديات جريئة", color = NeonPurple)
            }
            GameMode.SALFA_BARRA -> {
              FeaturePill(icon = "👥", label = "3 - 10 لاعبين", color = NeonCyan)
              FeaturePill(icon = "🕵️‍♂️", label = "تحقيق وأسئلة", color = NeonPurple)
              FeaturePill(icon = "🎭", label = "تحديات وعقاب", color = NeonPink)
            }
            GameMode.CHARADES -> {
              FeaturePill(icon = "🤫", label = "بدون كلام", color = NeonAmber)
              FeaturePill(icon = "🎬", label = "أفلام وأمثال", color = NeonCyan)
              FeaturePill(icon = "🏆", label = "تحدي فرق", color = NeonGreen)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        val playButtonTitle = when (currentMode) {
          GameMode.WORD_BOMB -> "ابدأ القنبلة الموقوتة 🔥"
          GameMode.TRUTH_OR_DARE -> "ابدأ صراحة أو جرأة 🍾"
          GameMode.SALFA_BARRA -> "العب برا السالفة 🚀"
          GameMode.CHARADES -> "ابدأ تمثيل بدون كلام 🎭🎬"
        }

        NeonPrimaryButton(
          text = playButtonTitle,
          onClick = onPlayNow,
          icon = Icons.Default.PlayArrow,
          accentColor = heroAccentColor,
          testTag = "play_now_button"
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          NeonSecondaryButton(
            text = "طريقة اللعب 📜",
            onClick = { onShowRules(true) },
            icon = Icons.Default.HelpOutline,
            borderColor = heroSecondaryColor,
            modifier = Modifier.weight(1f),
            testTag = "how_to_play_button"
          )

          NeonSecondaryButton(
            text = "المزايا 👑",
            onClick = onOpenPerksHub,
            borderColor = NeonAmber,
            modifier = Modifier.weight(1f),
            testTag = "vip_perks_hub_button"
          )
        }
      }
    }

    if (uiState.showRulesModal) {
      RulesModal(
        gameMode = uiState.selectedGameMode,
        onDismiss = { onShowRules(false) }
      )
    }
  }
}

@Composable
private fun GameModeTabButton(
  title: String,
  isSelected: Boolean,
  selectedColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  val context = LocalContext.current
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (isSelected) selectedColor.copy(alpha = 0.2f) else Color.Transparent
      )
      .border(
        if (isSelected) 1.5.dp else 0.dp,
        if (isSelected) selectedColor else Color.Transparent,
        RoundedCornerShape(16.dp)
      )
      .clickable {
        SoundEffectManager.playTapSound(context)
        onClick()
      }
      .padding(vertical = 10.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      color = if (isSelected) selectedColor else TextMuted,
      fontSize = 14.sp,
      fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold
    )
  }
}

@Composable
private fun FeaturePill(
  icon: String,
  label: String,
  color: Color
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(text = icon, fontSize = 22.sp)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      color = color,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
