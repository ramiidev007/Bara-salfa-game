package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameMode
import com.example.util.SoundEffectManager
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
import com.example.ui.theme.TextDisabled
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NeonPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: ImageVector? = null,
  accentColor: Color = NeonGreen,
  testTag: String = "primary_button"
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.96f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "button_scale"
  )

  val context = LocalContext.current

  Button(
    onClick = {
      SoundEffectManager.playTapSound(context)
      onClick()
    },
    enabled = enabled,
    interactionSource = interactionSource,
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = accentColor,
      contentColor = DarkBackground,
      disabledContainerColor = DarkSurfaceElevated,
      disabledContentColor = TextDisabled
    ),
    modifier = modifier
      .testTag(testTag)
      .scale(scale)
      .fillMaxWidth()
      .heightIn(min = 54.dp)
      .shadow(
        elevation = if (enabled) 12.dp else 0.dp,
        shape = RoundedCornerShape(16.dp),
        ambientColor = if (enabled) accentColor.copy(alpha = 0.5f) else Color.Transparent,
        spotColor = if (enabled) accentColor.copy(alpha = 0.8f) else Color.Transparent
      )
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun NeonSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  borderColor: Color = NeonPurple,
  textColor: Color = TextPrimary,
  icon: ImageVector? = null,
  testTag: String = "secondary_button"
) {
  val context = LocalContext.current
  OutlinedButton(
    onClick = {
      SoundEffectManager.playTapSound(context)
      onClick()
    },
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.5.dp, borderColor.copy(alpha = 0.8f)),
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = textColor,
      containerColor = DarkSurface.copy(alpha = 0.6f)
    ),
    modifier = modifier
      .testTag(testTag)
      .fillMaxWidth()
      .heightIn(min = 52.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = borderColor,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun NeonCard(
  modifier: Modifier = Modifier,
  borderColor: Color = DarkSurfaceBorder,
  backgroundColor: Color = DarkSurface,
  cornerRadius: Dp = 18.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  Card(
    shape = RoundedCornerShape(cornerRadius),
    border = BorderStroke(1.2.dp, borderColor),
    colors = CardDefaults.cardColors(
      containerColor = backgroundColor
    ),
    modifier = modifier
      .then(
        if (onClick != null) {
          Modifier.clickable {
            SoundEffectManager.playTapSound(context)
            onClick()
          }
        } else Modifier
      )
  ) {
    content()
  }
}

@Composable
fun RulesModal(
  gameMode: GameMode = GameMode.SALFA_BARRA,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DarkSurface,
      border = BorderStroke(1.5.dp, if (gameMode == GameMode.WORD_BOMB) NeonRed.copy(alpha = 0.8f) else NeonPurple.copy(alpha = 0.8f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("rules_modal")
    ) {
      Column(
        modifier = Modifier
          .padding(24.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val headerEmoji = when (gameMode) {
          GameMode.WORD_BOMB -> "💣"
          GameMode.TRUTH_OR_DARE -> "🍾"
          GameMode.SALFA_BARRA -> "📜"
          GameMode.CHARADES -> "🎭"
        }
        val headerColor = when (gameMode) {
          GameMode.WORD_BOMB -> NeonRed
          GameMode.TRUTH_OR_DARE -> NeonPink
          GameMode.SALFA_BARRA -> NeonPurple
          GameMode.CHARADES -> NeonAmber
        }

        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(headerColor.copy(alpha = 0.4f), Color.Transparent)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(text = headerEmoji, fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        val titleText = when (gameMode) {
          GameMode.WORD_BOMB -> "طريقة لعب القنبلة الموقوتة 💣"
          GameMode.TRUTH_OR_DARE -> "طريقة لعب صراحة أو جرأة 🍾"
          GameMode.SALFA_BARRA -> "طريقة لعب برا السالفة 🕵️‍♂️"
          GameMode.CHARADES -> "طريقة لعب تمثيل بدون كلام 🎭"
        }

        Text(
          text = titleText,
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (gameMode == GameMode.CHARADES) {
          RuleItem(
            number = "١",
            title = "اختيار النمط والتصنيف",
            description = "اختر اللعب بنمط الفرق (فريق أ ضد ب) أو اللعب الفردي، وحدد تصنيف الأفلام، الأمثال، أو المواقف الكوميدية."
          )
          RuleItem(
            number = "٢",
            title = "السرية وبدء المؤقت ⏱️",
            description = "يرى ممثل الفريق الكلمة السرية، ثم يضغط على 'ابدأ التمثيل' لينطلق العداد التنازلي الحماسي!"
          )
          RuleItem(
            number = "٣",
            title = "القانون الذهبي: لا كلام ولا أصوات! 🤫",
            description = "الممثل ممنوع تماماً من الكلام أو إصدار أي صوت أو تحريك الشفاه! الاعتماد كلياً على لغة الجسد والإشارات."
          )
          RuleItem(
            number = "٤",
            title = "تسجيل النقاط والفوز 🎉",
            description = "إذا خمن فريقه الكلمة قبل انتهاء الوقت يضغط 'صح 🎉' لكسب نقطة والانتقال لكلمة جديدة. الفريق الأكثر نقاطاً يفوز!"
          )
        } else if (gameMode == GameMode.TRUTH_OR_DARE) {
          RuleItem(
            number = "١",
            title = "تحديد اللاعبين والباقة",
            description = "أضف أسماء أصدقائك واختر باقة الأسئلة المفضلة (جلسة وضحك، صراحة نارية 🔥، أو سوالف دوام)."
          )
          RuleItem(
            number = "٢",
            title = "لف القارورة العجيبة 🍾",
            description = "اضغط على زر تدوير القارورة لتنطلق بدوران فيزيائي سينمائي مثير حتى تستقر وتشير إلى اللاعب المختار!"
          )
          RuleItem(
            number = "٣",
            title = "صراحة 💬 أو جرأة 🔥؟",
            description = "يختار اللاعب المحدد إما الإجابة بصدق تام على سؤال الصراحة، أو تنفيذ تحدي الجرأة أمام الجميع!"
          )
          RuleItem(
            number = "٤",
            title = "تغيير السؤال أو الانتقال للتالي",
            description = "يمكن تغيير السؤال بضغطة زر، أو المتابعة فوراً لتدوير القارورة من جديد للجولة القادمة."
          )
        } else if (gameMode == GameMode.WORD_BOMB) {
          RuleItem(
            number = "١",
            title = "التحدي وتوقيت الفتيل السري",
            description = "يتم اختيار موضوع التحدي (مثل: أكلات بحرف معين أو ماركات). يبدأ فتيل القنبلة بالاشتعال بوقت سري عشوائي!"
          )
          RuleItem(
            number = "٢",
            title = "اذكر الكلمة ومرر بسرعة!",
            description = "اللاعب الذي يحمل الجوال يذكر كلمة صحيحة مطابقة للشرط ثم يضغط فوراً على 'مرر القنبلة 💥' ليمررها للشخص التالي."
          )
          RuleItem(
            number = "٣",
            title = "ممنوع التكرار أو التردد",
            description = "الكلمة المذكورة لا يجوز تكرارها، وسرعة البديهة هي سر النجاة!"
          )
          RuleItem(
            number = "٤",
            title = "الانفجار والعقاب! 💥",
            description = "القنبلة ستنفجر في أي لحظة بشكل مفاجئ.. الشخص الذي بيده الجوال لحظة الانفجار يخسر وينفذ الحكم المطلوب!"
          )
        } else {
          RuleItem(
            number = "١",
            title = "تحديد اللاعبين والتصنيف",
            description = "يجتمع من 3 إلى 10 لاعبين ويتم اختيار أحد التصنيفات المتوفرة."
          )
          RuleItem(
            number = "٢",
            title = "السرية والتمرير",
            description = "يمرر الهاتف لكل لاعب بالترتيب. يضغط مطولاً ليرى الكلمة السرية، بينما لاعب واحد عشوائياً سيكون (برا السالفة) ولن تظهر له الكلمة!"
          )
          RuleItem(
            number = "٣",
            title = "وقت التحقيق والأسئلة",
            description = "يسأل اللاعبون بعضهم بالدور أسئلة ذكية عن الكلمة. انتبه ألا تفضح الكلمة للمحتال، وانتبه لو كنت برا السالفة أن تمشي مع الجو!"
          )
          RuleItem(
            number = "٤",
            title = "التصويت والكشف",
            description = "يصوت كل لاعب على الشخص المشبوه بالأغلبية."
          )
          RuleItem(
            number = "٥",
            title = "تخمين المحتال والنتيجة",
            description = "إذا تم كشف المحتال، يحصل على فرصة لتخمين الكلمة من 4 خيارات، والفائز يُعلن بناءً على التخمين والتصويت!"
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        NeonPrimaryButton(
          text = "فهمت! يلا نلعب 🚀",
          onClick = onDismiss,
          accentColor = if (gameMode == GameMode.WORD_BOMB) NeonRed else NeonGreen,
          testTag = "close_rules_button"
        )
      }
    }
  }
}

@Composable
private fun RuleItem(
  number: String,
  title: String,
  description: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
      .background(NeonPurple.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = number,
        color = NeonPurple,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = description,
        color = TextSecondary,
        fontSize = 13.sp,
        lineHeight = 18.sp
      )
    }
  }
}

@Composable
fun ScreenHeader(
  title: String,
  subtitle: String? = null,
  onBackClick: (() -> Unit)? = null,
  rightActionIcon: ImageVector? = null,
  onRightActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (onBackClick != null) {
      IconButton(
        onClick = {
          SoundEffectManager.playTapSound(context)
          onBackClick()
        },
        modifier = Modifier.testTag("screen_header_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "الرجوع",
          tint = TextPrimary
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
    }

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary,
        fontWeight = FontWeight.Bold
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    }

    if (rightActionIcon != null && onRightActionClick != null) {
      IconButton(
        onClick = {
          SoundEffectManager.playTapSound(context)
          onRightActionClick()
        },
        modifier = Modifier.testTag("screen_header_right_action")
      ) {
        Icon(
          imageVector = rightActionIcon,
          contentDescription = "مساعدة",
          tint = TextSecondary
        )
      }
    }
  }
}
