package com.app.varzybos

sealed class AdminScreen(val route: String) {

    object Renginiai: AdminScreen("Renginiai")
    object Vartotojai: AdminScreen("Vartotojai")
    object Pranesimai: AdminScreen("Pranešimai")
}