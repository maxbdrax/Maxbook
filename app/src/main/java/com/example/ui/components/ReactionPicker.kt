package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReactionType

@Composable
fun ReactionPicker(
    visible: Boolean,
    onReactionSelected: (ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }) + scaleIn(),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { 40 }) + scaleOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val reactions = listOf(
                    ReactionType.LIKE,
                    ReactionType.LOVE,
                    ReactionType.HAHA,
                    ReactionType.WOW,
                    ReactionType.SAD,
                    ReactionType.ANGRY
                )

                reactions.forEach { reaction ->
                    ReactionItem(
                        reaction = reaction,
                        onClick = { onReactionSelected(reaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReactionItem(
    reaction: ReactionType,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.4f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "reaction_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(4.dp)
            .testTag("reaction_option_${reaction.name.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = reaction.emoji,
                fontSize = 24.sp
            )
        }
    }
}
