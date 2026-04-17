package com.example.vlc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.example.vlc.databinding.ActivityMainBinding;

import org.videolan.libvlc.FactoryManager;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.interfaces.ILibVLC;
import org.videolan.libvlc.interfaces.ILibVLCFactory;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.media.VideoView;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'vlc' library on application startup.
    static {
        System.loadLibrary("myvlc");
    }

    LibVLC libVLC;
    MediaPlayer mediaPlayer;
    VLCVideoLayout videoLayout;
    Media media;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String path = getExternalCacheDir().getAbsolutePath();

        List<String> options = new ArrayList<String>();
        options.add("--audio-filter=scaletempo_pitch");
        options.add("--pitch-shift=12");
        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);

//        mediaPlayer.setRate(2.3f);
        videoLayout = binding.videoLayout;
        mediaPlayer.attachViews(videoLayout, null, false, false);

        File file = new File(path + "/crypt.mp4");
        try {
            FileInputStream fis = new FileInputStream(file);
            FileDescriptor fd = fis.getFD();
            long len = file.length();

            media = new Media(libVLC, fd, 0, len);

            // Enable the pitch-shifting audio filter
//            media.addOption(":audio-filter=scaletempo_pitch");
//
//            // Set the pitch shift value (e.g., to +5 semitones)
//            // The value can be adjusted from -12 to 12
//            media.addOption(":pitch-shift=12");
//
//            // Optional: add other low-latency or performance options if needed
//            media.addOption(":network-caching=300");
//            media.addOption(":clock-jitter=0");


            mediaPlayer.setMedia(media);
            mediaPlayer.play();

            media.release();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
        mediaPlayer.detachViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        libVLC.release();
    }

    /**
     * A native method that is implemented by the 'vlc' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}