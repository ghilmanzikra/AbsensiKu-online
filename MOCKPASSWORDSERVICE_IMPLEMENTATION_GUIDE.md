# 🔐 MockPasswordService Implementation Guide

## ✅ MOCKPASSWORDSERVICE SUDAH DIIMPLEMENTASI!

MockPasswordService telah dibuat untuk mengatasi error import dan menyediakan functionality change password.

## 🔧 Technical Implementation

### **1. MockPasswordService Features**
- ✅ **Password Storage**: In-memory HashMap untuk menyimpan password users
- ✅ **Change Password**: Method untuk mengubah password dengan validasi
- ✅ **Password Verification**: Method untuk verifikasi password
- ✅ **Role Validation**: Validasi role siswa/guru
- ✅ **Error Handling**: Comprehensive error handling

### **2. Mock Database Structure**
```java
private static final Map<String, String> MOCK_PASSWORDS = new HashMap<>();

static {
    // Siswa passwords
    MOCK_PASSWORDS.put("siswa1", "siswa1");
    MOCK_PASSWORDS.put("siswa2", "siswa2");
    MOCK_PASSWORDS.put("siswa3", "siswa3");
    
    // Guru passwords
    MOCK_PASSWORDS.put("guru1", "guru1");
    MOCK_PASSWORDS.put("guru2", "guru2");
    MOCK_PASSWORDS.put("guru3", "guru3");
}
```

### **3. Change Password Method**
```java
public static PasswordChangeResult changePassword(String username, String oldPassword, String newPassword, String userRole) {
    // Validasi input
    // Cek password lama
    // Update password
    // Return result
}
```

## 🔐 Security Features

### **1. Input Validation**
- ✅ **Required Fields**: Username, old password, new password tidak boleh kosong
- ✅ **Minimum Length**: Password baru minimal 6 karakter
- ✅ **Different Password**: Password baru harus berbeda dari lama
- ✅ **Role Validation**: Username harus sesuai dengan role

### **2. Password Verification**
- ✅ **Old Password Check**: Verifikasi password lama sebelum update
- ✅ **User Existence**: Cek apakah user ada di database
- ✅ **Role Consistency**: Validasi role sesuai username pattern

### **3. Error Scenarios**
- ✅ **Invalid Username**: Username null atau kosong
- ✅ **Wrong Old Password**: Password lama salah
- ✅ **Short Password**: Password baru kurang dari 6 karakter
- ✅ **Same Password**: Password baru sama dengan lama
- ✅ **User Not Found**: Username tidak ada di database
- ✅ **Role Mismatch**: Role tidak sesuai dengan username

## 🧪 Testing Data

### **Default Passwords**
| Username | Default Password | Role |
|----------|------------------|------|
| siswa1 | siswa1 | Siswa |
| siswa2 | siswa2 | Siswa |
| siswa3 | siswa3 | Siswa |
| guru1 | guru1 | Guru |
| guru2 | guru2 | Guru |
| guru3 | guru3 | Guru |

### **Test Scenarios**

#### **1. Successful Password Change**
- **Username**: siswa1
- **Old Password**: siswa1
- **New Password**: newpassword123
- **Expected**: Success, password updated

#### **2. Wrong Old Password**
- **Username**: siswa1
- **Old Password**: wrongpassword
- **New Password**: newpassword123
- **Expected**: Error "Password lama salah"

#### **3. Short New Password**
- **Username**: siswa1
- **Old Password**: siswa1
- **New Password**: 123
- **Expected**: Error "Password baru minimal 6 karakter"

#### **4. Same Password**
- **Username**: siswa1
- **Old Password**: siswa1
- **New Password**: siswa1
- **Expected**: Error "Password baru harus berbeda dengan password lama"

#### **5. Role Mismatch**
- **Username**: siswa1
- **Role**: guru
- **Expected**: Error "Role tidak sesuai dengan username"

## 🔄 API Integration

### **1. Model Classes Created**
- ✅ **ChangePasswordRequest**: Request model dengan old_password dan new_password
- ✅ **ChangePasswordResponse**: Response model dengan message, success, data
- ✅ **ApiService**: Added changeStudentPassword() dan changeGuruPassword()

### **2. API Endpoints**
```java
@POST("api/siswa/change-password")
Call<ChangePasswordResponse> changeStudentPassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

@POST("api/guru/change-password")
Call<ChangePasswordResponse> changeGuruPassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
```

### **3. EditPasswordActivity Integration**
- ✅ **Mock Mode**: Uses MockPasswordService.changePassword()
- ✅ **Real API Mode**: Uses ApiService.changeStudentPassword() / changeGuruPassword()
- ✅ **Role Detection**: Automatic role-based API selection
- ✅ **Error Handling**: Comprehensive error handling untuk both modes

## 🎯 Usage Examples

### **1. Change Password (Mock)**
```java
MockPasswordService.PasswordChangeResult result = MockPasswordService.changePassword(
    "siswa1", 
    "siswa1", 
    "newpassword123", 
    "siswa"
);

if (result.success) {
    // Password changed successfully
} else {
    // Handle error: result.errorMessage
}
```

### **2. Verify Password**
```java
boolean isValid = MockPasswordService.verifyPassword("siswa1", "newpassword123");
```

### **3. Reset Password to Default**
```java
boolean reset = MockPasswordService.resetPasswordToDefault("siswa1");
```

## 🔧 Utility Methods

### **1. Password Verification**
- **Method**: `verifyPassword(username, password)`
- **Purpose**: Verify if password is correct for user
- **Return**: boolean

### **2. Get Current Password**
- **Method**: `getCurrentPassword(username)`
- **Purpose**: Get current password for user (testing only)
- **Return**: String password atau null

### **3. Reset Password**
- **Method**: `resetPasswordToDefault(username)`
- **Purpose**: Reset password to default (same as username)
- **Return**: boolean success

### **4. Get All Users**
- **Method**: `getAllUsers()`
- **Purpose**: Get all users dan passwords (debugging)
- **Return**: Map<String, String>

## 🎉 Benefits

### **1. Development**
- ✅ **No Backend Required**: Test change password tanpa real API
- ✅ **Predictable Data**: Known test data untuk consistent testing
- ✅ **Fast Testing**: Instant response tanpa network delay
- ✅ **Error Simulation**: Test berbagai error scenarios

### **2. Security**
- ✅ **Validation**: Comprehensive input validation
- ✅ **Role Security**: Role-based access control
- ✅ **Password Rules**: Enforce password complexity rules
- ✅ **Error Messages**: Clear error messages untuk user

### **3. Maintenance**
- ✅ **Clean Code**: Well-structured dan documented
- ✅ **Extensible**: Easy to add new features
- ✅ **Testable**: Easy to unit test
- ✅ **Debuggable**: Utility methods untuk debugging

## 🎯 Status: READY FOR TESTING!

MockPasswordService sekarang menyediakan:
- 🔐 **Complete Password Management** - Change, verify, reset passwords
- 🛡️ **Security Validation** - Comprehensive input dan role validation
- 🔄 **API Integration** - Ready untuk real API integration
- 🧪 **Testing Support** - Utility methods untuk testing dan debugging
- ✨ **Error Handling** - Robust error handling dengan clear messages

**Test change password functionality dengan berbagai scenarios!** 🔐
