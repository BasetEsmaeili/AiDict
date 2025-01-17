package com.baset.ai.dict.presentation.ui.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.baset.ai.dict.R
import com.baset.ai.dict.presentation.ui.core.theme.appLogoBorderRadius
import com.baset.ai.dict.presentation.ui.core.theme.appLogoSizeForOverlayService
import com.baset.ai.dict.presentation.ui.core.theme.margin4

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    background: Color = colorResource(R.color.approval_green),
    shape: Shape = CircleShape,
    borderWidth: Dp = appLogoBorderRadius,
    borderColor: Color = colorResource(R.color.white),
    borderShape: Shape = CircleShape,
    innerPadding: PaddingValues = PaddingValues(margin4),
    logoColorFilter: ColorFilter = ColorFilter.tint(colorResource(R.color.white)),
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    Image(
        modifier = modifier
            .background(
                color = background,
                shape = shape
            )
            .border(
                borderWidth,
                borderColor,
                borderShape
            )
            .padding(innerPadding)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        painter = painterResource(R.drawable.ic_logo),
        colorFilter = logoColorFilter,
        contentDescription = stringResource(R.string.app_name)
    )
}

@Preview
@Composable
private fun AiOverlayBubblePreview() {
    AppLogo(
        modifier = Modifier.size(appLogoSizeForOverlayService),
        onClick = {}
    )
}