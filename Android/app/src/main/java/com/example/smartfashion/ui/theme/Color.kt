package com.example.smartfashion.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Màu Nền (Background)
val BgLight = Color(0xFFF8F9FD)
val BgDark = Color(0xFF1C1752)
val BgCyan = Color(0xFF00C6FF)
val BgPurple = Color(0xFF6D3194)

// Màu Chủ Đạo / Nút (Primary)
val PrimaryCyan = Color(0xFF00C6FF)
val PrimaryPink = Color(0xFFFF00CC)

val AccentBlue = Color(0xFF1D8ACA)
val AccentPurplePink = Color(0xFFF16C98)

val SoftBlue = Color(0xFF61ADDA)
val SoftPink = Color(0xFFF49DCD)

// Màu Phụ (Secondary)
val SecWhite = Color(0xFFFFFFFF)
val SecLightPink = Color(0xFFFFCBEB)
val SecDarkPink = Color(0xFFF5569B)

// Màu Chữ (Text)
val TextBlue = Color(0xFF1355B2)
val TextPink = Color(0xFFE93397)
val TextDarkBlue = Color(0xFF292B7A)
val TextGray = Color(0xFFBDBDBD)
val TextLightBlue = Color(0xFF4277C1)

// Gradient Nền 2 (Cyan -> Tím)
val GradientBg2 = Brush.verticalGradient(
    colors = listOf(BgCyan, BgPurple)
)

// Gradient Nút Chủ Đạo
val GradientPrimaryButton = Brush.horizontalGradient(
    0.0f to PrimaryCyan,
    0.4f to PrimaryCyan,
    1.0f to PrimaryPink
)

// Gradient Xanh ngọc -> Hồng phấn
val GradientSoft = Brush.horizontalGradient(
    colors = listOf(SoftBlue, SoftPink)
)

val GradientAccent3 = Brush.verticalGradient(
    colors = listOf(AccentBlue, TextPink, AccentPurplePink)
)

// Gradient Màu Phụ 1 (Trắng -> Hồng nhạt)
val GradientSecondary1 = Brush.horizontalGradient(
    colors = listOf(SecWhite, SecLightPink)
)

// Gradient Màu Phụ 2 (Hồng đậm -> Hồng nhạt -> Xanh dương)
val GradientSecondary2 = Brush.horizontalGradient(
    colors = listOf(SecDarkPink, SecLightPink, TextBlue)
)

// Gradient Màu chữ
val GradientText = Brush.horizontalGradient(
    colors = listOf(TextBlue, TextPink)
)