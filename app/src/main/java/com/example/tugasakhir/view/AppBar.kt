package com.example.tugasakhir.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.tugasakhir.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerpustakaanTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    val primaryColor = colorResource(id = R.color.primary_blue)
    val textColorPrimary = colorResource(id = R.color.text_primary)
    val backgroundColor = colorResource(id = R.color.background)

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title.uppercase(),
                fontWeight = FontWeight.SemiBold,
                color = textColorPrimary
            )
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = textColorPrimary,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = primaryColor
                    )
                }
            }
        }
    )
}