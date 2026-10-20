package com.example.arcfind.model

enum class SolveType(val title: String, val subtitle: String) {
    FIND_ARC_LENGTH("Arc Length", "Radius + Angle"),
    FIND_RADIUS("Radius", "Arc Length + Angle"),
    FIND_ANGLE("Angle", "Arc Length + Radius")
}
