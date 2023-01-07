package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var uid: String? = null,
    var bird: Bird? = null,
    var coin: Coin? = null,
    var score: Score? = null,
    var tube: Tube? = null
) : Parcelable