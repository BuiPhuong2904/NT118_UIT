package com.example.smartfashion.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

import com.example.smartfashion.ui.screens.auth.*
import com.example.smartfashion.ui.screens.closet.*
import com.example.smartfashion.ui.screens.home.HomeScreen
import com.example.smartfashion.ui.screens.planner.*
import com.example.smartfashion.ui.screens.profile.*
import com.example.smartfashion.ui.screens.studio.*
import com.example.smartfashion.ui.screens.ai.AiChatScreen
import com.example.smartfashion.ui.screens.hub.FashionHubScreen
import com.example.smartfashion.ui.screens.hub.CommunityTrendScreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartfashion.data.local.TokenManager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AppNavigation(startDestination: String) {

    val navController = rememberNavController()

    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    
    var userToken by remember { mutableStateOf(tokenManager.getToken()) }
    val isFirstTimeOpen = false


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("splash_screen") {

            SplashScreen(onSplashFinished = { })

            LaunchedEffect(Unit) {
                delay(2000)

                val token = tokenManager.getToken()

                val destination = when {
                    isFirstTimeOpen -> "onboarding_screen"
                    token == null -> "login_screen"
                    else -> "home_screen"
                }

                navController.navigate(destination) {
                    popUpTo("splash_screen") { inclusive = true }
                }
            }
        }

        composable("onboarding_screen") {
            OnboardingScreen()
        }

        // ==========================================
        // 1. TRANG CHỦ & AI
        // ==========================================
        composable("home_screen") {
            HomeScreen(navController)
        }

        composable("ai_chat_screen") {
            AiChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ==========================================
        // 2. TỦ ĐỒ (CLOSET)
        // ==========================================
        composable("closet_screen") { ClosetScreen(navController) }
        composable("insights_screen") { InsightsScreen(navController) }
        composable("declutter_screen") { DeclutterScreen(navController) }
        composable("favorites_screen") { FavoritesScreen(navController) }
        composable("store_screen") { StoreScreen(navController) }

        composable(
            route = "store_item_detail/{templateId}",
            arguments = listOf(navArgument("templateId") { type = NavType.IntType })
        ) { backStackEntry ->
            val templateId = backStackEntry.arguments?.getInt("templateId") ?: 0

            StoreItemDetailScreen(
                navController = navController,
                templateId = templateId
            )
        }

        composable(
            route = "item_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val clothingId = backStackEntry.arguments?.getInt("id") ?: 0

            ItemDetailScreen(
                navController = navController,
                clothingId = clothingId
            )
        }

        composable("add_item_screen") { AddItemScreen(navController) }

        composable("loading_upload_screen") {
            LoadingUploadScreen(
                onFinished = {
                    navController.navigate("add_item_screen") {
                        popUpTo("loading_upload_screen") { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        // ==========================================
        // 3. PHỐI ĐỒ (STUDIO)
        // ==========================================
        composable("saved_outfits_screen") { SavedOutfitsScreen(navController) }

        composable(
            route = "outfit_detail_screen/{outfitId}",
            arguments = listOf(androidx.navigation.navArgument("outfitId") {
                type = androidx.navigation.NavType.IntType
            })
        ) { backStackEntry ->
            val outfitId = backStackEntry.arguments?.getInt("outfitId") ?: 0

            OutfitDetailScreen(
                navController = navController,
                outfitId = outfitId
            )
        }

        composable("studio_screen") { StudioScreen() }

        // ==========================================
        // 4. LỊCH & CHUYẾN ĐI (PLANNER)
        // ==========================================
        composable("calendar_screen") { CalendarScreen(navController) }

        composable("travel_planner_screen") {
            TravelPlannerScreen(
                onBackClick = { navController.popBackStack() },
                onTripClick = { navController.navigate("trip_detail_screen") }
            )
        }

        composable("create_trip_screen") {
            CreateTripScreen(
                onBackClick = { navController.popBackStack() },
                onCreateClick = {
                    navController.navigate("trip_detail_screen") {
                        popUpTo("create_trip_screen") { inclusive = true }
                    }
                }
            )
        }

        composable("trip_detail_screen") {
            TripDetailScreen(
                onBackClick = { navController.popBackStack() },
                onAddOutfitClick = {
                    navController.navigate("select_outfit_luggage")
                }
            )
        }

        composable("select_outfit_calendar") {
            OutfitSelectionScreen(
                isSingleSelection = true,
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { navController.popBackStack() }
            )
        }

        composable("select_outfit_luggage") {
            OutfitSelectionScreen(
                isSingleSelection = false,
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { navController.popBackStack() }
            )
        }

        // ==========================================
        // 5. KHÁM PHÁ (FASHION HUB)
        // ==========================================
        composable("fashion_hub_screen") {
            FashionHubScreen(
                onBackClick = { navController.popBackStack() },
                onArticleClick = {},
                onTrendClick = {
                    navController.navigate("community_trend_screen")
                }
            )
        }

        composable("community_trend_screen") {
            CommunityTrendScreen(
                onBackClick = { navController.popBackStack() },
                onPostClick = {}
            )
        }

        // ==========================================
        // 6. TÀI KHOẢN (PROFILE)
        // ==========================================
        composable("profile_screen") {
            ProfileScreen(
                navController = navController,
                onLogoutClick = {
                    tokenManager.clearToken()

                    userToken = null

                    navController.navigate("login_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                }
            )
        }

        composable("edit_profile_screen") {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable("notification_screen") {
            NotificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("settings_screen") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ==========================================
        // 7. XÁC THỰC (AUTH)
        // ==========================================
        composable("login_screen") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { token ->
                    tokenManager.saveToken(token)
                    userToken = token
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup_screen") },
                onForgotPasswordClick = { navController.navigate("forgot_password_screen") }
            )
        }

        composable("signup_screen") {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                viewModel = signUpViewModel,
                onSignUpSuccess = { token ->
                    tokenManager.saveToken(token)
                    userToken = token
                    navController.navigate("home_screen") {
                        popUpTo("signup_screen") { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable("forgot_password_screen") {
            val forgotViewModel: ForgotPasswordViewModel = hiltViewModel()
            val state by forgotViewModel.state
            var emailInput by remember { mutableStateOf("") }

            // Chuyển sang màn hình Reset khi gửi email thành công
            LaunchedEffect(state) {
                if (state is ForgotPasswordState.Success) {
                    navController.navigate("reset_password_screen/$emailInput")
                    forgotViewModel.resetState()
                }
            }

            ForgotPasswordScreen(
                onBackToLoginClick = { navController.popBackStack() },
                onSendEmailClick = { email ->
                    emailInput = email
                    forgotViewModel.sendResetEmail(email)
                }
            )
        }

        composable(
            route = "reset_password_screen/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("email") ?: ""
            val resetViewModel: ResetPasswordViewModel = hiltViewModel()
            val state by resetViewModel.state

            // Quay về Login sau khi đổi mật khẩu thành công
            LaunchedEffect(state) {
                if (state is ResetPasswordState.Success) {
                    delay(1500)
                    navController.navigate("login_screen") {
                        popUpTo("forgot_password_screen") { inclusive = true }
                    }
                }
            }

            ResetPasswordScreen(
                email = emailArg,
                onBackToLoginClick = { navController.navigate("login_screen") },
                onResetPasswordClick = { email, otp, newPass ->
                    resetViewModel.resetPassword(email, otp, newPass)
                }
            )
        }
    }

}