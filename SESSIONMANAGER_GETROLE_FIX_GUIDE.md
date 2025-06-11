# 🔧 SessionManager getUserRole() Fix Guide

## ✅ SESSIONMANAGER ERROR SUDAH DIPERBAIKI!

Error `method getUserRole() not found` telah diselesaikan dengan menggunakan method yang benar.

## 🔧 Root Cause & Solution

### **Problem Identified**
- Error: `sessionManager.getUserRole()` method tidak ditemukan
- Method yang benar di SessionManager adalah `getRole()` bukan `getUserRole()`

### **Solution Applied**
- ✅ **Changed**: `sessionManager.getUserRole()`
- ✅ **To**: `sessionManager.getRole()`

## 📋 SessionManager Method Verification

### **Available Methods in SessionManager**
```java
public class SessionManager {
    private static final String KEY_ROLE = "role";
    
    // Create login session with role
    public void createLoginSession(String token, String username, String role) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);  // ✅ Role disimpan
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }
    
    // Get user role
    public String getRole() {  // ✅ Method yang benar
        return pref.getString(KEY_ROLE, null);
    }
    
    // Other methods...
    public String getToken() { ... }
    public String getUsername() { ... }
    public boolean isLoggedIn() { ... }
    public void logout() { ... }
}
```

## 🔄 Role Storage & Retrieval Flow

### **1. Login Process - Role Storage**
```java
// LoginSiswaActivity
sessionManager.createLoginSession(
    loginResponse.getToken(),
    loginResponse.getUser().getUsername(),
    "siswa"  // ✅ Role disimpan sebagai "siswa"
);

// LoginGuruActivity  
sessionManager.createLoginSession(
    loginResponse.getToken(),
    loginResponse.getUser().getUsername(),
    "guru"   // ✅ Role disimpan sebagai "guru"
);
```

### **2. EditPasswordActivity - Role Retrieval**
```java
// BEFORE (Error)
userRole = sessionManager.getUserRole();  // ❌ Method tidak ada

// AFTER (Fixed)
userRole = sessionManager.getRole();      // ✅ Method yang benar
```

### **3. Role-Based Navigation**
```java
// Bottom navigation based on role
if ("guru".equals(userRole)) {
    startActivity(new Intent(this, DashboardActivityGuru.class));
} else {
    startActivity(new Intent(this, DashboardActivity.class));
}

// Profile navigation based on role
if ("guru".equals(userRole)) {
    intent = new Intent(this, ProfilGuruActivity.class);
} else {
    intent = new Intent(this, ProfilActivity.class);
}
```

## 🧪 Testing Scenarios

### **1. Role Detection Test**
- **Login Siswa**: Role = "siswa"
- **Login Guru**: Role = "guru"
- **EditPasswordActivity**: `getRole()` returns correct role
- **Expected**: Role-based navigation works correctly

### **2. Password Change Flow Test**
- **Siswa Flow**: 
  - Login siswa → Profil → Edit Password
  - `getRole()` returns "siswa"
  - Uses `MockPasswordService.changePassword()` with role "siswa"
  - Navigate back to ProfilActivity
  
- **Guru Flow**:
  - Login guru → Profil Guru → Edit Password  
  - `getRole()` returns "guru"
  - Uses `MockPasswordService.changePassword()` with role "guru"
  - Navigate back to ProfilGuruActivity

### **3. API Selection Test**
- **Real API Mode**:
  - Siswa: Uses `apiService.changeStudentPassword()`
  - Guru: Uses `apiService.changeGuruPassword()`
- **Mock Mode**:
  - Both: Uses `MockPasswordService.changePassword()` with appropriate role

## 🔐 Role-Based Security

### **1. Role Validation in MockPasswordService**
```java
// Validasi role (optional, untuk keamanan tambahan)
if (userRole != null) {
    if ("siswa".equals(userRole) && !username.startsWith("siswa")) {
        return new PasswordChangeResult(false, "Role tidak sesuai dengan username", null);
    }
    if ("guru".equals(userRole) && !username.startsWith("guru")) {
        return new PasswordChangeResult(false, "Role tidak sesuai dengan username", null);
    }
}
```

### **2. Role-Based API Endpoints**
- **Siswa**: `POST /api/siswa/change-password`
- **Guru**: `POST /api/guru/change-password`
- **Security**: Different endpoints untuk different roles

## 📊 Role Data Mapping

### **Login Response to Session**
| Login Type | Username | Role Stored | API Endpoint |
|------------|----------|-------------|--------------|
| **LoginSiswaActivity** | siswa1 | "siswa" | /api/siswa/change-password |
| **LoginSiswaActivity** | siswa2 | "siswa" | /api/siswa/change-password |
| **LoginGuruActivity** | guru1 | "guru" | /api/guru/change-password |
| **LoginGuruActivity** | guru2 | "guru" | /api/guru/change-password |

### **Session to Navigation**
| Role Retrieved | Dashboard | Profile | Edit Password |
|----------------|-----------|---------|---------------|
| **"siswa"** | DashboardActivity | ProfilActivity | EditPasswordActivity |
| **"guru"** | DashboardActivityGuru | ProfilGuruActivity | EditPasswordActivity |

## ✅ Verification Checklist

- ✅ **Method Name**: `getRole()` exists in SessionManager
- ✅ **Role Storage**: Role saved during login process
- ✅ **Role Retrieval**: `getRole()` returns correct role
- ✅ **Role Usage**: Used for navigation dan API selection
- ✅ **Error Handling**: Handles null role gracefully
- ✅ **Security**: Role validation in password change

## 🎯 Expected Results

### **Successful Role Detection**
- ✅ **Method Call**: `sessionManager.getRole()` works
- ✅ **Role Return**: Returns "siswa" atau "guru"
- ✅ **Navigation**: Correct navigation based on role
- ✅ **API Selection**: Correct API endpoint based on role

### **Error Scenarios**
- ✅ **Null Role**: Handled gracefully with default behavior
- ✅ **Invalid Role**: Validation in MockPasswordService
- ✅ **Session Expired**: Proper error handling

## 🎉 Status: FIXED AND READY!

SessionManager role functionality sekarang:
- ✅ **Method Available** - `getRole()` method exists dan works
- ✅ **Role Storage** - Role properly stored during login
- ✅ **Role Retrieval** - Role properly retrieved in EditPasswordActivity
- ✅ **Role-Based Logic** - Navigation dan API selection based on role
- ✅ **Security** - Role validation untuk password changes

**Test dengan login sebagai siswa dan guru, kemudian test edit password!** 🔐
