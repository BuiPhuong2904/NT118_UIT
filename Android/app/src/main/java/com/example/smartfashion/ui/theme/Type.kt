package com.example.smartfashion.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.smartfashion.R

// Cài đặt provider để kéo font
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Khai báo
val montserratFont = GoogleFont("Montserrat")
val beVietnamProFont = GoogleFont("Be Vietnam Pro")
val chakraPetchFont = GoogleFont("Chakra Petch")

// Đóng gói thành FontFamily
val MontserratFamily = FontFamily(Font(googleFont = montserratFont, fontProvider = provider))
val BeVietnamProFamily = FontFamily(Font(googleFont = beVietnamProFont, fontProvider = provider))
val ChakraPetchFamily = FontFamily(Font(googleFont = chakraPetchFont, fontProvider = provider))

val Typography = Typography(
    // NỘI DUNG
    bodyLarge = TextStyle(
        fontFamily = ChakraPetchFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // TIÊU ĐỀ CHÍNH
    titleLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // TIÊU ĐỀ PHỤ
    titleMedium = TextStyle(
        fontFamily = BeVietnamProFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
)