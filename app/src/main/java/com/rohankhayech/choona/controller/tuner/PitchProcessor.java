/*
 *      _______                       _____   _____ _____
 *     |__   __|                     |  __ \ / ____|  __ \
 *        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
 *        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/
 *        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |
 *        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|
 *
 * -------------------------------------------------------------
 *
 * TarsosDSP is developed by Joren Six at IPEM, University Ghent
 *
 * -------------------------------------------------------------
 *
 *  Info: http://0110.be/tag/TarsosDSP
 *  Github: https://github.com/JorenSix/TarsosDSP
 *  Releases: http://0110.be/releases/TarsosDSP/
 *
 *  TarsosDSP includes modified source code by various authors,
 *  for credits and info, see README.
 *
 */


package com.rohankhayech.choona.controller.tuner;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchDetector;


/**
 * Modified copy of PitchProcessor from TarsosDSP allowing a pitch detector object to be passed in.
 *
 * Is responsible to call a pitch estimation algorithm. It also calculates progress.
 * The underlying pitch detection algorithm must implement the {@link PitchDetector} interface.
 * @author Joren Six
 */
class PitchProcessor implements AudioProcessor {

    /**
     * The underlying pitch detector;
     */
    private final PitchDetector detector;

    private final PitchDetectionHandler handler;

    public PitchProcessor(PitchDetector detector, PitchDetectionHandler handler) {
        this.detector = detector;
        this.handler = handler;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();

        PitchDetectionResult result = detector.getPitch(audioFloatBuffer);


        handler.handlePitch(result,audioEvent);
        return true;
    }

    @Override
    public void processingFinished() {
    }
}
