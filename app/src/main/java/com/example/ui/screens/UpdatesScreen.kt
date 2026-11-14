package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.checkers.CheckersBoard
import com.example.domain.checkers.Piece
import com.example.domain.checkers.PieceColor
import com.example.domain.checkers.RussianCheckersEngine
import com.example.domain.curriculum.ContentPackParser
import com.example.domain.curriculum.PuzzleDifficulty
import com.example.ui.components.CheckersBoardView
import com.example.ui.components.MascotSpeechCard
import com.example.ui.theme.CrownGold
import com.example.ui.theme.ForestEmerald
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TerracottaOrange
import com.example.ui.viewmodel.UpdateActionStatus
import com.example.ui.viewmodel.UpdatesViewModel

@Composable
fun UpdatesScreen(
  viewModel: UpdatesViewModel,
  modifier: Modifier = Modifier,
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  @Suppress("DEPRECATION")
  val clipboardManager = LocalClipboardManager.current

  var manifestUrlInput by remember(state.manifestUrl) {
    mutableStateOf(state.manifestUrl)
  }
  var showPasteJsonDialog by remember { mutableStateOf(false) }
  var showPuzzleBuilderDialog by remember { mutableStateOf(false) }
  var clipboardBanner by remember { mutableStateOf<String?>(null) }

  val openDocumentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument(),
  ) { uri ->
    if (uri != null) {
      val jsonContent = runCatching {
        context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
      }.getOrNull()
      if (jsonContent != null) {
        viewModel.importFromRawJson(jsonContent)
      }
    }
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 700.dp)
        .fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Обновления и новые задачи",
              style = MaterialTheme.typography.headlineMedium,
            )
            Text(
              text = "Версия программы: v${state.currentVersionName} (сборка ${state.currentVersionCode})",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer,
          ) {
            Icon(
              imageVector = Icons.Filled.SystemUpdate,
              contentDescription = "Центр обновлений",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(10.dp).size(24.dp),
            )
          }
        }
      }

      item {
        MascotSpeechCard(
          title = "Как обновлять приложение",
          message = "Загружай свежие задачи и уроки из JSON-файла или по HTTPS-ссылке прямо сейчас без переустановки, придумывай свои задания на доске или проверяй выход новой версии APK!",
        )
      }

      // Status Feedback Card
      item {
        AnimatedVisibility(
          visible = state.status !is UpdateActionStatus.Idle || clipboardBanner != null,
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            clipboardBanner?.let { msg ->
              StatusCard(
                message = msg,
                isError = false,
                isLoading = false,
                onDismiss = { clipboardBanner = null },
              )
            }
            when (val status = state.status) {
              UpdateActionStatus.Idle -> Unit
              UpdateActionStatus.Loading -> StatusCard(
                message = "Проверяем и загружаем пакет...",
                isError = false,
                isLoading = true,
                onDismiss = null,
              )
              is UpdateActionStatus.Success -> StatusCard(
                message = status.message,
                isError = false,
                isLoading = false,
                onDismiss = { viewModel.clearStatus() },
              )
              is UpdateActionStatus.Error -> StatusCard(
                message = status.message,
                isError = true,
                isLoading = false,
                onDismiss = { viewModel.clearStatus() },
              )
            }
          }
        }
      }

      // Bonus Section: One-Tap Tournament & Sports Rank Expansion Pack + Checklist
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
          ),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = CrownGold,
              )
              Text(
                text = "Бонус: Подготовка к разряду и соревнованиям",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
            }
            Text(
              text = "В базовый курс уже добавлена «Школа разрядника» (уроки 9–12 и задачи 9–12). Нажмите кнопку ниже, чтобы одним касанием подгрузить дополнительный турнирный выпуск («Городская партия» + комбинация «Решето»)!",
              style = MaterialTheme.typography.bodyMedium,
            )
            Button(
              onClick = { viewModel.loadTournamentBonusPack() },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("load_tournament_bonus_button"),
              shape = RoundedCornerShape(14.dp),
            ) {
              Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Загрузить турнирный пакет (Выпуск 2)")
            }
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Text(
                  text = "Памятка юного разрядника на турнире:",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold,
                )
                Text("• Правило «Тронул — ходи»: сначала рассчитай ход глазами, потом бери шашку.", style = MaterialTheme.typography.bodySmall)
                Text("• Выбор боя: бить обязательно, но из двух разных взятий можно выбрать самое выгодное.", style = MaterialTheme.typography.bodySmall)
                Text("• Нотация: тихий ход пишется через тире (c3–d4), а бой — через двоеточие (b2:d4:f6).", style = MaterialTheme.typography.bodySmall)
                Text("• Часы: тренируйся играть партии с контролем по 10 минут.", style = MaterialTheme.typography.bodySmall)
              }
            }
          }
        }
      }

      // Section 1: App Version & HTTPS Manifest Check
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.CloudDownload,
                contentDescription = null,
                tint = SkyBlue,
              )
              Text(
                text = "1. Проверка обновлений по ссылке",
                style = MaterialTheme.typography.titleMedium,
              )
            }
            Text(
              text = "Укажите HTTPS-ссылку на JSON-манифест (например, на GitHub Pages или личном сервере), чтобы загрузить новые задачи или скачать новую версию программы.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
              value = manifestUrlInput,
              onValueChange = { manifestUrlInput = it },
              label = { Text("HTTPS-ссылка на updates.json") },
              placeholder = { Text("https://example.com/shashki/updates.json") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("manifest_url_input"),
            )
            Button(
              onClick = { viewModel.checkUrlUpdate(manifestUrlInput) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("check_url_update_button"),
              shape = RoundedCornerShape(14.dp),
            ) {
              Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Проверить и загрузить обновление")
            }

            state.availableRelease?.let { release ->
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                ),
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Text(
                    text = "Доступна новая версия v${release.versionName} (сборка ${release.versionCode})",
                    style = MaterialTheme.typography.titleSmall,
                    color = ForestEmerald,
                    fontWeight = FontWeight.Bold,
                  )
                  Text(
                    text = release.changelog,
                    style = MaterialTheme.typography.bodyMedium,
                  )
                  val safeHttp = release.downloadUrl.startsWith("https://") ||
                    release.downloadUrl.startsWith("http://")
                  if (safeHttp) {
                    Button(
                      onClick = {
                        runCatching {
                          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(release.downloadUrl))
                          context.startActivity(intent)
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_apk_download_browser"),
                    ) {
                      Icon(imageVector = Icons.Filled.OpenInBrowser, contentDescription = null)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Открыть загрузку в браузере")
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Section 2: Import JSON pack from File or Clipboard
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.FileOpen,
                contentDescription = null,
                tint = TerracottaOrange,
              )
              Text(
                text = "2. Загрузка пакета задач и уроков (.json)",
                style = MaterialTheme.typography.titleMedium,
              )
            }
            if (state.lastImportedPackName.isNotBlank()) {
              Text(
                text = "Последний пакет: ${state.lastImportedPackName}",
                style = MaterialTheme.typography.labelLarge,
                color = ForestEmerald,
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Button(
                onClick = {
                  openDocumentLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                },
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("pick_json_file_button"),
                shape = RoundedCornerShape(14.dp),
              ) {
                Icon(imageVector = Icons.Filled.FileOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Выбрать файл")
              }
              OutlinedButton(
                onClick = { showPasteJsonDialog = true },
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("paste_json_button"),
                shape = RoundedCornerShape(14.dp),
              ) {
                Icon(imageVector = Icons.Filled.Code, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Вставить JSON")
              }
            }
            TextButton(
              onClick = {
                clipboardManager.setText(AnnotatedString(ContentPackParser.sampleTemplateJson()))
                clipboardBanner = "Образец JSON-пакета скопирован в буфер обмена!"
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("copy_json_template_button"),
            ) {
              Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Скопировать пример JSON-шаблона")
            }
          }
        }
      }

      // Section 3: Interactive Custom Puzzle Builder
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
          ),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                tint = ForestEmerald,
              )
              Text(
                text = "3. Конструктор своей задачи",
                style = MaterialTheme.typography.titleMedium,
              )
            }
            Text(
              text = "Расставьте шашки прямо на доске, выберите правильный ход и добавьте собственное задание для ребёнка!",
              style = MaterialTheme.typography.bodyMedium,
            )
            Button(
              onClick = { showPuzzleBuilderDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = ForestEmerald),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("open_custom_puzzle_builder"),
              shape = RoundedCornerShape(14.dp),
            ) {
              Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Создать свою задачу на доске")
            }
          }
        }
      }

      // Section 4: Added Custom Materials
      if (state.customPuzzles.isNotEmpty() || state.customLessons.isNotEmpty()) {
        item {
          Text(
            text = "Добавленные материалы (${state.customPuzzles.size + state.customLessons.size})",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 4.dp),
          )
        }

        items(state.customPuzzles, key = { "cp_${it.id}" }) { puzzle ->
          CustomItemRow(
            badgeText = "Задача • ${puzzle.difficulty.label}",
            title = puzzle.title,
            subtitle = puzzle.prompt,
            onDelete = { viewModel.deleteCustomPuzzle(puzzle.id) },
            testTag = "delete_custom_puzzle_${puzzle.id}",
          )
        }

        items(state.customLessons, key = { "cl_${it.id}" }) { lesson ->
          CustomItemRow(
            badgeText = "Урок • +${lesson.starsReward} ⭐",
            title = lesson.title,
            subtitle = lesson.subtitle,
            onDelete = { viewModel.deleteCustomLesson(lesson.id) },
            testTag = "delete_custom_lesson_${lesson.id}",
          )
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }

  if (showPasteJsonDialog) {
    PasteJsonDialog(
      onDismiss = { showPasteJsonDialog = false },
      onImport = { raw ->
        showPasteJsonDialog = false
        viewModel.importFromRawJson(raw)
      },
    )
  }

  if (showPuzzleBuilderDialog) {
    CustomPuzzleBuilderDialog(
      onDismiss = { showPuzzleBuilderDialog = false },
      onCreate = { title, theme, difficulty, prompt, board, notation, hint ->
        showPuzzleBuilderDialog = false
        viewModel.createQuickCustomPuzzle(
          title = title,
          themeTag = theme,
          difficulty = difficulty,
          prompt = prompt,
          board = board,
          solutionNotation = notation,
          hintText = hint,
        )
      },
    )
  }
}

