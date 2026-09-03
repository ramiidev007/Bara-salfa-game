package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import com.example.util.SoundEffectManager
import com.example.util.UnityAdsManager
import com.example.viewmodel.GameUiState

@Composable
fun ResultScreen(
  uiState: GameUiState,
  onGuessWord: (String) -> Unit,
  onRerollPunishment: () -> Unit,
  onUseFiftyFiftyHint: () -> Unit,
  onShieldPunishment: () -> Unit,
  onPlayAgain: () -> Unit,
  onGoHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val impostorPlayer = uiState.players.getOrNull(uiState.impostorIndex)
  val isCaught = uiState.impostorCaught
  val hasGuessed = uiState.impostorGuessedCorrectly != null
  val isImpostorVictorious = uiState.impostorGuessedCorrectly == true || (!isCaught && uiState.impostorGuessedCorrectly != false)

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 24.dp, vertical = 20.dp)
      .verticalScroll(rememberScrollState())
      .testTag("result_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      if (!hasGuessed) {
        // Step 1: Impostor MCQ Guess Stage
        Box(
          modifier = Modifier
            .size(84.dp)
            .clip(CircleShape)
            .background(NeonPurple.copy(alpha = 0.2f))
            .border(2.dp, NeonPurple, CircleShape)
            .shadow(24.dp, shape = CircleShape, ambientColor = NeonPurple),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "🤫", fontSize = 42.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "دور المحتال للتخمين 🕵️",
            color = NeonPink,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "سلم الجوال لـ: ${impostorPlayer?.name ?: "المحتال"}",
          style = MaterialTheme.typography.displayMedium,
          color = TextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (isCaught) {
            "🎯 كشفك اللاعبون بالتصويت!\nلكن فرصتك للفوز إذا خمنت الكلمة السرية الصحيحة:"
          } else {
            "🎭 خدعت الجميع وصوتوا ضد شخص بريء!\nخمن الكلمة السرية لتحقيق فوز ساحق:"
          },
          style = MaterialTheme.typography.bodyMedium,
          color = if (isCaught) NeonAmber else NeonGreen,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Multiple choice 4 options grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "اختر الكلمة السرية من الخيارات 👇",
            color = NeonCyan,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )

          // 50:50 Lifeline Button
          if (!uiState.isFiftyFiftyUsed && uiState.impostorGuessOptions.size > 2) {
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(NeonAmber.copy(alpha = 0.18f))
                .border(1.dp, NeonAmber, RoundedCornerShape(12.dp))
                .clickable {
                  val act = context as? Activity
                  if (act != null) {
                    SoundEffectManager.playTapSound(context)
                    UnityAdsManager.showRewardedAd(
                      activity = act,
                      onRewardEarned = {
                        SoundEffectManager.playSuccessSound(context)
                        onUseFiftyFiftyHint()
                        Toast.makeText(context, "💡 تم حذف خيارين خاطئين بنجاح!", Toast.LENGTH_SHORT).show()
                      },
                      onError = {
                        onUseFiftyFiftyHint()
                      }
                    )
                  } else {
                    onUseFiftyFiftyHint()
                  }
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("fifty_fifty_hint_button"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = NeonAmber,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "مساعدة 50:50 🎬",
                color = NeonAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          uiState.impostorGuessOptions.forEachIndexed { index, option ->
            val isEliminated = uiState.eliminatedGuessOptions.contains(option)
            NeonCard(
              borderColor = if (isEliminated) DarkSurfaceBorder.copy(alpha = 0.3f) else DarkSurfaceBorder,
              backgroundColor = if (isEliminated) DarkSurface.copy(alpha = 0.4f) else DarkSurface,
              cornerRadius = 16.dp,
              onClick = {
                if (!isEliminated) {
                  onGuessWord(option)
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("guess_option_$index")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "${GameMasterData.getEmojiForWord(option, uiState.selectedCategory?.icon ?: "🔑")} $option",
                    color = if (isEliminated) TextMuted else TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isEliminated) TextDecoration.LineThrough else null
                  )
                  if (isEliminated) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "(مستبعد ❌)",
                      color = NeonRed,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${index + 1}",
                    color = if (isEliminated) TextMuted else NeonPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      } else {
        // Step 2: Final Win / Loss Announcement
        val isImpostorGuessedRight = uiState.impostorGuessedCorrectly == true

        val winnerTitle: String
        val winnerSubtitle: String
        val outcomeColor: Color
        val outcomeEmoji: String

        if (isImpostorGuessedRight) {
          winnerTitle = "👑 فاز المحتال (${impostorPlayer?.name})!"
          winnerSubtitle = if (isCaught) {
            "رغم كشفه بالتصويت، استطاع تخمين الكلمة السرية بذكاء وسرق الفوز! 🎉"
          } else {
            "نجح في خداع الجميع وخمن الكلمة السرية بشكل صحيح تماماً! 🌟"
          }
          outcomeColor = NeonPurple
          outcomeEmoji = "👑"
        } else if (!isCaught) {
          winnerTitle = "🎭 فاز المحتال (${impostorPlayer?.name}) بالتمويه!"
          winnerSubtitle = "نجح في التخفي وخدع الجميع، رغم أنه أخطأ في تخمين الكلمة السرية! 🤫"
          outcomeColor = NeonCyan
          outcomeEmoji = "🎭"
        } else {
          winnerTitle = "🏆 فاز باقي اللاعبين!"
          winnerSubtitle = "كشفوا المحتال (${impostorPlayer?.name}) وفشل في تخمين الكلمة السرية! 🎯"
          outcomeColor = NeonGreen
          outcomeEmoji = "🏆"
        }

        Box(
          modifier = Modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(outcomeColor.copy(alpha = 0.2f))
            .border(2.5.dp, outcomeColor, CircleShape)
            .shadow(24.dp, shape = CircleShape, ambientColor = outcomeColor),
          contentAlignment = Alignment.Center
        ) {
          Text(text = outcomeEmoji, fontSize = 44.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = winnerTitle,
          style = MaterialTheme.typography.displayMedium,
          color = outcomeColor,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = winnerSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Outcome Details Card
        NeonCard(
          borderColor = outcomeColor.copy(alpha = 0.4f),
          backgroundColor = DarkSurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "اللي كان برا السالفة:",
                color = TextSecondary,
                fontSize = 14.sp
              )
              Text(
                text = "🕵️ ${impostorPlayer?.name ?: ""}",
                color = NeonPink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "الكلمة السرية:",
                color = TextSecondary,
                fontSize = 14.sp
              )
              Text(
                text = "${GameMasterData.getEmojiForWord(uiState.secretWord, uiState.selectedCategory?.icon ?: "🔑")} ${uiState.secretWord}",
                color = NeonGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "المتهم بالأغلبية:",
                color = TextSecondary,
                fontSize = 14.sp
              )
              Text(
                text = "${uiState.votedSuspect?.name ?: ""} ${if (isCaught) "(تم كشفه! 🎯)" else "(بريء! 😇)"}",
                color = if (isCaught) NeonGreen else NeonCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (uiState.votesCountMap.isNotEmpty()) {
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "توزيع الأصوات:",
                  color = TextSecondary,
                  fontSize = 14.sp
                )
                Text(
                  text = uiState.votesCountMap.entries.joinToString(" • ") { entry ->
                    val pName = uiState.players.find { it.id == entry.key }?.name ?: ""
                    "$pName (${entry.value})"
                  },
                  color = NeonPurple,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            val guessWord = uiState.selectedGuess ?: ""
            val guessEmoji = GameMasterData.getEmojiForWord(guessWord, "")
            val emojiPrefix = if (guessEmoji.isNotBlank()) "$guessEmoji " else ""
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "تخمين المحتال:",
                color = TextSecondary,
                fontSize = 14.sp
              )
              Text(
                text = "$emojiPrefix$guessWord (${if (isImpostorGuessedRight) "صحيح! 🎉" else "خطأ! ❌"})",
                color = if (isImpostorGuessedRight) NeonGreen else NeonRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Punishment Section if Impostor lost completely
        if (isCaught && !isImpostorGuessedRight) {
          Spacer(modifier = Modifier.height(18.dp))

          NeonCard(
            borderColor = NeonRed.copy(alpha = 0.6f),
            backgroundColor = Color(0xFF241016),
            cornerRadius = 18.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "⚖️ عقاب المحتال الخاسر (${impostorPlayer?.name}):",
                style = MaterialTheme.typography.titleMedium,
                color = NeonAmber,
                fontWeight = FontWeight.Black
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = uiState.currentPunishment,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Reroll Punishment button
                Row(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .clickable { onRerollPunishment() }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "تغيير الحكم 🎲",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                // Shield of Immunity (Ad benefit)
                if (!uiState.isPunishmentShielded) {
                  Row(
                    modifier = Modifier
                      .weight(1.2f)
                      .clip(RoundedCornerShape(12.dp))
                      .background(NeonAmber.copy(alpha = 0.2f))
                      .border(1.dp, NeonAmber, RoundedCornerShape(12.dp))
                      .clickable {
                        val act = context as? Activity
                        if (act != null) {
                          SoundEffectManager.playTapSound(context)
                          UnityAdsManager.showRewardedAd(
                            activity = act,
                            onRewardEarned = {
                              SoundEffectManager.playSuccessSound(context)
                              onShieldPunishment()
                              Toast.makeText(context, "🛡️ تم تفعيل درع الإعفاء!", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                              onShieldPunishment()
                            }
                          )
                        } else {
                          onShieldPunishment()
                        }
                      }
                      .padding(horizontal = 8.dp, vertical = 8.dp)
                      .testTag("shield_punishment_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Shield,
                      contentDescription = null,
                      tint = NeonAmber,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "إعفاء من الحكم 🎬",
                      color = NeonAmber,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Bottom Action Buttons (shown on final outcome screen)
    if (hasGuessed) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        NeonPrimaryButton(
          text = "العب مرة ثانية",
          onClick = onPlayAgain,
          icon = Icons.Default.Refresh,
          accentColor = NeonGreen,
          testTag = "play_again_button"
        )

        NeonSecondaryButton(
          text = "الرئيسية",
          onClick = onGoHome,
          icon = Icons.Default.Home,
          borderColor = DarkSurfaceBorder,
          testTag = "go_home_button"
        )
      }
    }
  }
}
