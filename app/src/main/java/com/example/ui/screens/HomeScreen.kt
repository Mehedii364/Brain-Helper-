package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.ObsidianCard
import com.example.ui.components.ObsidianPrimaryButton
import com.example.ui.components.ObsidianSummaryCard
import com.example.ui.components.ObsidianTopBar
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSecondarySurface
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SubtitleColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletHighlight
import com.example.ui.theme.VioletPrimary
import com.example.ui.theme.WarmAccent
import com.example.viewmodel.BrainHelperViewModel
import com.example.viewmodel.ScreenTab
import com.example.viewmodel.SubScreen

@Composable
fun HomeScreen(
    viewModel: BrainHelperViewModel,
    modifier: Modifier = Modifier
) {
    val totalTasks by viewModel.totalTasksCount.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasksCount.collectAsStateWithLifecycle()
    val activeReminders by viewModel.activeRemindersCount.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        val horizontalPadding = if (maxWidth > 600.dp) 24.dp else 16.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section B: Header
            item {
                ObsidianTopBar(
                    title = stringResource(id = R.string.app_name),
                    subtitle = stringResource(id = R.string.app_tagline),
                    notificationCount = activeReminders,
                    onNotificationClick = {
                        viewModel.navigateToSubScreen(SubScreen.ALARMS)
                    }
                )
            }

            // Section C: Main Hero Card
            item {
                ObsidianHeroCard(
                    totalTasks = totalTasks,
                    activeReminders = activeReminders,
                    onTasksClick = { viewModel.navigateToTab(ScreenTab.TASKS) },
                    onRemindersClick = { viewModel.navigateToSubScreen(SubScreen.ALARMS) }
                )
            }

            // Section D: AI Input Card
            item {
                ObsidianAiInputCard(
                    onOpenChat = { viewModel.navigateToTab(ScreenTab.CHAT) },
                    onOpenVoice = { viewModel.navigateToSubScreen(SubScreen.VOICE) }
                )
            }

            // Section E: Quick Actions Section
            item {
                Column {
                    Text(
                        text = stringResource(id = R.string.quick_actions_title),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card 1: Plan my day
                        QuickActionGridCard(
                            title = stringResource(id = R.string.plan_my_day),
                            subtitle = stringResource(id = R.string.plan_my_day_sub),
                            icon = Icons.Default.CalendarMonth,
                            iconTint = ElectricBlue,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_plan_day",
                            onClick = { viewModel.navigateToTab(ScreenTab.TASKS) }
                        )

                        // Card 2: Smart alarm
                        QuickActionGridCard(
                            title = stringResource(id = R.string.smart_alarm),
                            subtitle = stringResource(id = R.string.smart_alarm_sub),
                            icon = Icons.Default.Alarm,
                            iconTint = Color(0xFFB7A5FF),
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_smart_alarm",
                            onClick = { viewModel.navigateToSubScreen(SubScreen.ALARMS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card 3: AI scanner
                        QuickActionGridCard(
                            title = stringResource(id = R.string.ai_scanner),
                            subtitle = stringResource(id = R.string.ai_scanner_sub),
                            icon = Icons.Default.DocumentScanner,
                            iconTint = MintSuccess,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_ai_scanner",
                            onClick = { viewModel.navigateToSubScreen(SubScreen.SCANNER) }
                        )

                        // Card 4: My memory
                        QuickActionGridCard(
                            title = stringResource(id = R.string.my_memory),
                            subtitle = stringResource(id = R.string.my_memory_sub),
                            icon = Icons.Default.Psychology,
                            iconTint = WarmAccent,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_my_memory",
                            onClick = { viewModel.navigateToTab(ScreenTab.MEMORY) }
                        )
                    }
                }
            }

            // Section F: Daily Progress Section
            item {
                ObsidianDailyProgressCard(
                    completedTasks = completedTasks,
                    totalTasks = totalTasks,
                    onClick = { viewModel.navigateToTab(ScreenTab.TASKS) }
                )
            }

            // Extra bottom spacing for smooth scrolling over bottom nav
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ObsidianHeroCard(
    totalTasks: Int,
    activeReminders: Int,
    onTasksClick: () -> Unit,
    onRemindersClick: () -> Unit
) {
    ObsidianCard(
        cornerRadius = 20.dp,
        backgroundColor = ObsidianSurface,
        borderColor = ObsidianBorder,
        contentPadding = 22.dp,
        modifier = Modifier.testTag("hero_summary_card")
    ) {
        // 1. Small violet uppercase label
        Text(
            text = stringResource(id = R.string.assistant_badge),
            color = VioletHighlight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Large headline
        Text(
            text = stringResource(id = R.string.headline_today),
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 3. Supporting Bengali text
        Text(
            text = stringResource(id = R.string.bengali_hero_sub),
            color = SubtitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Two side-by-side summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Task list summary card
            val tasksFormatted = if (totalTasks < 10) "0$totalTasks" else "$totalTasks"
            ObsidianSummaryCard(
                icon = Icons.Default.Checklist,
                iconTint = ElectricBlue,
                value = tasksFormatted,
                label = stringResource(id = R.string.tasks_stat_label),
                modifier = Modifier
                    .weight(1f)
                    .testTag("summary_tasks_card"),
                onClick = onTasksClick
            )

            // Reminders summary card
            val remindersFormatted = if (activeReminders < 10) "0$activeReminders" else "$activeReminders"
            ObsidianSummaryCard(
                icon = Icons.Default.Alarm,
                iconTint = VioletHighlight,
                value = remindersFormatted,
                label = stringResource(id = R.string.reminders_stat_label),
                modifier = Modifier
                    .weight(1f)
                    .testTag("summary_reminders_card"),
                onClick = onRemindersClick
            )
        }
    }
}

@Composable
private fun ObsidianAiInputCard(
    onOpenChat: () -> Unit,
    onOpenVoice: () -> Unit
) {
    ObsidianCard(
        cornerRadius = 18.dp,
        backgroundColor = ObsidianSurface,
        borderColor = ObsidianBorder,
        contentPadding = 16.dp,
        modifier = Modifier.testTag("ai_prompt_card")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = VioletHighlight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.ask_brain_helper),
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input preview area with Bengali placeholder and mic button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianSecondarySurface)
                .clickable(onClick = onOpenChat)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.ask_input_placeholder),
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x337367F0))
                        .clickable(onClick = onOpenVoice)
                        .testTag("ai_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        tint = VioletHighlight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prominent violet button below: "Start a conversation"
        ObsidianPrimaryButton(
            text = stringResource(id = R.string.start_conversation),
            onClick = onOpenChat,
            icon = Icons.Default.AutoAwesome,
            backgroundColor = VioletPrimary,
            modifier = Modifier.testTag("start_conversation_button")
        )
    }
}

@Composable
private fun QuickActionGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(108.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianSecondarySurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ObsidianDailyProgressCard(
    completedTasks: Int,
    totalTasks: Int,
    onClick: () -> Unit
) {
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f
    val encouragement = when {
        totalTasks == 0 -> stringResource(id = R.string.progress_encouragement_empty)
        completedTasks == totalTasks -> stringResource(id = R.string.progress_encouragement_done)
        else -> stringResource(id = R.string.progress_encouragement_good)
    }

    ObsidianCard(
        cornerRadius = 16.dp,
        backgroundColor = ObsidianSurface,
        borderColor = ObsidianBorder,
        contentPadding = 18.dp,
        modifier = Modifier.testTag("daily_progress_card"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.daily_progress),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.daily_progress_sub),
                    color = SubtitleColor,
                    fontSize = 12.sp
                )
            }

            Text(
                text = stringResource(id = R.string.tasks_completed_ratio, completedTasks, totalTasks),
                color = if (progress >= 1f) MintSuccess else ElectricBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = if (progress >= 1f) MintSuccess else VioletPrimary,
            trackColor = ObsidianSecondarySurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = encouragement,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}
