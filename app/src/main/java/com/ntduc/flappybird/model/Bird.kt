package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bird(
    var style: Int,
    var bird1: Int,
    var bird2: Int,
    var birdX: Float = 0f,
    var birdY: Float = 0f
) : Parcelable