package com.measton.dogapp

import kotlin.native.Platform
import kotlin.experimental.ExperimentalNativeApi

@OptIn( ExperimentalNativeApi::class)
actual val isDebugBuild: Boolean = Platform.isDebugBinary