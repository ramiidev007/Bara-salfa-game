package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.model.GameMode
import com.example.model.GamePhase
import com.example.model.TodChoiceType
import com.example.ui.components.VipPerksModal
import com.example.ui.components.VipUnlockDialog
import com.example.ui.screens.BombGameScreen
import com.example.ui.screens.BombResultScreen
import com.example.ui.screens.BombTopicSelectionScreen
import com.example.ui.screens.CategorySelectionScreen
import com.example.ui.screens.CharadesGameScreen
import com.example.ui.screens.CharadesResultScreen
import com.example.ui.screens.CharadesSetupScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InterrogationTimerScreen
import com.example.ui.screens.PassAndRevealScreen
import com.example.ui.screens.PlayerSetupScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.TruthOrDarePromptScreen
import com.example.ui.screens.TruthOrDareSpinScreen
import com.example.ui.screens.VotingScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.util.UnityAdsManager
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: GameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Unity Ads with Android Game ID
    UnityAdsManager.initialize(this)

    setContent {
      MyApplicationTheme {
        // Enforce RTL globally for entire Arabic experience
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(DarkBackground)
              .statusBarsPadding()
              .navigationBarsPadding()
          ) {
            AlSalfaBarraApp(
              viewModel = viewModel,
              onTriggerInterstitial = { UnityAdsManager.showInterstitialAfterGame(this@MainActivity) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun AlSalfaBarraApp(
  viewModel: GameViewModel,
  onTriggerInterstitial: () -> Unit = {}
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val activity = context as? Activity

  // Handle system back navigation according to game phase
  BackHandler(enabled = uiState.gamePhase != GamePhase.HOME) {
    when (uiState.gamePhase) {
      GamePhase.PLAYER_SETUP -> viewModel.setPhase(GamePhase.HOME)
      GamePhase.CATEGORY_SELECTION -> viewModel.setPhase(GamePhase.PLAYER_SETUP)
      GamePhase.PASS_AND_REVEAL -> viewModel.setPhase(GamePhase.CATEGORY_SELECTION)
      GamePhase.TIMER -> viewModel.setPhase(GamePhase.PASS_AND_REVEAL)
      GamePhase.VOTING -> viewModel.setPhase(GamePhase.TIMER)
      GamePhase.RESULT -> viewModel.resetToHome()
      
      // Word Bomb back flow
      GamePhase.BOMB_TOPIC_SELECTION -> viewModel.setPhase(GamePhase.PLAYER_SETUP)
      GamePhase.BOMB_GAME -> viewModel.setPhase(GamePhase.BOMB_TOPIC_SELECTION)
      GamePhase.BOMB_RESULT -> viewModel.resetToHome()
      
      // Truth or Dare back flow
      GamePhase.TOD_SPIN -> viewModel.setPhase(GamePhase.PLAYER_SETUP)
      GamePhase.TOD_PROMPT -> viewModel.nextBottleSpin()

      // Charades (تمثيل بدون كلام) back flow
      GamePhase.CHARADES_SETUP -> viewModel.setPhase(GamePhase.PLAYER_SETUP)
      GamePhase.CHARADES_GAME -> viewModel.setPhase(GamePhase.CHARADES_SETUP)
      GamePhase.CHARADES_ROUND_RESULT -> viewModel.setPhase(GamePhase.CHARADES_SETUP)

      GamePhase.HOME -> Unit
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    AnimatedContent(
      targetState = uiState.gamePhase,
      transitionSpec = {
        fadeIn() togetherWith fadeOut()
      },
      label = "game_phase_transition"
    ) { phase ->
      when (phase) {
        GamePhase.HOME -> {
          HomeScreen(
            uiState = uiState,
            onSelectGameMode = { viewModel.selectGameMode(it) },
            onPlayNow = { viewModel.setPhase(GamePhase.PLAYER_SETUP) },
            onShowRules = { viewModel.showRules(it) },
            onOpenPerksHub = { viewModel.toggleVipPerksHub(true) }
          )
        }

        GamePhase.PLAYER_SETUP -> {
          PlayerSetupScreen(
            uiState = uiState,
            onAddPlayer = { viewModel.addPlayer(it) },
            onRemovePlayer = { viewModel.removePlayer(it) },
            onNext = {
              when (uiState.selectedGameMode) {
                GameMode.WORD_BOMB -> viewModel.setPhase(GamePhase.BOMB_TOPIC_SELECTION)
                GameMode.TRUTH_OR_DARE -> viewModel.setPhase(GamePhase.TOD_SPIN)
                GameMode.SALFA_BARRA -> viewModel.setPhase(GamePhase.CATEGORY_SELECTION)
                GameMode.CHARADES -> viewModel.setPhase(GamePhase.CHARADES_SETUP)
              }
            },
            onBack = { viewModel.setPhase(GamePhase.HOME) }
          )
        }

        // ---------------- Salfa Barra Game Phases ----------------

        GamePhase.CATEGORY_SELECTION -> {
          CategorySelectionScreen(
            uiState = uiState,
            onSelectCategory = { viewModel.selectCategoryAndStart(it) },
            onPromptVipUnlock = { cat -> viewModel.promptVipUnlock(cat.id, cat.name, "category") },
            onBack = { viewModel.setPhase(GamePhase.PLAYER_SETUP) }
          )
        }

        GamePhase.PASS_AND_REVEAL -> {
          PassAndRevealScreen(
            uiState = uiState,
            onHoldingChange = { viewModel.setHolding(it) },
            onNextPlayer = { viewModel.nextRevealPlayer() },
            onSkipCountdown = { viewModel.skipHandoffCountdown() }
          )
        }

        GamePhase.TIMER -> {
          InterrogationTimerScreen(
            uiState = uiState,
            onNextQuestionTurn = { viewModel.nextQuestionTurn() },
            onRandomizeQuestionPair = { viewModel.randomizeQuestionPair() },
            onNextHint = { viewModel.nextHint() },
            onStartVoting = { viewModel.setPhase(GamePhase.VOTING) }
          )
        }

        GamePhase.VOTING -> {
          VotingScreen(
            uiState = uiState,
            onVotePlayer = { viewModel.castVote(it) }
          )
        }

        GamePhase.RESULT -> {
          ResultScreen(
            uiState = uiState,
            onGuessWord = { viewModel.submitImpostorGuess(it) },
            onRerollPunishment = { viewModel.rerollPunishment() },
            onUseFiftyFiftyHint = { viewModel.applyFiftyFiftyHint() },
            onShieldPunishment = { viewModel.shieldPunishment() },
            onPlayAgain = {
              onTriggerInterstitial()
              viewModel.playAgain()
            },
            onGoHome = {
              onTriggerInterstitial()
              viewModel.resetToHome()
            }
          )
        }

        // ---------------- Word Bomb Game Phases ----------------

        GamePhase.BOMB_TOPIC_SELECTION -> {
          BombTopicSelectionScreen(
            uiState = uiState,
            onSelectTopic = { viewModel.selectBombTopicAndStart(it) },
            onPromptVipUnlock = { topic -> viewModel.promptVipUnlock(topic.id, topic.title, "bomb_topic") },
            onBack = { viewModel.setPhase(GamePhase.PLAYER_SETUP) },
            onGoHome = { viewModel.resetToHome() }
          )
        }

        GamePhase.BOMB_GAME -> {
          BombGameScreen(
            uiState = uiState,
            onPassBomb = { viewModel.passBomb() },
            onRerollChallenge = { viewModel.rerollBombChallenge() },
            onBackToTopics = { viewModel.setPhase(GamePhase.BOMB_TOPIC_SELECTION) },
            onGoHome = { viewModel.resetToHome() },
            onShowRules = { viewModel.showRules(it) }
          )
        }

        GamePhase.BOMB_RESULT -> {
          BombResultScreen(
            uiState = uiState,
            onPlayAgain = {
              onTriggerInterstitial()
              viewModel.restartBombRound()
            },
            onChangeTopic = {
              onTriggerInterstitial()
              viewModel.setPhase(GamePhase.BOMB_TOPIC_SELECTION)
            },
            onRerollPunishment = { viewModel.rerollPunishment() },
            onShieldPunishment = { viewModel.shieldPunishment() },
            onGoHome = {
              onTriggerInterstitial()
              viewModel.resetToHome()
            }
          )
        }

        // ---------------- Truth or Dare (Spinning Bottle) Game Phases ----------------

        GamePhase.TOD_SPIN -> {
          TruthOrDareSpinScreen(
            uiState = uiState,
            onSpinBottle = { viewModel.spinBottle() },
            onChooseTruth = { viewModel.chooseTruthOrDare(TodChoiceType.TRUTH) },
            onChooseDare = { viewModel.chooseTruthOrDare(TodChoiceType.DARE) },
            onSelectPack = { viewModel.selectTodPack(it) },
            onPromptVipUnlock = { pack -> viewModel.promptVipUnlock(pack.id, pack.title, "tod_pack") },
            onShowRules = { viewModel.showRules(it) },
            onBackToHome = { viewModel.setPhase(GamePhase.PLAYER_SETUP) }
          )
        }

        GamePhase.TOD_PROMPT -> {
          TruthOrDarePromptScreen(
            uiState = uiState,
            onRerollPrompt = { viewModel.rerollTodPrompt() },
            onSwitchToOppositeChoice = {
              val currentChoice = uiState.todChoice ?: TodChoiceType.TRUTH
              val newChoice = if (currentChoice == TodChoiceType.TRUTH) TodChoiceType.DARE else TodChoiceType.TRUTH
              viewModel.chooseTruthOrDare(newChoice)
            },
            onNextSpin = { viewModel.nextBottleSpin() },
            onShowRules = { viewModel.showRules(it) },
            onBackToHome = {
              onTriggerInterstitial()
              viewModel.resetToHome()
            }
          )
        }

        // ---------------- Charades (تمثيل بدون كلام) Game Phases ----------------

        GamePhase.CHARADES_SETUP -> {
          CharadesSetupScreen(
            uiState = uiState,
            onSelectCategory = { viewModel.selectCharadesCategory(it) },
            onPromptVipUnlock = { cat -> viewModel.promptVipUnlock(cat.id, cat.name, "charades_category") },
            onSetDuration = { viewModel.setCharadesDuration(it) },
            onSetTeamMode = { viewModel.setCharadesTeamMode(it) },
            onSetTeamNames = { a, b -> viewModel.setCharadesTeamNames(a, b) },
            onStartGame = { viewModel.startCharadesGame() },
            onBack = { viewModel.setPhase(GamePhase.PLAYER_SETUP) },
            onGoHome = { viewModel.resetToHome() },
            onShowRules = { viewModel.showRules(it) }
          )
        }

        GamePhase.CHARADES_GAME -> {
          CharadesGameScreen(
            uiState = uiState,
            onStartTimer = { viewModel.startCharadesTurnTimer() },
            onPauseTimer = { viewModel.pauseCharadesTurnTimer() },
            onToggleWordReveal = { viewModel.toggleCharadesWordReveal() },
            onCorrectGuess = { viewModel.charadesCorrectGuess() },
            onSkipWord = { viewModel.charadesSkipWord() },
            onEndTurn = { viewModel.endCharadesTurn() },
            onBackToSetup = { viewModel.setPhase(GamePhase.CHARADES_SETUP) },
            onGoHome = { viewModel.resetToHome() },
            onShowRules = { viewModel.showRules(it) }
          )
        }

        GamePhase.CHARADES_ROUND_RESULT -> {
          CharadesResultScreen(
            uiState = uiState,
            onNextRound = {
              onTriggerInterstitial()
              viewModel.nextCharadesTurn()
            },
            onChangeCategory = {
              onTriggerInterstitial()
              viewModel.setPhase(GamePhase.CHARADES_SETUP)
            },
            onResetScores = { viewModel.resetCharadesScores() },
            onGoHome = {
              onTriggerInterstitial()
              viewModel.resetToHome()
            }
          )
        }
      }
    }

    // Modal Overlays
    if (uiState.showVipUnlockModal) {
      val packId = uiState.pendingVipPackId ?: ""
      val currentProgress = uiState.packProgressMap[packId] ?: 0
      VipUnlockDialog(
        packId = packId,
        title = uiState.pendingVipTitle,
        icon = uiState.pendingVipIcon,
        description = uiState.pendingVipDesc,
        currentProgress = currentProgress,
        requiredAds = uiState.requiredAdsPerPack,
        onWatchAdSuccess = { id -> viewModel.recordAdWatchedForPack(id) },
        onDismiss = { viewModel.dismissVipUnlock() }
      )
    }

    if (uiState.showPerksHubModal) {
      VipPerksModal(
        unlockedPacks = uiState.unlockedPackIds,
        packProgressMap = uiState.packProgressMap,
        onUnlockPackClicked = { id, title, icon, desc ->
          viewModel.promptVipUnlock(id, title, icon, desc)
        },
        onDismiss = { viewModel.setPerksHubModal(false) }
      )
    }
  }
}
