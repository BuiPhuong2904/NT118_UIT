package com.example.smartfashion.ui.screens.profile

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    // Chuyển String sang RequestBody (Dùng cho các trường text trong Multipart)
    fun String.toPart(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())

    // Chuyển Uri sang MultipartBody.Part (Dùng cho file ảnh)
    fun prepareImagePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
        // Sử dụng System.currentTimeMillis() để tên file luôn duy nhất, tránh cache ảnh cũ
        val file = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
        
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        
        inputStream?.use { input -> 
            outputStream.use { output -> 
                input.copyTo(output) 
            } 
        }

        // Khai báo đúng định dạng ảnh để Server dễ xử lý
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        
        // Trả về Part để gửi qua Retrofit
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}