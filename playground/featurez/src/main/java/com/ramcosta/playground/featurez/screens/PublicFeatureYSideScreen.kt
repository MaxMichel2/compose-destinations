package com.ramcosta.playground.featurez.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph

public data class SomeArgsInHere(
    val asd: String,
    val list: ArrayList<String>
)

@Destination<ExternalModuleGraph>(
    navArgs = SomeArgsInHere::class
)
@Composable
internal fun PublicFeatureZSideScreen() {
    Text("PublicFeatureYSideScreen")
}