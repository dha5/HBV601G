package com.hbv601.folf.Entities

class UserEntity(val userName:String, private var password: String) {
    //implement authentication and encoding, no plaintext passwords anywhere
    fun comparePassword(password: String): Boolean{
        if(password == this.password){
            return true
        }
        return false
    }
    fun setNewPassword(password: String){
        this.password = password
    }
}