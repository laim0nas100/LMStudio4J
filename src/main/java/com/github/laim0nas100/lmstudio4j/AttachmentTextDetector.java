package com.github.laim0nas100.lmstudio4j;

import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;

/**
 *
 * @author Lemmin
 */
public class AttachmentTextDetector {

    private static final Tika TIKA = new Tika();

    public record TextDetectionResult(String filename, String detectedMimeType, String initialMimeType, boolean isText) {}

    public static TextDetectionResult detectText(TikaInputStream inputStream, String filename, String providedMimeType) throws Exception {

        inputStream.enableRewind(Defaults.MEMORY_BUDGET.get());
        String detectedType = TIKA.detect(inputStream);

        inputStream.rewind();
        boolean text = detectedType.startsWith("text/")
                || detectedType.contains("json")
                || detectedType.contains("xml")
                || detectedType.contains("javascript")
                || (providedMimeType != null && providedMimeType.startsWith("text/"));

        return new TextDetectionResult(filename, detectedType, providedMimeType, text);
    }

}
