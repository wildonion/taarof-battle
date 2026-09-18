package com.wildonion.taarofbattle.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

enum class Screen { Menu, Battle, Result }
enum class Mode { TwoPlayers, VsBot }

data class PowerUp(val id: String, val title: String, val emoji: String)

val POWER_UPS = listOf(
    PowerUp("ghasam", "قسم!", "\uD83D\uDE4F"),
    PowerUp("waiter", "گارسون!", "\uD83D\uDECE"),
    PowerUp("wallet", "کیف نیست", "\uD83D\uDC5B")
)

data class GameUiState(
    val screen: Screen = Screen.Menu,
    val player1Name: String = "علی",
    val player2Name: String = "رضا",
    val mode: Mode = Mode.TwoPlayers,
    val durationSec: Int = 10,
    val billInput: String = "",
    val countdown: Int = 3,
    val timeLeftMs: Long = 10_000L,
    val overtime: Boolean = false,
    val p1Score: Int = 0,
    val p2Score: Int = 0,
    val p1Combo: Int = 0,
    val p2Combo: Int = 0,
    val p1Target: Pair<Float, Float> = 0.5f to 0.5f,
    val p2Target: Pair<Float, Float> = 0.5f to 0.5f,
    val p1MultiplierUntil: Long = 0L,
    val p2MultiplierUntil: Long = 0L,
    val p1FrozenUntil: Long = 0L,
    val p2FrozenUntil: Long = 0L,
    val p1Used: Set<String> = emptySet(),
    val p2Used: Set<String> = emptySet(),
    val winnerName: String = "",
    val loserName: String = "",
    val billAmount: String = "",
    val funnyLine: String = ""
)

class GameViewModel : ViewModel() {
    private val _ui = MutableStateFlow(GameUiState())
    val ui: StateFlow<GameUiState> = _ui

    private var ticker: Job? = null
    private var botJob: Job? = null
    private var targetJob: Job? = null

    private val funnyWinnerLines = listOf(
        "استاد تعارف! بدون رحم.",
        "قربونت برم، ولی تو حساب کن!",
        "چه مهمون‌نوازی... از جیب بازنده!",
        "برنده شدی! بازنده لبخند بزن و پرداخت کن."
    )

    fun setP1(name: String) { _ui.value = _ui.value.copy(player1Name = name.take(12)) }
    fun setP2(name: String) { _ui.value = _ui.value.copy(player2Name = name.take(12)) }
    fun setMode(m: Mode) { _ui.value = _ui.value.copy(mode = m) }
    fun setDuration(s: Int) { _ui.value = _ui.value.copy(durationSec = s) }
    fun setBill(b: String) { _ui.value = _ui.value.copy(billInput = b.filter { it.isDigit() }.take(9)) }

    fun goMenu() {
        ticker?.cancel(); botJob?.cancel(); targetJob?.cancel()
        _ui.value = _ui.value.copy(screen = Screen.Menu, overtime = false)
    }