@Composable
private fun StatusCard(
  message: String,
  isError: Boolean,
  isLoading: Boolean,
  onDismiss: (() -> Unit)?,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = when {
        isError -> MaterialTheme.colorScheme.errorContainer
        isLoading -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
      },
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("updates_status_card")
      .then(if (onDismiss != null) Modifier.clickable(onClick = onDismiss) else Modifier),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
      } else {
        Icon(
          imageVector = if (isError) Icons.Filled.ErrorOutline else Icons.Filled.CheckCircle,
          contentDescription = null,
          tint = if (isError) MaterialTheme.colorScheme.error else ForestEmerald,
        )
      }
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun CustomItemRow(
  badgeText: String,
  title: String,
  subtitle: String,
  onDelete: () -> Unit,
  testTag: String,
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = badgeText,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
        )
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
        )
      }
      IconButton(
        onClick = onDelete,
        modifier = Modifier.testTag(testTag),
      ) {
        Icon(
          imageVector = Icons.Filled.Delete,
          contentDescription = "Удалить",
          tint = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}

@Composable
private fun PasteJsonDialog(
  onDismiss: () -> Unit,
  onImport: (String) -> Unit,
) {
  var jsonText by remember { mutableStateOf(ContentPackParser.sampleTemplateJson()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Вставить JSON-пакет") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Отредактируйте или вставьте JSON с новыми задачами, уроками или манифестом версии.",
          style = MaterialTheme.typography.bodyMedium,
        )
        OutlinedTextField(
          value = jsonText,
          onValueChange = { jsonText = it },
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .testTag("paste_json_textfield"),
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onImport(jsonText) },
        modifier = Modifier.testTag("confirm_paste_json_button"),
      ) {
        Text("Загрузить")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Отмена")
      }
    },
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CustomPuzzleBuilderDialog(
  onDismiss: () -> Unit,
  onCreate: (
    title: String,
    themeTag: String,
    difficulty: PuzzleDifficulty,
    prompt: String,
    board: CheckersBoard,
    solutionNotation: String,
    hintText: String,
  ) -> Unit,
) {
  var title by remember { mutableStateOf("Новая задача") }
  var themeTag by remember { mutableStateOf("Тактика") }
  var difficulty by remember { mutableStateOf(PuzzleDifficulty.EASY) }
  var prompt by remember { mutableStateOf("Найди лучший ход белых!") }
  var hintText by remember { mutableStateOf("") }
  var board by remember {
    mutableStateOf(
      CheckersBoard.fromStrings(
        listOf(
          "........",
          "........",
          "........",
          "..b.....",
          "........",
          "..b.....",
          ".w......",
          "........",
        )
      )
    )
  }

  val legalMoves = remember(board) {
    RussianCheckersEngine.getLegalMoves(board, PieceColor.WHITE)
  }
  var solutionNotation by remember { mutableStateOf("b2:d4:b6") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.background,
      modifier = Modifier
        .padding(16.dp)
        .widthIn(max = 560.dp)
        .fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
          text = "Конструктор задачи",
          style = MaterialTheme.typography.headlineSmall,
        )
        Text(
          text = "Нажимай на тёмные клетки доски: Пусто → Белая → Белая дамка → Чёрная → Чёрная дамка.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        CheckersBoardView(
          board = board,
          interactive = true,
          onSquareClick = { sq ->
            val nextPiece = when (board[sq]) {
              null -> Piece(PieceColor.WHITE, isKing = false)
              Piece(PieceColor.WHITE, isKing = false) -> Piece(PieceColor.WHITE, isKing = true)
              Piece(PieceColor.WHITE, isKing = true) -> Piece(PieceColor.BLACK, isKing = false)
              Piece(PieceColor.BLACK, isKing = false) -> Piece(PieceColor.BLACK, isKing = true)
              else -> null
            }
            board = board.withPiece(sq, nextPiece)
          },
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          OutlinedButton(
            onClick = { board = CheckersBoard() },
            modifier = Modifier.weight(1f).height(48.dp),
          ) {
            Text("Очистить доску")
          }
          OutlinedButton(
            onClick = {
              board = CheckersBoard.fromStrings(
                listOf(
                  "........",
                  "........",
                  "........",
                  "..b.....",
                  "........",
                  "..b.....",
                  ".w......",
                  "........",
                )
              )
              solutionNotation = "b2:d4:b6"
            },
            modifier = Modifier.weight(1f).height(48.dp),
          ) {
            Text("Пример")
          }
        }

        if (legalMoves.isNotEmpty()) {
          Text(
            text = "Доступные ходы белых (нажмите для выбора):",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            legalMoves.take(8).forEach { move ->
              AssistChip(
                onClick = { solutionNotation = move.notationString },
                label = { Text(move.notationString) },
              )
            }
          }
        } else {
          Text(
            text = "Поставьте хотя бы одну белую шашку, чтобы появились доступные ходы.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
          )
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Название задачи") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("builder_title_input"),
        )

        OutlinedTextField(
          value = themeTag,
          onValueChange = { themeTag = it },
          label = { Text("Тема (например: Взятие, Дамка)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          PuzzleDifficulty.entries.forEach { diff ->
            FilterChip(
              selected = difficulty == diff,
              onClick = { difficulty = diff },
              label = { Text(diff.label) },
            )
          }
        }

        OutlinedTextField(
          value = prompt,
          onValueChange = { prompt = it },
          label = { Text("Условие для ребёнка") },
          modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
          value = solutionNotation,
          onValueChange = { solutionNotation = it },
          label = { Text("Правильный ход (например: c3-d4 или b2:d4:b6)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("builder_solution_input"),
        )

        OutlinedTextField(
          value = hintText,
          onValueChange = { hintText = it },
          label = { Text("Текст подсказки (необязательно)") },
          modifier = Modifier.fillMaxWidth(),
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f).height(48.dp),
          ) {
            Text("Отмена")
          }
          Button(
            onClick = {
              onCreate(title, themeTag, difficulty, prompt, board, solutionNotation, hintText)
            },
            enabled = legalMoves.isNotEmpty() && solutionNotation.isNotBlank(),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("builder_save_button"),
          ) {
            Text("Сохранить")
          }
        }
      }
    }
  }
}
