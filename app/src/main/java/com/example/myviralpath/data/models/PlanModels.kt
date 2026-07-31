package com.example.myviralpath.data.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StrategicPlan(
    val id: String = "",
    val user_id: String = "",
    val plan_date: String = "",
    val content_ideas: List<ContentIdea> = emptyList(),
    val best_posting_time: String? = null,
    val recommended_platform: String? = null,
    val opportunity_score: Int? = null,
    val competition_level: String? = null,
    val growth_potential: String? = null
)

@Serializable
data class ContentIdea(
    val type: String,
    val title: String,
    val platform: String,
    val recommended_time: String,
    val status: String
)

@Serializable
data class NextStep(
    val id: String = "",
    val user_id: String = "",
    val plan_id: String? = null,
    val title: String = "",
    val is_completed: Boolean = false,
    val order_index: Int = 0
)
