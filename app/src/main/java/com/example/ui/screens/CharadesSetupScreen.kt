package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharadesCategory
import com.example.model.GameMasterData
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
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun CharadesSetupScreen(
  uiState: GameUiState,
  onSelectCategory: (CharadesCategory) -> Unit,
  onPromptVipUnlock: (CharadesCategory) -> Unit,
  onSetDuration: (Int) -> Unit,
  onSetTeamMode: (Boolean) -> Unit,
  onSetTeamNames: (String, String) -> Unit,
  onStartGame: () -> Unit,
  onBack: () -> Unit,
  onGoHome: () -> Unit,
  onShowRules: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf(uiState.selectedCharadesCategory) }
  var duration by remember { mutableIntStateOf(uiState.charadesDurationSeconds) }
  var isTeamMode by remember { mutableStateOf(uiState.charadesIsTeamMode) }
  var teamAName by remember { mutableStateOf(uiState.charadesTeamA.name) }
  var teamBName by remember { mutableStateOf(uiState.charadesTeamB.name) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 14.dp)
        .testTag("charades_setup_screen"),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Navigation Bar
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
            onClick = onBack,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(DarkSurfaceElevated)
              .border(1.dp, DarkSurfaceBorder, CircleShape)
              .testTag("charades_back_button")
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
              .testTag("charades_home_button")
          ) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = "الرئيسية",
              tint = NeonPurple
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(NeonAmber.copy(alpha = 0.15f))
            .border(1.2.dp, NeonAmber.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "🎭 تمثيل بدون كلام",
            color = NeonAmber,
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
            .testTag("charades_rules_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = "القواعد",
            tint = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Main Settings & Category Selection Area
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
      ) {
        // Section A: Game Style (Teams vs Individual)
        item {
          NeonCard(
            borderColor = NeonCyan.copy(alpha = 0.5f),
            backgroundColor = DarkSurface,
            cornerRadius = 16.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "⚔️ نمط التحدي",
                color = NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Team Mode Chip
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isTeamMode) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceElevated)
                    .border(
                      1.2.dp,
                      if (isTeamMode) NeonCyan else DarkSurfaceBorder,
                      RoundedCornerShape(12.dp)
                    )
                    .clickable {
                      isTeamMode = true
                      onSetTeamMode(true)
                    }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "🏆 تحدي الفرق (أ ضد ب)",
                    color = if (isTeamMode) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isTeamMode) FontWeight.Bold else FontWeight.Normal
                  )
                }

                // Individual Mode Chip
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isTeamMode) NeonAmber.copy(alpha = 0.2f) else DarkSurfaceElevated)
                    .border(
                      1.2.dp,
                      if (!isTeamMode) NeonAmber else DarkSurfaceBorder,
                      RoundedCornerShape(12.dp)
                    )
                    .clickable {
                      isTeamMode = false
                      onSetTeamMode(false)
                    }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "👤 فردي (الدور بالدور)",
                    color = if (!isTeamMode) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (!isTeamMode) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }
          }
        }

        // Section B: Round Duration Picker
        item {
          NeonCard(
            borderColor = NeonAmber.copy(alpha = 0.5f),
            backgroundColor = DarkSurface,
            cornerRadius = 16.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "⏱️ مدة جولة التمثيل",
                  color = NeonAmber,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "$duration ثانية",
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                listOf(30, 60, 90, 120).forEach { sec ->
                  val isSelected = duration == sec
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(10.dp))
                      .background(if (isSelected) NeonAmber.copy(alpha = 0.25f) else DarkSurfaceElevated)
                      .border(
                        1.dp,
                        if (isSelected) NeonAmber else DarkSurfaceBorder,
                        RoundedCornerShape(10.dp)
                      )
                      .clickable {
                        duration = sec
                        onSetDuration(sec)
                      }
                      .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${sec}ث",
                      color = if (isSelected) NeonAmber else TextSecondary,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                    )
                  }
                }
              }
            }
          }
        }

        // Section C: Category Header
        item {
          Text(
            text = "🎬 اختر تصنيف الكلمات والأفلام:",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
        }

        // Category Cards List
        items(GameMasterData.charadesCategories) { cat ->
          val isSelected = selectedCategory.id == cat.id
          val isUnlocked = !cat.isVip || uiState.isAllVipUnlocked || uiState.unlockedPackIds.contains(cat.id)
          val cardBorder = when {
            isSelected -> NeonAmber
            cat.isVip && !isUnlocked -> NeonAmber.copy(alpha = 0.6f)
            else -> DarkSurfaceBorder
          }
          val cardBackground = if (isSelected) DarkSurfaceElevated else DarkSurface

          NeonCard(
            borderColor = cardBorder,
            backgroundColor = cardBackground,
            cornerRadius = 16.dp,
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                if (isUnlocked) {
                  selectedCategory = cat
                  onSelectCategory(cat)
                } else {
                  onPromptVipUnlock(cat)
                }
              }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) NeonAmber.copy(alpha = 0.2f) else DarkSurfaceElevated)
                    .border(
                      1.dp,
                      if (isSelected) NeonAmber else if (cat.isVip) NeonAmber.copy(alpha = 0.5f) else DarkSurfaceBorder,
                      RoundedCornerShape(12.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = cat.icon, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = cat.name,
                      color = if (isSelected) NeonAmber else TextPrimary,
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold
                    )
                    if (cat.isVip) {
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(if (isUnlocked) Color(0xFF1B382B) else NeonAmber.copy(alpha = 0.25f))
                          .padding(horizontal = 5.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = if (isUnlocked) "👑 مفعل" else "👑 VIP",
                          color = if (isUnlocked) Color(0xFF00E676) else NeonAmber,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Black
                        )
                      }
                    }
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = if (cat.isVip && !isUnlocked) "🔒 اضغط للمشاهدة والفتح لمجموعتك" else cat.description,
                    color = if (cat.isVip && !isUnlocked) NeonAmber else TextMuted,
                    fontSize = 11.sp,
                    maxLines = 2
                  )
                }
              }

              if (isSelected) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(NeonAmber),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Bottom Start Button
      NeonPrimaryButton(
        text = "ابدأ التمثيل بدون كلام 🎭🎬",
        onClick = {
          onSelectCategory(selectedCategory)
          onSetDuration(duration)
          onSetTeamMode(isTeamMode)
          onStartGame()
        },
        icon = Icons.Default.PlayArrow,
        accentColor = NeonAmber,
        testTag = "start_charades_button",
        modifier = Modifier.height(58.dp)
      )
    }

    if (uiState.showRulesModal) {
      RulesModal(
        gameMode = GameMode.CHARADES,
        onDismiss = { onShowRules(false) }
      )
    }
  }
}
