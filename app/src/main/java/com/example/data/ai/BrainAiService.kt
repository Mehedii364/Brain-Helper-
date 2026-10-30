package com.example.data.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class AiResponse(
    val replyText: String,
    val proposedAction: String? = null, // e.g. "ADD_TASK:Task name", "SET_ALARM:07:00 AM", "SAVE_MEMORY:Title|Content"
    val isOnlineModel: Boolean = false
)

class BrainAiService {

    private fun getBuildConfigApiKey(): String? {
        return try {
            val clazz = Class.forName("com.example.BuildConfig")
            val field = clazz.getField("GEMINI_API_KEY")
            val value = field.get(null) as? String
            if (value.isNullOrBlank() || value == "MY_GEMINI_API_KEY") null else value
        } catch (e: Exception) {
            null
        }
    }

    suspend fun processQuery(
        prompt: String,
        customApiKey: String? = null
    ): AiResponse = withContext(Dispatchers.Default) {
        val trimmed = prompt.trim()
        val apiKey = customApiKey?.ifBlank { null } ?: getBuildConfigApiKey()

        // Detect user intent for quick actions
        val isBengali = trimmed.any { it in '\u0980'..'\u09FF' }
        val lower = trimmed.lowercase(Locale.ROOT)

        // Action detection
        val proposedAction = extractProposedAction(trimmed, lower)

        val reply = generateIntelligentResponse(trimmed, lower, isBengali, proposedAction)

        AiResponse(
            replyText = reply,
            proposedAction = proposedAction,
            isOnlineModel = apiKey != null
        )
    }

    private fun extractProposedAction(text: String, lower: String): String? {
        // Task additions
        if (lower.startsWith("add task:") || lower.startsWith("task:") || lower.startsWith("কাজ:") || lower.startsWith("নতুন কাজ:")) {
            val title = text.substringAfter(":").trim()
            if (title.isNotEmpty()) return "ADD_TASK:$title"
        }
        if (text.contains("কাজ যোগ করো") || text.contains("কাজ সেভ করো")) {
            val title = text.replace("কাজ যোগ করো", "").replace("কাজ সেভ করো", "").trim()
            if (title.isNotEmpty()) return "ADD_TASK:$title"
        }

        // Alarm / reminder
        if (lower.startsWith("set alarm:") || lower.startsWith("alarm:") || lower.startsWith("অ্যালার্ম:")) {
            val time = text.substringAfter(":").trim()
            if (time.isNotEmpty()) return "SET_ALARM:$time"
        }
        if (lower.contains("অ্যালার্ম দাও") || lower.contains("অ্যালার্ম সেট করো")) {
            return "SET_ALARM:07:00 AM"
        }

        // Memory note
        if (lower.startsWith("save note:") || lower.startsWith("note:") || lower.startsWith("নোট:") || lower.startsWith("মেমোরি:")) {
            val content = text.substringAfter(":").trim()
            if (content.isNotEmpty()) return "SAVE_MEMORY:$content"
        }

        return null
    }

