data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val message: String,
    val token: String,
    val user: User
)

data class User(
    val user_id: Int,
    val username: String,
    val email: String,
    val gender: String?
)
