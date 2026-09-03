package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMasterData
import com.example.model.GameMode
import com.example.model.Player
import com.example.util.SoundEffectManager
import com.example.ui.components.NeonCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonAmber
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
fun PlayerSetupScreen(
  uiState: GameUiState,
  onAddPlayer: (String) -> Unit,
  onRemovePlayer: (String) -> Unit,
  onNext: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var nameInput by remember { mutableStateOf("") }
  val currentMode = uiState.selectedGameMode
  val minPlayers = when (currentMode) {
    GameMode.WORD_BOMB -> 2
    GameMode.TRUTH_OR_DARE -> 2
    GameMode.SALFA_BARRA -> 3
    GameMode.CHARADES -> 2
  }
  val canAddMore = uiState.players.size < 10
  val canProceed = uiState.players.size >= minPlayers
  val accentColor = when (currentMode) {
    GameMode.WORD_BOMB -> NeonRed
    GameMode.TRUTH_OR_DARE -> NeonPink
    GameMode.SALFA_BARRA -> NeonGreen
    GameMode.CHARADES -> NeonAmber
  }

  val modeTitle = when (currentMode) {
    GameMode.WORD_BOMB -> "القنبلة 💣"
    GameMode.TRUTH_OR_DARE -> "صراحة أو جرأة 🍾"
    GameMode.SALFA_BARRA -> "برا السالفة 🕵️"
    GameMode.CHARADES -> "تمثيل بدون كلام 🎭"
  }

  val context = LocalContext.current

  val submitPlayer = {
    if (nameInput.isNotBlank() && canAddMore) {
      SoundEffectManager.playTapSound(context)
      onAddPlayer(nameInput)
      nameInput = ""
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
      .testTag("player_setup_screen"),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Header
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = {
            SoundEffectManager.playTapSound(context)
            onBack()
          },
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "الرجوع",
            tint = TextPrimary
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "إعداد اللاعبين ($modeTitle)",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "أضف أسماء اللاعبين (من $minPlayers إلى 10 لاعبين)",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }

        // Counter Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
              if (canProceed) accentColor.copy(alpha = 0.2f) else NeonPink.copy(alpha = 0.2f)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "${uiState.players.size}/10",
            color = if (canProceed) accentColor else NeonPink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Input Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = nameInput,
          onValueChange = { nameInput = it },
          placeholder = { Text("اسم اللاعب...", color = TextMuted) },
          singleLine = true,
          enabled = canAddMore,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(onDone = { submitPlayer() }),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface,
            focusedBorderColor = accentColor,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = accentColor
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("player_name_input")
        )

        Spacer(modifier = Modifier.width(10.dp))

        NeonCard(
          backgroundColor = if (canAddMore && nameInput.isNotBlank()) accentColor else DarkSurfaceElevated,
          borderColor = if (canAddMore && nameInput.isNotBlank()) accentColor else DarkSurfaceBorder,
          cornerRadius = 16.dp,
          onClick = { submitPlayer() },
          modifier = Modifier
            .size(54.dp)
            .testTag("add_player_button")
        ) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "إضافة لاعب",
              tint = if (canAddMore && nameInput.isNotBlank()) DarkBackground else TextMuted
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Suggestions row
      Text(
        text = "اقتراحات سريعة:",
        color = TextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )

      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
      ) {
        val existingNames = uiState.players.map { it.name }
        val availableSuggestions = GameMasterData.samplePlayerNames.filterNot { it in existingNames }
        items(availableSuggestions) { sampleName ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(DarkSurfaceElevated)
              .clickable(enabled = canAddMore) {
                SoundEffectManager.playTapSound(context)
                onAddPlayer(sampleName)
              }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "+ $sampleName",
              color = accentColor,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // Players List
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .padding(vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      itemsIndexed(uiState.players, key = { _, player -> player.id }) { index, player ->
        PlayerItemRow(
          index = index + 1,
          player = player,
          badgeColor = accentColor,
          onDelete = { onRemovePlayer(player.id) }
        )
      }
    }

    // Bottom Action
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      if (!canProceed) {
        Text(
          text = "يجب إضافة $minPlayers لاعبين على الأقل للبدء",
          color = NeonPink,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(bottom = 8.dp)
        )
      }

      NeonPrimaryButton(
        text = "التالي",
        onClick = onNext,
        enabled = canProceed,
        icon = Icons.AutoMirrored.Filled.ArrowForward,
        accentColor = accentColor,
        testTag = "next_button"
      )
    }
  }
}

@Composable
private fun PlayerItemRow(
  index: Int,
  player: Player,
  badgeColor: Color = NeonPurple,
  onDelete: () -> Unit
) {
  val context = LocalContext.current
  NeonCard(
    borderColor = DarkSurfaceBorder,
    backgroundColor = DarkSurface,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(badgeColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$index",
          color = badgeColor,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Text(
        text = player.name,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.weight(1f)
      )

      IconButton(
        onClick = {
          SoundEffectManager.playTapSound(context)
          onDelete()
        },
        modifier = Modifier.testTag("delete_player_${player.id}")
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "حذف",
          tint = NeonRed.copy(alpha = 0.8f)
        )
      }
    }
  }
}
