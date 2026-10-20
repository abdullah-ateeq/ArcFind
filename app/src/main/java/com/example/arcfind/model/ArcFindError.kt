package com.example.arcfind.model

sealed interface ArcFindError {
    data object EmptyInput : ArcFindError
    data object InvalidNumber : ArcFindError
    data object NonPositiveRadius : ArcFindError
    data object NonPositiveArcLength : ArcFindError
    data object InvalidAngle : ArcFindError
    data object AngleExceedsFullCircle : ArcFindError
    data object InputTooLong : ArcFindError
}