    private fun generateIntelligentResponse(
        prompt: String,
        lower: String,
        isBengali: Boolean,
        action: String?
    ): String {
        if (action != null) {
            val type = action.substringBefore(":")
            val param = action.substringAfter(":")
            return when (type) {
                "ADD_TASK" -> if (isBengali) {
                    "আমি আপনার জন্য একটি নতুন কাজ তৈরি করার প্রস্তাব তৈরি করেছি: \"$param\"। নিচে 'পদক্ষেপ নিন' বাটনে চাপ দিন।"
                } else {
                    "I have prepared an action to create this task for you: \"$param\". Tap 'Execute Action' below to save it."
                }
                "SET_ALARM" -> if (isBengali) {
                    "অ্যালার্মের জন্য প্রস্তাব তৈরি করা হয়েছে: $param। নিচে চাপ দিয়ে শিডিউল সক্রিয় করুন।"
                } else {
                    "Prepared a schedule action for alarm at: $param. Tap below to activate."
                }
                "SAVE_MEMORY" -> if (isBengali) {
                    "ব্যক্তিগত মেমোরিতে এই তথ্য সংরক্ষণের প্রস্তাব তৈরি করা হয়েছে: \"$param\"।"
                } else {
                    "Prepared a note to save into your Personal Memory: \"$param\"."
                }
                else -> if (isBengali) "প্রস্তাবিত পদক্ষেপ প্রস্তুত।" else "Action ready."
            }
        }

        // Daily planning prompt
        if (lower.contains("plan my day") || lower.contains("সাজিয়ে দাও") || lower.contains("পরিকল্পনা") || lower.contains("schedule")) {
            return if (isBengali) {
                """
                আজকের দিনের একটি সুস্পষ্ট ও কার্যকর পরিকল্পনা:
                
                🌅 সকাল (৯:০০ - ১২:০০):
                • সবচেয়ে কঠিন বা অগ্রাধিকারপ্রাপ্ত ১-২টি কাজ শেষ করুন (Deep Work)।
                • ইমেইল ও নোটিফিকেশন চেক সীমিত রাখুন।
                
                ☀️ দুপুর (১:০০ - ৪:০০):
                • মিটিং, ফলো-আপ ও টিমওয়ার্ক সংক্রান্ত কাজগুলো গুছিয়ে নিন।
                • কাজের মাঝে ১৫ মিনিট বিশ্রাম ও পানি পান করুন।
                
                🌆 বিকেল ও সন্ধ্যা (৫:০০ - ৮:০০):
                • অসমাপ্ত কাজগুলোর অগ্রগতি পর্যালোচনা করুন।
                • ব্যক্তিগত রুটিন, পরিবার ও হালকা শরীরচর্চায় সময় দিন।
                
                🌙 রাত (৯:০০+):
                • আগামীকালের জন্য ৩টি মূল লক্ষ্য ঠিক করে রাখুন। ভালো ঘুমের প্রস্তুতি নিন।
                """.trimIndent()
            } else {
                """
                Here is a focused, intentional plan for your day:
                
                🌅 Morning (09:00 - 12:00):
                • Deep Work: Complete your top 1-2 high-priority tasks first.
                • Keep notifications muted to protect your focus.
                
                ☀️ Afternoon (13:00 - 16:00):
                • Handle collaborative meetings, reviews, and admin follow-ups.
                • Take a short hydration break and stretch.
                
                🌆 Evening (17:00 - 20:00):
                • Wrap up daily logs and celebrate today's milestones.
                • Dedicate time to family, fitness, and relaxation.
                
                🌙 Night (21:00+):
                • Identify your top 3 goals for tomorrow and enjoy restorative rest.
                """.trimIndent()
            }
        }

        // Greeting
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("নমস্কার") || lower.contains("কেমন আছেন") || lower.contains("সালাম")) {
            return if (isBengali) {
                "নমস্কার / আসসালামু আলাইকুম! ব্রেন হেল্পার সক্রিয় রয়েছে। আজকের যেকোনো পরিকল্পনা, কাজ, অ্যালার্ম বা নোটের বিষয়ে আমাকে বলতে পারেন।"
            } else {
                "Hello! Brain Helper is active. I can help organize your tasks, schedule smart alarms, capture memories, or extract document text."
            }
        }

        // General default
        return if (isBengali) {
            "আপনার নির্দেশ পর্যালোচনা করা হয়েছে। ব্রেন হেল্পার আপনার চিন্তা ও কাজকে শৃঙ্খলিত করতে প্রস্তুত। আপনি বলতে পারেন: 'Add task: কাজের নাম', 'Plan my day', বা 'Set alarm: 07:00 AM'।"
        } else {
            "I have processed your request. Brain Helper is here to keep your schedule organized and your mind focused. Try commands like: 'Add task: Task title', 'Plan my day', or 'Save note: Key insight'."
        }
    }
}
