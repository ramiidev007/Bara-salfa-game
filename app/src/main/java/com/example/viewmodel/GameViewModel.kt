package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BombTopic
import com.example.model.Category
import com.example.model.CharadesCategory
import com.example.model.CharadesTeam
import com.example.model.GameMasterData
import com.example.model.GameMode
import com.example.model.GamePhase
import com.example.model.Player
import com.example.model.TodChoiceType
import com.example.model.TodPack
import com.example.util.PerksManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

data class GameUiState(
  val selectedGameMode: GameMode = GameMode.SALFA_BARRA,
  val gamePhase: GamePhase = GamePhase.HOME,
  val players: List<Player> = listOf(
    Player(id = "p1", name = "أحمد"),
    Player(id = "p2", name = "سارة"),
    Player(id = "p3", name = "فيصل")
  ),
  
  // Salfa Barra state
  val selectedCategory: Category? = null,
  val secretWord: String = "",
  val impostorIndex: Int = -1,
  val currentRevealIndex: Int = 0,
  val isHolding: Boolean = false,
  val hasRevealedCurrent: Boolean = false,
  val isHandoffCountdownActive: Boolean = false,
  val handoffCountdownSeconds: Int = 5,
  val starterPlayerIndex: Int = 0,
  val currentAskerIndex: Int = 0,
  val currentTargetIndex: Int = 1,
  val questionTurnCount: Int = 1,
  val currentHintIndex: Int = 0,
  val currentVoterIndex: Int = 0,
  val votes: Map<String, String> = emptyMap(), // voterId -> suspectId
  val votesCountMap: Map<String, Int> = emptyMap(), // suspectId -> count
  val votedSuspect: Player? = null,
  val impostorCaught: Boolean = false,
  val impostorGuessOptions: List<String> = emptyList(),
  val selectedGuess: String? = null,
  val impostorGuessedCorrectly: Boolean? = null,
  val currentPunishment: String = "",
  
  // Word Bomb (القنبلة الموقوتة) state
  val selectedBombTopic: BombTopic? = null,
  val currentBombChallengeText: String = "",
  val currentBombHolderIndex: Int = 0,
  val bombPassCount: Int = 0,
  val bombTotalFuseSeconds: Int = 30,
  val bombSecondsElapsed: Int = 0,
  val bombIsTicking: Boolean = false,
  val bombExploded: Boolean = false,
  val bombLoser: Player? = null,
  
  // Truth or Dare (صراحة أو جرأة / القارورة الدوارة) state
  val selectedTodPack: TodPack = GameMasterData.todPacks.first(),
  val bottleRotationDegrees: Float = 0f,
  val isBottleSpinning: Boolean = false,
  val selectedTodPlayer: Player? = null,
  val todChoice: TodChoiceType? = null,
  val currentTodPromptText: String = "",
  val todRoundsPlayed: Int = 0,

  // Charades (تمثيل بدون كلام) state
  val selectedCharadesCategory: CharadesCategory = GameMasterData.charadesCategories.first(),
  val charadesDurationSeconds: Int = 60,
  val charadesIsTeamMode: Boolean = true,
  val charadesTeamA: CharadesTeam = CharadesTeam(id = "team_a", name = "فريق النسور 🦅", colorHex = 0xFFFFB800),
  val charadesTeamB: CharadesTeam = CharadesTeam(id = "team_b", name = "فريق الذئاب 🐺", colorHex = 0xFF00F0FF),
  val charadesCurrentTeamTurnIndex: Int = 0, // 0 for Team A, 1 for Team B
  val charadesCurrentActorPlayerIndex: Int = 0,
  val charadesCurrentWord: String = "",
  val charadesWordsGuessedThisTurn: Int = 0,
  val charadesGuessedWordsList: List<String> = emptyList(),
  val charadesTimerSecondsRemaining: Int = 60,
  val charadesIsTimerRunning: Boolean = false,
  val charadesIsWordRevealed: Boolean = false,
  val charadesRoundsPlayed: Int = 0,

  // Dialogs & Modals
  val showRulesModal: Boolean = false,

  // Rewarded Perks & Ad benefits state
  val unlockedPackIds: Set<String> = emptySet(),
  val packProgressMap: Map<String, Int> = emptyMap(),
  val requiredAdsPerPack: Int = 2,
  val isAllVipUnlocked: Boolean = false,
  val eliminatedGuessOptions: List<String> = emptyList(), // 50:50 Impostor Lifeline
  val isFiftyFiftyUsed: Boolean = false,
  val isPunishmentShielded: Boolean = false,
  val showVipUnlockModal: Boolean = false,
  val pendingVipPackId: String? = null,
  val pendingVipTitle: String = "",
  val pendingVipIcon: String = "",
  val pendingVipDesc: String = "",
  val showPerksHubModal: Boolean = false
)

class GameViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  private var timerJob: Job? = null
  private var handoffJob: Job? = null
  private var bombJob: Job? = null
  private var charadesJob: Job? = null

  init {
    viewModelScope.launch {
      combine(
        PerksManager.unlockedPacksState,
        PerksManager.packProgressState
      ) { packs, progressMap ->
        Pair(packs, progressMap)
      }.collect { (packs, progressMap) ->
        _uiState.update {
          it.copy(
            unlockedPackIds = packs,
            packProgressMap = progressMap
          )
        }
      }
    }
  }

  // ---------------- Rewarded Perks & VIP Management ----------------

  fun isPackUnlocked(packId: String, isVip: Boolean): Boolean {
    return PerksManager.isPackUnlocked(packId, isVip)
  }

  fun getPackAdProgress(packId: String): Int {
    return PerksManager.getPackAdProgress(packId)
  }

  fun recordAdWatchedForPack(packId: String): Pair<Int, Boolean> {
    val result = PerksManager.registerAdWatchedForPack(packId)
    val (newProgress, isUnlocked) = result
    if (isUnlocked) {
      _uiState.update { it.copy(showVipUnlockModal = false, pendingVipPackId = null) }
    }
    return result
  }

  fun promptVipUnlock(packId: String, title: String, icon: String, description: String) {
    _uiState.update {
      it.copy(
        showVipUnlockModal = true,
        pendingVipPackId = packId,
        pendingVipTitle = title,
        pendingVipIcon = icon,
        pendingVipDesc = description
      )
    }
  }

  fun promptVipUnlock(packId: String, title: String, categoryType: String = "") {
    val (icon, desc) = when (categoryType) {
      "bomb_topic" -> Pair("💣", "شاهد إعلانين لفتح هذا الموضوع الحصري في القنبلة الموقوتة بشكل دائم!")
      "tod_pack" -> Pair("🍾", "شاهد إعلانين لفتح هذه الباقة المميزة في صراحة أو جرأة بشكل دائم!")
      "charades_category" -> Pair("🎭", "شاهد إعلانين لفتح هذه الفئة الحصرية في تمثيل بدون كلام بشكل دائم!")
      else -> Pair("⭐", "شاهد إعلانين لفتح هذه الحزمة والمزايا الحصرية بشكل دائم!")
    }
    promptVipUnlock(packId, title, icon, desc)
  }

  fun dismissVipUnlockModal() {
    _uiState.update { it.copy(showVipUnlockModal = false, pendingVipPackId = null) }
  }

  fun dismissVipUnlock() = dismissVipUnlockModal()

  fun completeVipUnlock(packId: String) {
    PerksManager.unlockPackDirectly(packId)
    _uiState.update { it.copy(showVipUnlockModal = false, pendingVipPackId = null) }
  }

  fun completeVipUnlock() {
    _uiState.value.pendingVipPackId?.let { completeVipUnlock(it) }
  }

  fun setPerksHubModal(show: Boolean) {
    _uiState.update { it.copy(showPerksHubModal = show) }
  }

  fun toggleVipPerksHub(show: Boolean) = setPerksHubModal(show)

  fun applyFiftyFiftyHint() {
    val currentState = _uiState.value
    if (currentState.isFiftyFiftyUsed || currentState.impostorGuessOptions.size <= 2) return
    val secretWord = currentState.secretWord
    // Eliminate 2 incorrect options
    val wrongOptions = currentState.impostorGuessOptions.filter { it != secretWord }.shuffled()
    val toEliminate = wrongOptions.take(2)
    _uiState.update {
      it.copy(
        eliminatedGuessOptions = toEliminate,
        isFiftyFiftyUsed = true
      )
    }
  }

  fun applyPunishmentShield() {
    _uiState.update {
      it.copy(
        isPunishmentShielded = true,
        currentPunishment = "🎉 تم الإعفاء من العقاب بفضل درع المشاهدة! أنت محمي من أي حكم!"
      )
    }
  }

  fun shieldPunishment() = applyPunishmentShield()

  // ---------------- UI Phase & Mode Navigation ----------------

  fun selectGameMode(mode: GameMode) {
    _uiState.update { it.copy(selectedGameMode = mode) }
  }

  fun setPhase(phase: GamePhase) {
    if (_uiState.value.gamePhase == GamePhase.BOMB_GAME && phase != GamePhase.BOMB_GAME) {
      bombJob?.cancel()
      _uiState.update { it.copy(bombIsTicking = false) }
    }
    if (_uiState.value.gamePhase == GamePhase.CHARADES_GAME && phase != GamePhase.CHARADES_GAME) {
      charadesJob?.cancel()
      _uiState.update { it.copy(charadesIsTimerRunning = false) }
    }
    _uiState.update { it.copy(gamePhase = phase) }
  }

  fun showRules(show: Boolean) {
    _uiState.update { it.copy(showRulesModal = show) }
  }

  // ---------------- Player Setup ----------------

  fun addPlayer(name: String) {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return
    if (_uiState.value.players.size >= 10) return

    val newPlayer = Player(
      id = UUID.randomUUID().toString(),
      name = trimmed
    )
    _uiState.update { state ->
      state.copy(players = state.players + newPlayer)
    }
  }

  fun removePlayer(playerId: String) {
    _uiState.update { state ->
      state.copy(players = state.players.filterNot { it.id == playerId })
    }
  }

  // ---------------- Salfa Barra Flow ----------------

  fun selectCategoryAndStart(category: Category) {
    val players = _uiState.value.players
    if (players.size < 3) return

    val secretWord = category.words.random()
    val impostorIndex = players.indices.random()
    val starterIndex = players.indices.random()

    val updatedPlayers = players.mapIndexed { index, player ->
      player.copy(
        role = if (index == impostorIndex) "impostor" else "informed",
        votedFor = null
      )
    }

    _uiState.update { state ->
      state.copy(
        selectedCategory = category,
        secretWord = secretWord,
        impostorIndex = impostorIndex,
        starterPlayerIndex = starterIndex,
        currentAskerIndex = starterIndex,
        currentTargetIndex = if (players.size > 1) (starterIndex + 1) % players.size else 0,
        questionTurnCount = 1,
        players = updatedPlayers,
        gamePhase = GamePhase.PASS_AND_REVEAL,
        currentRevealIndex = 0,
        isHolding = false,
        hasRevealedCurrent = false,
        currentVoterIndex = 0,
        votes = emptyMap(),
        votesCountMap = emptyMap(),
        votedSuspect = null,
        impostorCaught = false,
        impostorGuessOptions = emptyList(),
        selectedGuess = null,
        impostorGuessedCorrectly = null,
        currentPunishment = GameMasterData.punishments.random(),
        eliminatedGuessOptions = emptyList(),
        isFiftyFiftyUsed = false,
        isPunishmentShielded = false
      )
    }
  }

  fun setHolding(holding: Boolean) {
    _uiState.update { state ->
      state.copy(
        isHolding = holding,
        hasRevealedCurrent = if (holding) true else state.hasRevealedCurrent
      )
    }
  }

  fun nextRevealPlayer() {
    val state = _uiState.value
    if (state.currentRevealIndex + 1 < state.players.size) {
      startHandoffCountdown()
    } else {
      setPhase(GamePhase.TIMER)
    }
  }

  private fun startHandoffCountdown() {
    handoffJob?.cancel()
    _uiState.update {
      it.copy(
        isHandoffCountdownActive = true,
        handoffCountdownSeconds = 5,
        isHolding = false
      )
    }
    handoffJob = viewModelScope.launch {
      for (second in 5 downTo 1) {
        _uiState.update { it.copy(handoffCountdownSeconds = second) }
        delay(1000)
      }
      _uiState.update { state ->
        state.copy(
          isHandoffCountdownActive = false,
          currentRevealIndex = state.currentRevealIndex + 1,
          isHolding = false,
          hasRevealedCurrent = false,
          handoffCountdownSeconds = 5
        )
      }
    }
  }

  fun skipHandoffCountdown() {
    handoffJob?.cancel()
    _uiState.update { state ->
      state.copy(
        isHandoffCountdownActive = false,
        currentRevealIndex = (state.currentRevealIndex + 1).coerceAtMost(state.players.size - 1),
        isHolding = false,
        hasRevealedCurrent = false,
        handoffCountdownSeconds = 5
      )
    }
  }

  fun nextHint() {
    _uiState.update { state ->
      val next = (state.currentHintIndex + 1) % GameMasterData.smartQuestionHints.size
      state.copy(currentHintIndex = next)
    }
  }

  fun nextQuestionTurn() {
    val state = _uiState.value
    val total = state.players.size
    if (total <= 1) return

    val newAsker = state.currentTargetIndex
    var newTarget = (newAsker + 1) % total
    if (newTarget == newAsker) {
      newTarget = (newTarget + 1) % total
    }

    val nextHint = (state.currentHintIndex + 1) % GameMasterData.smartQuestionHints.size

    _uiState.update {
      it.copy(
        currentAskerIndex = newAsker,
        currentTargetIndex = newTarget,
        questionTurnCount = it.questionTurnCount + 1,
        currentHintIndex = nextHint
      )
    }
  }

  fun randomizeQuestionPair() {
    val state = _uiState.value
    val total = state.players.size
    if (total <= 1) return

    val newAsker = (0 until total).random()
    var newTarget = (0 until total).filter { it != newAsker }.random()
    val nextHint = (state.currentHintIndex + 1) % GameMasterData.smartQuestionHints.size

    _uiState.update {
      it.copy(
        currentAskerIndex = newAsker,
        currentTargetIndex = newTarget,
        questionTurnCount = it.questionTurnCount + 1,
        currentHintIndex = nextHint
      )
    }
  }

  fun castVote(suspectedPlayer: Player) {
    val state = _uiState.value
    val currentVoter = state.players.getOrNull(state.currentVoterIndex) ?: return

    val updatedVotes = state.votes + (currentVoter.id to suspectedPlayer.id)

    if (state.currentVoterIndex + 1 < state.players.size) {
      _uiState.update {
        it.copy(
          currentVoterIndex = it.currentVoterIndex + 1,
          votes = updatedVotes
        )
      }
    } else {
      val counts = mutableMapOf<String, Int>()
      updatedVotes.values.forEach { suspectId ->
        counts[suspectId] = (counts[suspectId] ?: 0) + 1
      }

      val topSuspectId = counts.maxByOrNull { it.value }?.key
      val finalAccusedPlayer = state.players.find { it.id == topSuspectId } ?: suspectedPlayer
      val impostorPlayer = state.players.getOrNull(state.impostorIndex)
      val isCaught = finalAccusedPlayer.id == impostorPlayer?.id

      val categoryWords = state.selectedCategory?.words ?: emptyList()
      val otherWords = categoryWords.filterNot { it == state.secretWord }.shuffled().take(3)
      val options = (otherWords + state.secretWord).shuffled()

      _uiState.update {
        it.copy(
          votes = updatedVotes,
          votesCountMap = counts,
          votedSuspect = finalAccusedPlayer,
          impostorCaught = isCaught,
          impostorGuessOptions = options,
          selectedGuess = null,
          impostorGuessedCorrectly = null,
          currentPunishment = GameMasterData.punishments.random(),
          gamePhase = GamePhase.RESULT
        )
      }
    }
  }

  fun submitImpostorGuess(guess: String) {
    val state = _uiState.value
    val isCorrect = guess == state.secretWord
    _uiState.update {
      it.copy(
        selectedGuess = guess,
        impostorGuessedCorrectly = isCorrect,
        currentPunishment = if (!isCorrect) GameMasterData.punishments.random() else it.currentPunishment
      )
    }
  }

  fun rerollPunishment() {
    _uiState.update {
      it.copy(
        currentPunishment = GameMasterData.punishments.random(),
        isPunishmentShielded = false
      )
    }
  }

  fun playAgain() {
    handoffJob?.cancel()
    _uiState.update { state ->
      state.copy(
        gamePhase = GamePhase.CATEGORY_SELECTION,
        selectedCategory = null,
        secretWord = "",
        impostorIndex = -1,
        currentRevealIndex = 0,
        isHolding = false,
        hasRevealedCurrent = false,
        isHandoffCountdownActive = false,
        handoffCountdownSeconds = 5,
        currentAskerIndex = 0,
        currentTargetIndex = 1,
        questionTurnCount = 1,
        currentVoterIndex = 0,
        votes = emptyMap(),
        votesCountMap = emptyMap(),
        votedSuspect = null,
        impostorCaught = false,
        impostorGuessOptions = emptyList(),
        selectedGuess = null,
        impostorGuessedCorrectly = null
      )
    }
  }

  // ---------------- Word Bomb (القنبلة الموقوتة) Flow ----------------

  fun selectBombTopicAndStart(topic: BombTopic) {
    val players = _uiState.value.players
    if (players.isEmpty()) return

    // Calculate prompt challenge
    val challengeText = if (topic.id == "bt_random_letter") {
      "حرف: ${topic.examples.random()} 🔤"
    } else {
      "${topic.title} ${topic.icon}"
    }

    // Random fuse between 12 and 22 seconds for each turn
    val randomFuseSeconds = (12..22).random()
    val starterIndex = (0 until players.size).random()

    _uiState.update { state ->
      state.copy(
        selectedBombTopic = topic,
        currentBombChallengeText = challengeText,
        currentBombHolderIndex = starterIndex,
        bombPassCount = 0,
        bombTotalFuseSeconds = randomFuseSeconds,
        bombSecondsElapsed = 0,
        bombIsTicking = true,
        bombExploded = false,
        bombLoser = null,
        gamePhase = GamePhase.BOMB_GAME
      )
    }

    startBombTimer(randomFuseSeconds)
  }

  private fun startBombTimer(totalSeconds: Int) {
    bombJob?.cancel()
    bombJob = viewModelScope.launch {
      for (second in 1..totalSeconds) {
        delay(1000)
        _uiState.update { it.copy(bombSecondsElapsed = second) }
      }

      // Explosion! BOOM!
      val state = _uiState.value
      val loser = state.players.getOrNull(state.currentBombHolderIndex)
        ?: state.players.firstOrNull()

      _uiState.update {
        it.copy(
          bombIsTicking = false,
          bombExploded = true,
          bombLoser = loser,
          currentPunishment = GameMasterData.punishments.random(),
          isPunishmentShielded = false,
          gamePhase = GamePhase.BOMB_RESULT
        )
      }
    }
  }

  fun passBomb() {
    val state = _uiState.value
    if (!state.bombIsTicking || state.bombExploded || state.players.size <= 1) return

    val nextHolder = (state.currentBombHolderIndex + 1) % state.players.size
    // Fresh random suspense timer for each turn (between 10 and 22 seconds)
    val freshTurnFuseSeconds = (10..22).random()

    _uiState.update {
      it.copy(
        currentBombHolderIndex = nextHolder,
        bombPassCount = it.bombPassCount + 1,
        bombTotalFuseSeconds = freshTurnFuseSeconds,
        bombSecondsElapsed = 0
      )
    }

    startBombTimer(freshTurnFuseSeconds)
  }

  fun rerollBombChallenge() {
    val state = _uiState.value
    val topic = state.selectedBombTopic ?: return
    val newChallengeText = if (topic.id == "bt_random_letter") {
      "حرف: ${topic.examples.random()} 🔤"
    } else {
      "${topic.title} ${topic.icon}"
    }
    _uiState.update { it.copy(currentBombChallengeText = newChallengeText) }
  }

  fun restartBombRound() {
    val topic = _uiState.value.selectedBombTopic ?: GameMasterData.bombTopics.first()
    selectBombTopicAndStart(topic)
  }

  // ---------------- Truth or Dare (صراحة أو جرأة / القارورة الدوارة) Flow ----------------

  fun selectTodPack(pack: TodPack) {
    _uiState.update { it.copy(selectedTodPack = pack) }
  }

  fun spinBottle() {
    val state = _uiState.value
    if (state.isBottleSpinning || state.players.isEmpty()) return

    val totalPlayers = state.players.size
    val targetIndex = (0 until totalPlayers).random()
    val chosenPlayer = state.players[targetIndex]

    // Calculate angle for target player with 4 to 6 full rotations landing exactly on target
    val currentDegrees = state.bottleRotationDegrees
    val currentModulo = (currentDegrees % 360f + 360f) % 360f
    val segmentAngle = 360f / totalPlayers
    val targetModulo = targetIndex * segmentAngle
    val fullSpins = (4..6).random() * 360f

    val forwardDelta = if (targetModulo >= currentModulo) {
      targetModulo - currentModulo
    } else {
      360f - currentModulo + targetModulo
    }
    val totalNewRotation = currentDegrees + fullSpins + forwardDelta

    _uiState.update {
      it.copy(
        isBottleSpinning = true,
        selectedTodPlayer = null,
        todChoice = null,
        currentTodPromptText = "",
        bottleRotationDegrees = totalNewRotation
      )
    }

    viewModelScope.launch {
      // 3.5 seconds spin animation
      delay(3500)
      _uiState.update {
        it.copy(
          isBottleSpinning = false,
          selectedTodPlayer = chosenPlayer,
          todRoundsPlayed = it.todRoundsPlayed + 1
        )
      }
    }
  }

  fun chooseTruthOrDare(choice: TodChoiceType) {
    val state = _uiState.value
    val pack = state.selectedTodPack
    val prompt = if (choice == TodChoiceType.TRUTH) {
      pack.truths.randomOrNull() ?: "ما هو أكبر سر تخفيه عن أصدقائك؟"
    } else {
      pack.dares.randomOrNull() ?: "قم بتقليد صوت شخصية كرتونية شهيرة لمدة دقيقة كاملة!"
    }

    _uiState.update {
      it.copy(
        todChoice = choice,
        currentTodPromptText = prompt,
        gamePhase = GamePhase.TOD_PROMPT
      )
    }
  }

  fun rerollTodPrompt() {
    val state = _uiState.value
    val pack = state.selectedTodPack
    val choice = state.todChoice ?: TodChoiceType.TRUTH
    val newPrompt = if (choice == TodChoiceType.TRUTH) {
      pack.truths.randomOrNull() ?: "ما هو أكثر موقف محرج تعرضت له مؤخراً؟"
    } else {
      pack.dares.randomOrNull() ?: "أرسل رسالة عشوائية لأول شخص في قائمة محادثاتك!"
    }
    _uiState.update { it.copy(currentTodPromptText = newPrompt) }
  }

  fun nextBottleSpin() {
    _uiState.update {
      it.copy(
        gamePhase = GamePhase.TOD_SPIN,
        todChoice = null,
        currentTodPromptText = ""
      )
    }
  }

  // ---------------- Charades (تمثيل بدون كلام) Game Engine ----------------

  fun selectCharadesCategory(category: CharadesCategory) {
    _uiState.update { it.copy(selectedCharadesCategory = category) }
  }

  fun setCharadesDuration(seconds: Int) {
    _uiState.update { 
      it.copy(
        charadesDurationSeconds = seconds,
        charadesTimerSecondsRemaining = seconds
      ) 
    }
  }

  fun setCharadesTeamMode(isTeamMode: Boolean) {
    _uiState.update { it.copy(charadesIsTeamMode = isTeamMode) }
  }

  fun setCharadesTeamNames(teamAName: String, teamBName: String) {
    _uiState.update { state ->
      state.copy(
        charadesTeamA = state.charadesTeamA.copy(name = teamAName.ifBlank { "فريق النسور 🦅" }),
        charadesTeamB = state.charadesTeamB.copy(name = teamBName.ifBlank { "فريق الذئاب 🐺" })
      )
    }
  }

  fun startCharadesGame(
    category: CharadesCategory = _uiState.value.selectedCharadesCategory,
    durationSeconds: Int = _uiState.value.charadesDurationSeconds,
    isTeamMode: Boolean = _uiState.value.charadesIsTeamMode
  ) {
    charadesJob?.cancel()
    val initialWord = category.items.randomOrNull() ?: "تايتانيك"
    _uiState.update { state ->
      state.copy(
        gamePhase = GamePhase.CHARADES_GAME,
        selectedCharadesCategory = category,
        charadesDurationSeconds = durationSeconds,
        charadesTimerSecondsRemaining = durationSeconds,
        charadesIsTeamMode = isTeamMode,
        charadesCurrentWord = initialWord,
        charadesWordsGuessedThisTurn = 0,
        charadesGuessedWordsList = emptyList(),
        charadesIsTimerRunning = false,
        charadesIsWordRevealed = false,
        charadesCurrentTeamTurnIndex = 0,
        charadesCurrentActorPlayerIndex = 0
      )
    }
  }

  fun startCharadesTurnTimer() {
    if (_uiState.value.charadesIsTimerRunning) return
    charadesJob?.cancel()
    _uiState.update { it.copy(charadesIsTimerRunning = true, charadesIsWordRevealed = true) }

    charadesJob = viewModelScope.launch {
      while (_uiState.value.charadesTimerSecondsRemaining > 0 && _uiState.value.charadesIsTimerRunning) {
        delay(1000L)
        val remaining = _uiState.value.charadesTimerSecondsRemaining - 1
        _uiState.update { it.copy(charadesTimerSecondsRemaining = remaining) }
        if (remaining <= 0) {
          endCharadesTurn()
          break
        }
      }
    }
  }

  fun pauseCharadesTurnTimer() {
    charadesJob?.cancel()
    _uiState.update { it.copy(charadesIsTimerRunning = false) }
  }

  fun toggleCharadesWordReveal() {
    _uiState.update { it.copy(charadesIsWordRevealed = !it.charadesIsWordRevealed) }
  }

  fun charadesCorrectGuess() {
    val state = _uiState.value
    val currentWord = state.charadesCurrentWord
    val updatedGuessedList = state.charadesGuessedWordsList + currentWord
    val updatedCount = state.charadesWordsGuessedThisTurn + 1

    // Update score
    val updatedTeamA = if (state.charadesCurrentTeamTurnIndex == 0) {
      state.charadesTeamA.copy(score = state.charadesTeamA.score + 1)
    } else state.charadesTeamA

    val updatedTeamB = if (state.charadesCurrentTeamTurnIndex == 1) {
      state.charadesTeamB.copy(score = state.charadesTeamB.score + 1)
    } else state.charadesTeamB

    // Pick next word from category that hasn't been guessed this turn
    val availableWords = state.selectedCharadesCategory.items.filterNot { it in updatedGuessedList }
    val nextWord = (if (availableWords.isNotEmpty()) availableWords else state.selectedCharadesCategory.items).random()

    _uiState.update {
      it.copy(
        charadesCurrentWord = nextWord,
        charadesWordsGuessedThisTurn = updatedCount,
        charadesGuessedWordsList = updatedGuessedList,
        charadesTeamA = updatedTeamA,
        charadesTeamB = updatedTeamB
      )
    }
  }

  fun charadesSkipWord() {
    val state = _uiState.value
    val availableWords = state.selectedCharadesCategory.items.filter { it != state.charadesCurrentWord }
    val nextWord = (if (availableWords.isNotEmpty()) availableWords else state.selectedCharadesCategory.items).random()
    _uiState.update { it.copy(charadesCurrentWord = nextWord) }
  }

  fun endCharadesTurn() {
    charadesJob?.cancel()
    _uiState.update {
      it.copy(
        gamePhase = GamePhase.CHARADES_ROUND_RESULT,
        charadesIsTimerRunning = false,
        charadesRoundsPlayed = it.charadesRoundsPlayed + 1
      )
    }
  }

  fun nextCharadesTurn() {
    charadesJob?.cancel()
    val state = _uiState.value
    val nextTeamTurn = (state.charadesCurrentTeamTurnIndex + 1) % 2
    val nextActorIndex = if (state.players.isNotEmpty()) {
      (state.charadesCurrentActorPlayerIndex + 1) % state.players.size
    } else 0

    val nextWord = state.selectedCharadesCategory.items.randomOrNull() ?: "الناظر"

    _uiState.update {
      it.copy(
        gamePhase = GamePhase.CHARADES_GAME,
        charadesCurrentTeamTurnIndex = nextTeamTurn,
        charadesCurrentActorPlayerIndex = nextActorIndex,
        charadesCurrentWord = nextWord,
        charadesWordsGuessedThisTurn = 0,
        charadesGuessedWordsList = emptyList(),
        charadesTimerSecondsRemaining = state.charadesDurationSeconds,
        charadesIsTimerRunning = false,
        charadesIsWordRevealed = false
      )
    }
  }

  fun resetCharadesScores() {
    _uiState.update {
      it.copy(
        charadesTeamA = it.charadesTeamA.copy(score = 0),
        charadesTeamB = it.charadesTeamB.copy(score = 0),
        charadesRoundsPlayed = 0
      )
    }
  }

  fun resetToHome() {
    handoffJob?.cancel()
    bombJob?.cancel()
    charadesJob?.cancel()
    _uiState.update { state ->
      state.copy(
        gamePhase = GamePhase.HOME,
        selectedCategory = null,
        secretWord = "",
        impostorIndex = -1,
        currentRevealIndex = 0,
        isHolding = false,
        hasRevealedCurrent = false,
        isHandoffCountdownActive = false,
        handoffCountdownSeconds = 5,
        currentAskerIndex = 0,
        currentTargetIndex = 1,
        questionTurnCount = 1,
        currentVoterIndex = 0,
        votes = emptyMap(),
        votesCountMap = emptyMap(),
        votedSuspect = null,
        impostorCaught = false,
        impostorGuessOptions = emptyList(),
        selectedGuess = null,
        impostorGuessedCorrectly = null,
        bombIsTicking = false,
        bombExploded = false,
        charadesIsTimerRunning = false
      )
    }
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
    handoffJob?.cancel()
    bombJob?.cancel()
    charadesJob?.cancel()
  }
}
