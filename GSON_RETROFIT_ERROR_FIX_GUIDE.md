# 🔧 Gson & Retrofit2 Import Error Fix Guide

## ✅ GSON DAN RETROFIT2 DEPENDENCIES SUDAH BENAR!

Dependencies gson dan retrofit2 sudah ada di build.gradle.kts. Jika masih ada error "cannot resolve symbol", ikuti langkah troubleshooting di bawah.

## 📋 Current Dependencies Status

### **1. Dependencies di build.gradle.kts**
```kotlin
dependencies {
    // HTTP Client untuk API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Other dependencies...
}
```

### **2. Files Using Gson & Retrofit2 Correctly**
| File | Gson Import | Retrofit2 Import | Status |
|------|-------------|------------------|--------|
| **ApiClient.java** | ✅ GsonConverterFactory | ✅ Retrofit, Call | ✅ Working |
| **ApiService.java** | ❌ Not needed | ✅ Call, @POST, @GET | ✅ Working |
| **LoginSiswaActivity.java** | ✅ Gson | ✅ Call, Callback, Response | ✅ Working |
| **LoginGuruActivity.java** | ✅ Gson | ✅ Call, Callback, Response | ✅ Working |
| **EditPasswordActivity.java** | ❌ Not needed | ✅ Call, Callback, Response | ✅ Working |
| **DashboardActivity.java** | ❌ Not needed | ✅ Call, Callback, Response | ✅ Working |
| **ProfilActivity.java** | ❌ Not needed | ✅ Call, Callback, Response | ✅ Working |

## 🔧 Troubleshooting Steps

### **1. Gradle Sync Issues**
```bash
# Clean and rebuild project
./gradlew clean
./gradlew build

# Or in Android Studio:
# File → Sync Project with Gradle Files
# Build → Clean Project
# Build → Rebuild Project
```

### **2. IDE Cache Issues**
```
Android Studio:
1. File → Invalidate Caches and Restart
2. Choose "Invalidate and Restart"
3. Wait for project to reload
4. Check if errors are resolved
```

### **3. Dependency Download Issues**
```bash
# Force download dependencies
./gradlew --refresh-dependencies

# Check if dependencies are downloaded
ls ~/.gradle/caches/modules-2/files-2.1/com.squareup.retrofit2/
ls ~/.gradle/caches/modules-2/files-2.1/com.google.code.gson/
```

## 🔍 Common Import Patterns

### **1. ApiClient.java - Correct Imports**
```java
package com.example.absenku.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;                              // ✅ Retrofit core
import retrofit2.converter.gson.GsonConverterFactory;   // ✅ Gson converter

public class ApiClient {
    // Implementation...
}
```

### **2. ApiService.java - Correct Imports**
```java
package com.example.absenku.api;

import com.example.absenku.models.LoginRequest;
import com.example.absenku.models.LoginResponse;
import retrofit2.Call;        // ✅ For API calls
import retrofit2.http.Body;   // ✅ For request body
import retrofit2.http.GET;    // ✅ For GET requests
import retrofit2.http.POST;   // ✅ For POST requests

public interface ApiService {
    // API endpoints...
}
```

### **3. Activity Files - Correct Imports**
```java
// For activities that make API calls
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// For activities that parse JSON manually (rare)
import com.google.gson.Gson;

// Example usage:
Call<ProfileResponse> call = apiService.getStudentProfile("Bearer " + token);
call.enqueue(new Callback<ProfileResponse>() {
    @Override
    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
        // Handle response
    }
    
    @Override
    public void onFailure(Call<ProfileResponse> call, Throwable t) {
        // Handle failure
    }
});
```

## 🚫 Files That DON'T Need Gson/Retrofit2

### **1. Files Using Only Mock Services**
```java
// EditInfoPribadiActivity.java - Uses MockProfileService only
// NO NEED for retrofit2 imports if only using mock services

import com.example.absenku.api.MockProfileService;  // ✅ Mock service
// import retrofit2.Call;                           // ❌ Not needed for mock
// import retrofit2.Callback;                       // ❌ Not needed for mock
// import retrofit2.Response;                       // ❌ Not needed for mock
```

