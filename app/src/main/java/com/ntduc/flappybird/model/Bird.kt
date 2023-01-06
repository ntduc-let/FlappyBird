package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bird(
    var id: Int,
    var bird1Res: Int,
    var bird2Res: Int,
    var birdX: Float = 0f,
    var birdY: Float = 0f,
    var velocity: Int = 0,
    var gravity: Int = 2
) : Parcelable