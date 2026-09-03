package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.model.GameMasterData
import com.example.util.SoundEffectManager
import com.example.ui.components.NeonCard
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
fun CategorySelectionScreen(
  uiState: GameUiState,
  onSelectCategory: (Category) -> Unit,
  onPromptVipUnlock: (Category) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 20.dp, vertical = 16.dp)
      .testTag("category_selection_screen")
  ) {
    val context = LocalContext.current
    // Header
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

      Column {
        Text(
          text = "اختيار التصنيف",
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "اختر موضوع الجولة أو افتح حزم الـ VIP الحصرية!",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Categories Grid
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.weight(1f)
    ) {
      items(GameMasterData.categories, key = { it.id }) { category ->
        val isUnlocked = !category.isVip || uiState.isAllVipUnlocked || uiState.unlockedPackIds.contains(category.id)
        CategoryCard(
          category = category,
          isUnlocked = isUnlocked,
          onClick = {
            if (isUnlocked) {
              onSelectCategory(category)
            } else {
              onPromptVipUnlock(category)
            }
          }
        )
      }
    }
  }
}

@Composable
private fun CategoryCard(
  category: Category,
  isUnlocked: Boolean,
  onClick: () -> Unit
) {
  val accentColor = when {
    category.isVip -> NeonAmber
    category.id == "c1" -> NeonGreen
    category.id == "c2" -> NeonPurple
    category.id == "c3" -> NeonCyan
    category.id == "c4" -> NeonPink
    category.id == "c5" -> NeonPurple
    else -> NeonGreen
  }

  NeonCard(
    borderColor = if (category.isVip && !isUnlocked) NeonAmber.copy(alpha = 0.6f) else DarkSurfaceBorder,
    backgroundColor = DarkSurface,
    cornerRadius = 20.dp,
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("category_card_${category.id}")
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(accentColor.copy(alpha = 0.25f), DarkSurfaceElevated)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = category.icon,
            fontSize = 32.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = category.name,
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        if (category.isVip && !isUnlocked) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "🔒 فتح الحزمة (إعلانين)",
            color = NeonAmber,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // VIP Badge in corner
      if (category.isVip) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isUnlocked) NeonGreen.copy(alpha = 0.2f) else NeonAmber.copy(alpha = 0.25f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (isUnlocked) "👑 مفعل" else "👑 VIP",
            color = if (isUnlocked) NeonGreen else NeonAmber,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
