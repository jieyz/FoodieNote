package tv.danmaku.ijk.media.player;

import android.content.Context;
import android.view.Surface;

import com.yaozu.video.player.IMediaPlayer;

/**
 * @author bbcallen
 * 
 *         Optional interface default implements
 */
public abstract class BaseMediaPlayer implements IMediaPlayer {

    @Override
    public void setAudioStreamType(int streamtype) {
    }

    @Override
    public void setWakeMode(Context context, int mode) {
        return;
    }

    @Override
    public void setSurface(Surface surface) {
    }
}
