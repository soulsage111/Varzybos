package com.app.varzybos

sealed class Screen(val route: String) {

    object Renginiai: Screen("Renginiai")
    object ManoRenginiai: Screen("Mano renginiai")
    object Pranesimai: Screen("Pranešimai")
}