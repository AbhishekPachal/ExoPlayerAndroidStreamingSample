package com.digitstory.exoplayersmaple;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends Activity {
    SimpleExoPlayer player;
    SimpleExoPlayerView playerView;
    private BandwidthMeter bandwidthMeter;
    private DataSource.Factory mediaDataSourceFactory;
    private TrackSelector trackSelector;
    private static final String VIDEO_DASH_URL = "https://bitmovin-a.akamaihd.net/content/playhouse-vr/mpds/105560.mpd";
    private static long SEEK_POSITION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.playerView);
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerSample"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    /**
     * Method to initialize player
     */
    private void initializePlayer() {
        try {
            if (player == null) {
                playerView.setPlayer(player);
                Uri uri = Uri.parse(VIDEO_DASH_URL);
                //Building data source
                DashMediaSource dashMediaSource = new DashMediaSource(uri, mediaDataSourceFactory,
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), null, null);
                //Setting Track selector
                trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                //Creating exo-player instance
                player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
                playerView.setPlayer(player);
                //Performing play event when source is ready to play
                player.setPlayWhenReady(true);
                player.prepare(dashMediaSource);
            }
            player.seekTo(SEEK_POSITION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to release Video player's when exiting form this activity
     */
    private void releasePlayer() {
        try {
            if (player != null) {
                SEEK_POSITION = player.getCurrentPosition();
                player.release();
                player = null;
                trackSelector = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