    fun startBattle() {
        ticker?.cancel(); botJob?.cancel(); targetJob?.cancel()
        val s = _ui.value
        _ui.value = s.copy(
            screen = Screen.Battle,
            countdown = 3,
            timeLeftMs = s.durationSec * 1000L,
            overtime = false,
            p1Score = 0, p2Score = 0,
            p1Combo = 0, p2Combo = 0,
            p1Target = randomTarget(), p2Target = randomTarget(),
            p1MultiplierUntil = 0, p2MultiplierUntil = 0,
            p1FrozenUntil = 0, p2FrozenUntil = 0,
            p1Used = emptySet(), p2Used = emptySet()
        )
        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _ui.value = _ui.value.copy(countdown = i)
                delay(700)
            }
            _ui.value = _ui.value.copy(countdown = 0)
            startTicker()
            startTargetMover()
            if (_ui.value.mode == Mode.VsBot) startBot()
        }
    }

    private fun randomTarget(): Pair<Float, Float> =
        (0.2f + Random.nextFloat() * 0.6f) to (0.25f + Random.nextFloat() * 0.5f)

    fun moveTargets() {
        val s = _ui.value
        if (s.screen != Screen.Battle || s.countdown != 0) return
        _ui.value = s.copy(p1Target = randomTarget(), p2Target = randomTarget())
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (_ui.value.timeLeftMs > 0) {
                delay(100)
                val left = max(0L, _ui.value.timeLeftMs - 100)
                _ui.value = _ui.value.copy(timeLeftMs = left)
                if (left == 0L) { onTimeUp(); break }
            }
        }
    }

    private fun startTargetMover() {
        targetJob?.cancel()
        targetJob = viewModelScope.launch {
            while (_ui.value.screen == Screen.Battle) {
                delay(1100)
                moveTargets()
            }
        }
    }

    private fun startBot() {
        botJob?.cancel()
        botJob = viewModelScope.launch {
            while (_ui.value.screen == Screen.Battle) {
                delay(Random.nextLong(280, 650))
                val s = _ui.value
                if (s.countdown != 0) continue
                if (s.overtime) {
                    // golden tap: bot has 50/50 to grab it instantly — tense!
                    if (Random.nextBoolean()) tapP2(true)
                    continue
                }
                if (s.timeLeftMs <= 0) break
                tapP2(Random.nextFloat() > 0.15f)
            }
        }
    }

    /** hit=true means the moving target was tapped. Miss resets combo. */
    fun tapP1(hit: Boolean) {
        val now = System.currentTimeMillis()
        val s = _ui.value
        if (s.screen != Screen.Battle || s.countdown != 0) return
        if (now < s.p1FrozenUntil) return
        if (s.overtime) {
            if (hit) finishBattle(winnerIsP1 = true, golden = true)
            return
        }
        if (!hit) {
            _ui.value = s.copy(p1Combo = 0)
            return
        }
        val mult = if (now < s.p1MultiplierUntil) 2 else 1
        val combo = s.p1Combo + 1
        var gain = mult + if (combo >= 10) 1 else 0
        if (combo % 15 == 0) gain += 4 // combo milestone bonus
        _ui.value = s.copy(
            p1Score = s.p1Score + gain,
            p1Combo = combo,
            p1Target = randomTarget()
        )
    }

    fun tapP2(hit: Boolean) {
        val now = System.currentTimeMillis()
        val s = _ui.value
        if (s.screen != Screen.Battle || s.countdown != 0) return
        if (s.mode != Mode.VsBot && now < s.p2FrozenUntil) return
        if (s.overtime) {
            if (hit) finishBattle(winnerIsP1 = false, golden = true)
            return
        }
        if (!hit) {
            _ui.value = _ui.value.copy(p2Combo = 0)
            return
        }
        val mult = if (now < s.p2MultiplierUntil) 2 else 1
        val combo = s.p2Combo + 1
        var gain = mult + if (combo >= 10) 1 else 0
        if (combo % 15 == 0) gain += 4
        _ui.value = s.copy(
            p2Score = s.p2Score + gain,
            p2Combo = combo,
            p2Target = randomTarget()
        )
    }

    fun usePowerUp(player: Int, id: String) {
        val now = System.currentTimeMillis()
        val s = _ui.value
        if (s.screen != Screen.Battle || s.countdown != 0) return
        // one use per power-up per round — no spamming
        if (player == 1 && s.p1Used.contains(id)) return
        if (player == 2 && s.p2Used.contains(id)) return
        when (id) {
            "ghasam" -> if (player == 1)
                _ui.value = s.copy(p1MultiplierUntil = now + 2000, p1Used = s.p1Used + id)
            else
                _ui.value = s.copy(p2MultiplierUntil = now + 2000, p2Used = s.p2Used + id)
            "waiter" -> if (player == 1)
                _ui.value = s.copy(p2FrozenUntil = now + 1500, p1Used = s.p1Used + id)
            else
                _ui.value = s.copy(p1FrozenUntil = now + 1500, p2Used = s.p2Used + id)
            "wallet" -> if (player == 1)
                _ui.value = s.copy(p1Score = s.p1Score + 5, p1Used = s.p1Used + id)
            else
                _ui.value = s.copy(p2Score = s.p2Score + 5, p2Used = s.p2Used + id)
        }
    }

    private fun onTimeUp() {
        val s = _ui.value
        when {
            s.p1Score > s.p2Score -> finishBattle(winnerIsP1 = true, golden = false)
            s.p2Score > s.p1Score -> finishBattle(winnerIsP1 = false, golden = false)
            else -> {
                // TIE → golden tap sudden death: next target hit wins
                _ui.value = s.copy(overtime = true)
            }
        }
    }

    private fun finishBattle(winnerIsP1: Boolean, golden: Boolean) {
        ticker?.cancel(); botJob?.cancel(); targetJob?.cancel()
        val s = _ui.value
        val winner = if (winnerIsP1) s.player1Name.ifBlank { "بازیکن ۱" } else s.player2Name.ifBlank { "بازیکن ۲" }
        val loser = if (winnerIsP1) s.player2Name.ifBlank { "بازیکن ۲" } else s.player1Name.ifBlank { "بازیکن ۱" }
        // only show the bill the users typed in the menu — never a made-up number
        val line = if (golden) "⚡ ضربه طلایی! مساوی شد و با یک ضربه سرنوشت مشخص شد."
        else funnyWinnerLines.random()
        _ui.value = s.copy(
            screen = Screen.Result,
            overtime = false,
            winnerName = winner,
            loserName = loser,
            billAmount = s.billInput,
            funnyLine = line
        )
    }
}
