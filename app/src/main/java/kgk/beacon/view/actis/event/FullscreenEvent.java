package kgk.beacon.view.actis.event;

import android.widget.ImageButton;

/**
 * Событие входа/выхода в полноэкранный режим
 */
public class FullscreenEvent {

    private ImageButton fullscreenButton;

    public ImageButton getFullscreenButton() {
        return fullscreenButton;
    }

    public void setFullscreenButton(ImageButton fullscreenButton) {
        this.fullscreenButton = fullscreenButton;
    }
}
