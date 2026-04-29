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

@Composable
fun AppNavigation(startDestination: String) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    var userToken by remember { mutableStateOf(tokenManager.getToken()) }
    val isFirstTimeOpen = false

    // Khởi tạo TravelViewModel ở đây để dùng chung cho luồng Planner (TravelPlanner và CreateTrip)
    // Việc dùng chung ViewModel giúp danh sách cập nhật ngay lập tức sau khi tạo
    val travelViewModel: TravelViewModel = hiltViewModel()

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

        composable("onboarding_screen") { OnboardingScreen() }

        // ==========================================
        // 1. TRANG CHỦ & AI
        // ==========================================
        composable("home_screen") { HomeScreen(navController) }

        composable("ai_chat_screen") {
            AiChatScreen(
                navController = navController,
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
            StoreItemDetailScreen(navController = navController, templateId = templateId)
        }

        composable(
            route = "item_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val clothingId = backStackEntry.arguments?.getInt("id") ?: 0
            ItemDetailScreen(navController = navController, clothingId = clothingId)
        }

        composable(
            route = "add_item_screen?imageUriNoBg={imageUriNoBg}&imageUriOriginal={imageUriOriginal}&imageId={imageId}",
            arguments = listOf(
                navArgument("imageUriNoBg") { type = NavType.StringType; defaultValue = "" },
                navArgument("imageUriOriginal") { type = NavType.StringType; defaultValue = "" },
                navArgument("imageId") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val uriNoBg = backStackEntry.arguments?.getString("imageUriNoBg") ?: ""
            val uriOriginal = backStackEntry.arguments?.getString("imageUriOriginal") ?: ""
            val imgId = backStackEntry.arguments?.getInt("imageId") ?: 0
            AddItemScreen(navController = navController, imageUri = uriNoBg, originalImageUri = uriOriginal, imageId = imgId)
        }

        composable(
            route = "loading_upload_screen?imageUri={imageUri}",
            arguments = listOf(navArgument("imageUri") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("imageUri") ?: ""
            LoadingUploadScreen(
                imageUri = uri,
                onFinished = { cloudinaryNoBgUrl, cloudinaryOriginalUrl, returnedImageId ->
                    val route = "add_item_screen?imageUriNoBg=$cloudinaryNoBgUrl&imageUriOriginal=$cloudinaryOriginalUrl&imageId=$returnedImageId"
                    navController.navigate(route) {
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
            arguments = listOf(navArgument("outfitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val outfitId = backStackEntry.arguments?.getInt("outfitId") ?: 0
            OutfitDetailScreen(navController = navController, outfitId = outfitId)
        }
        composable("studio_screen") { StudioScreen(navController) }

        // ==========================================
        // 4. LỊCH & CHUYẾN ĐI (PLANNER)
        // ==========================================
        composable("calendar_screen") { CalendarScreen(navController) }

        composable("travel_planner_screen") {
            TravelPlannerScreen(
                viewModel = travelViewModel,
                onBackClick = { navController.popBackStack() },
                onTripClick = { tripId -> 
                    navController.navigate("trip_detail_screen/$tripId") 
                },
                onCreateTripClick = {
                    navController.navigate("create_trip_screen")
                }
            )
        }

        composable("create_trip_screen") {
            CreateTripScreen(
                viewModel = travelViewModel,
                onBackClick = { navController.popBackStack() },
                onCreateClick = { newTripId ->
                    // Điều hướng sang màn hình chi tiết của chuyến đi vừa tạo
                    navController.navigate("trip_detail_screen/$newTripId") {
                        // Xóa CreateTrip khỏi stack để khi back sẽ về TravelPlanner
                        popUpTo("create_trip_screen") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "trip_detail_screen/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            TripDetailScreen(
                tripId = tripId,
                onBackClick = { 
                    // Quay lại màn hình danh sách chính
                    navController.navigate("travel_planner_screen") {
                        popUpTo("travel_planner_screen") { inclusive = true }
                    }
                },
                onAddOutfitClick = {
                    navController.navigate("select_outfit_luggage")
                }
            )
        }

        composable("select_outfit_calendar") {
            OutfitSelectionScreen(navController = navController, isSingleSelection = true)
        }

        composable("select_outfit_luggage") {
            OutfitSelectionScreen(navController = navController, isSingleSelection = false)
        }

        // ==========================================
        // 5. KHÁM PHÁ (FASHION HUB)
        // ==========================================
        composable("fashion_hub_screen") {
            FashionHubScreen(
                onBackClick = { navController.popBackStack() },
                onArticleClick = {},
                onTrendClick = { navController.navigate("community_trend_screen") }
            )
        }
        composable("community_trend_screen") {
            CommunityTrendScreen(onBackClick = { navController.popBackStack() }, onPostClick = {})
        }

        // ==========================================
        // 6. TÀI KHOẢN (PROFILE)
        // ==========================================
        composable("profile_screen") {
            ProfileScreen(
                navController = navController,
                onLogoutClick = {
                    tokenManager.clearAll()
                    userToken = null
                    navController.navigate("login_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                }
            )
        }
        composable("edit_profile_screen") { EditProfileScreen(onBackClick = { navController.popBackStack() }) }
        composable("notification_screen") { NotificationScreen(onBackClick = { navController.popBackStack() }) }
        composable("settings_screen") { SettingsScreen(onBackClick = { navController.popBackStack() }) }

        // ==========================================
        // 7. XÁC THỰC (AUTH)
        // ==========================================
        composable("login_screen") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { token, userId, username ->
                    tokenManager.saveToken(token)
                    tokenManager.saveUserId(userId)
                    tokenManager.saveUsername(username)
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
                onSignUpSuccess = { token, userId, username ->
                    tokenManager.saveToken(token)
                    tokenManager.saveUserId(userId)
                    tokenManager.saveUsername(username)
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