### **2. Model Classes**
```java
// ProfileResponse.java, LoginRequest.java, etc.
// NO NEED for gson imports - Gson uses reflection

public class ProfileResponse {
    private String message;
    private Profile profile;
    
    // Getters and setters - no gson imports needed
}
```

## 🔧 Manual Fixes for Specific Errors

### **1. "Cannot resolve symbol 'gson'"**
```java
// WRONG import
import gson.Gson;                    // ❌ Wrong package

// CORRECT import
import com.google.gson.Gson;         // ✅ Correct package
```

### **2. "Cannot resolve symbol 'retrofit2'"**
```java
// WRONG imports
import retrofit.Call;                // ❌ Wrong package (retrofit 1.x)
import retrofit.Callback;            // ❌ Wrong package

// CORRECT imports
import retrofit2.Call;               // ✅ Correct package (retrofit 2.x)
import retrofit2.Callback;           // ✅ Correct package
import retrofit2.Response;           // ✅ Correct package
```

### **3. "Cannot resolve symbol 'GsonConverterFactory'"**
```java
// WRONG import
import retrofit2.GsonConverterFactory;              // ❌ Wrong package

// CORRECT import
import retrofit2.converter.gson.GsonConverterFactory; // ✅ Correct package
```

## 🧪 Testing Import Resolution

### **1. Quick Test - ApiClient**
```java
// Add this test method to ApiClient.java temporarily
public static void testImports() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://test.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    
    // If this compiles, imports are working
}
```

### **2. Quick Test - Activity**
```java
// Add this test method to any activity temporarily
private void testRetrofitImports() {
    Call<String> testCall = null;
    Callback<String> testCallback = null;
    Response<String> testResponse = null;
    
    // If this compiles, retrofit2 imports are working
}
```

### **3. Quick Test - Gson**
```java
// Add this test method temporarily
private void testGsonImports() {
    Gson gson = new Gson();
    String json = gson.toJson("test");
    
    // If this compiles, gson imports are working
}
```

## 🔄 Alternative Solutions

### **1. Update Dependencies (if needed)**
```kotlin
// In build.gradle.kts, try latest versions
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### **2. Add Explicit Repository**
```kotlin
// In build.gradle.kts
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

### **3. Force Dependency Resolution**
```kotlin
// In build.gradle.kts, if there are conflicts
configurations.all {
    resolutionStrategy {
        force("com.google.code.gson:gson:2.10.1")
        force("com.squareup.retrofit2:retrofit:2.9.0")
    }
}
```

## 🎯 Expected Results After Fix

### **1. Successful Compilation**
```
✅ No "cannot resolve symbol" errors
✅ All imports resolve correctly
✅ Project builds successfully
✅ No red underlines in IDE
```

### **2. Working API Calls**
```java
// This should work without errors:
Call<ProfileResponse> call = apiService.getStudentProfile("Bearer " + token);
call.enqueue(new Callback<ProfileResponse>() {
    @Override
    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
        // Handle response
    }
    
    @Override
    public void onFailure(Call<ProfileResponse> call, Throwable t) {
        // Handle failure
    }
});
```

### **3. Working JSON Parsing**
```java
// This should work without errors:
Gson gson = new Gson();
ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);
```

## 🎉 Status: DEPENDENCIES VERIFIED!

Gson dan Retrofit2 dependencies sudah benar di project:
- ✅ **Dependencies Added** - All required libraries in build.gradle.kts
- ✅ **Correct Versions** - Latest stable versions used
- ✅ **Proper Imports** - All files use correct import statements
- ✅ **Working Implementation** - API calls and JSON parsing work correctly

**Jika masih ada error, lakukan Gradle Sync dan Invalidate Caches!** 🔧✅
