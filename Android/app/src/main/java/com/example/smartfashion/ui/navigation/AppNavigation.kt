package com.example.smartfashion.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

import com.example.smartfashion.ui.screens.auth.ResetPasswordScreen
import com.example.smartfashion.ui.viewmodel.ResetPasswordViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartfashion.ui.viewmodel.ForgotPasswordViewModel
import com.example.smartfashion.ui.screens.auth.LoginScreen
import com.example.smartfashion.ui.screens.auth.SignUpScreen
import com.example.smartfashion.ui.screens.auth.SplashScreen
import com.example.smartfashion.ui.screens.closet.ClosetScreen
import com.example.smartfashion.ui.screens.closet.AddItemScreen
import com.example.smartfashion.ui.screens.closet.DeclutterScreen
import com.example.smartfashion.ui.screens.closet.FavoritesScreen
import com.example.smartfashion.ui.screens.closet.InsightsScreen
import com.example.smartfashion.ui.screens.closet.ItemDetailScreen
import com.example.smartfashion.ui.screens.closet.LoadingUploadScreen
import com.example.smartfashion.ui.screens.closet.StoreScreen
import com.example.smartfashion.ui.screens.closet.StoreItemDetailScreen
import com.example.smartfashion.ui.screens.home.HomeScreen
import com.example.smartfashion.ui.screens.planner.CalendarScreen
import com.example.smartfashion.ui.screens.planner.OutfitSelectionScreen
import com.example.smartfashion.ui.screens.profile.ProfileScreen
import com.example.smartfashion.ui.screens.studio.OutfitDetailScreen
import com.example.smartfashion.ui.screens.studio.SavedOutfitsScreen
import com.example.smartfashion.ui.screens.studio.StudioScreen
import com.example.smartfashion.ui.screens.ai.AiChatScreen
import com.example.smartfashion.ui.screens.auth.ForgotPasswordScreen
import com.example.smartfashion.ui.screens.auth.OnboardingScreen
import com.example.smartfashion.ui.screens.hub.FashionHubScreen
import com.example.smartfashion.ui.screens.hub.CommunityTrendScreen
import com.example.smartfashion.ui.screens.planner.TravelPlannerScreen
import com.example.smartfashion.ui.screens.planner.CreateTripScreen
import com.example.smartfashion.ui.screens.planner.TripDetailScreen
import com.example.smartfashion.ui.screens.profile.EditProfileScreen
import com.example.smartfashion.ui.screens.profile.NotificationScreen
import com.example.smartfashion.ui.screens.profile.SettingsScreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.smartfashion.data.local.TokenManager

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
            LoginScreen(
                onLoginSuccess = { token: String ->
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
            SignUpScreen(
                onSignUpSuccess = { token: String ->

                    tokenManager.saveToken(token)

                    userToken = token

                    navController.navigate("login_screen") {
                        popUpTo("signup_screen") { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable("forgot_password_screen") {
            val viewModel: ForgotPasswordViewModel = viewModel()

    ForgotPasswordScreen(
                onBackToLoginClick = { navController.popBackStack() },

                onSendEmailClick = { email ->

                    viewModel.sendResetEmail(email)

                }
            )
        }
        composable(
    route = "reset_password_screen/{token}",
    arguments = listOf(
        androidx.navigation.navArgument("token") {
            type = androidx.navigation.NavType.StringType
        }
    )
) { backStackEntry ->

    val token = backStackEntry.arguments?.getString("token") ?: ""

    val viewModel: ResetPasswordViewModel = viewModel()

    ResetPasswordScreen(

        onResetPasswordClick = { newPassword ->

            viewModel.resetPassword(token, newPassword)

                }
            )
        }

    }

}

