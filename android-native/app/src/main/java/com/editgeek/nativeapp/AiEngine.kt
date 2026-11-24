package com.editgeek.nativeapp

/**
 * Inference boundary for future on-device and consent-based remote implementations.
 * No image is uploaded by this default implementation.
 */
sealed interface AiEngineState {
    data object LocalModelNotInstalled : AiEngineState
    data object RemoteEndpointNotConfigured : AiEngineState
}

class AiEngine {
    fun currentState(): AiEngineState = AiEngineState.LocalModelNotInstalled

    fun unavailableMessage(): String =
        "AI inference is not connected yet. Your photo remains on this device; no upload was made."
}
