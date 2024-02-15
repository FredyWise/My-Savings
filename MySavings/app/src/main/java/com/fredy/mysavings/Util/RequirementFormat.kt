package com.fredy.mysavings.Util

fun isValidLogin(
    email: String, password: String
): Boolean {
    return isValidEmail(email) && isValidPassword(password)
}

fun isValidPassword(password: String):Boolean{
    val isPasswordLengthValid = password.length >= 8
//    val containsUppercase = password.any { it.isUpperCase() }
//    val containsLowercase = password.any { it.isLowerCase() }
//    val containsDigit = password.any { it.isDigit() }
//    val containsSpecialChar = password.any { it.isLetterOrDigit().not() }
//
//    return isPasswordLengthValid && containsUppercase && containsLowercase && containsDigit && containsSpecialChar
    return isPasswordLengthValid
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
    return email.matches(emailRegex)
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    // Regular expression for a simple phone number format
    val isNumberLengthValid = phoneNumber.length >= 7
    val phoneRegex = Regex("^\\+?[0-9-]+\$")

    return phoneNumber.matches(phoneRegex) && isNumberLengthValid
}

fun isValidEmailOrPhone(emailOrPhoneNumber:String):Boolean{
    return isValidEmail(emailOrPhoneNumber) || isValidPhoneNumber(emailOrPhoneNumber)
}

