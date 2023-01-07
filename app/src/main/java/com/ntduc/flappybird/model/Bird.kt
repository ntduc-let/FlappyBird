package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bird(
    var id: Int = 0,
    var bird1Res: Int = 0,
    var bird2Res: Int = 0,
    var birdX: Float = 0f,
    var birdY: Float = 0f,
    var velocity: Int = 0,
    var gravity: Int = 2
) : Parcelable