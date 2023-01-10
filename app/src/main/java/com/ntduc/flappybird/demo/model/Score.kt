package com.ntduc.flappybird.demo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Score(
    var score: Int = 0,
    var scoringTube: Int = 0,
    var scoringCoin: Boolean = true
) : Parcelable