package com.peru.wafflecoach;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class AdapterPagerProfile extends RecyclerView.Adapter<AdapterPagerProfile.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private Context context;
    private String url;
    String urlY;
    boolean directory;
    public AdapterPagerProfile(Context context, List<String> data,boolean directory) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.directory = directory;
    }
    public AdapterPagerProfile(Context context, List<String> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.directory = directory;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.page_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        url = mData.get(position);
        if(position == 0) {
            la.neurometrics.cobi.ServiceGlobal.loadImage(url, holder.imgUser);
            holder.lytVideoUser.setVisibility(View.GONE);
        }else{
            holder.imgUser.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = holder.lytVideoUser.getLayoutParams();
         //  YouTubePlayerView youTubePlayerView = new YouTubePlayerView(context);
            if(directory) {
                params.height = (int) context.getResources().getDimension(R.dimen.video_height);
                holder.youTubePlayerView.setLayoutParams(params);
            }
            holder.youTubePlayerView.setEnableAutomaticInitialization(false);
            holder.youTubePlayerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            holder.youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    urlY = la.neurometrics.cobi.ServiceGlobal.extractYoutubeVideoId(url);
                    youTubePlayer.cueVideo(urlY, (float) 0);

                }

                @Override
                public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState playerState) {
                    Log.d("TAGV", "onStateChange");
                }

                @Override
                public void onPlaybackQualityChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackQuality playbackQuality) {
                    Log.d("TAGV", "onPlaybackQualityChange");
                }

                @Override
                public void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {
                    Log.d("TAGV", "onPlaybackRateChange");

                }

                @Override
                public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError playerError) {
                  //  Log.d("TAGV", "onError");

                }

                @Override
                public void onCurrentSecond(YouTubePlayer youTubePlayer, float v) {
                  //  Log.d("TAGV", "onCurrentSecond");
                }


                @Override
                public void onVideoDuration(YouTubePlayer youTubePlayer, float v) {
                    Log.d("TAGV", "onVideoDuration");

                }

                @Override
                public void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float v) {
                    Log.d("TAGV", "onVideoLoadedFraction");

                }

                @Override
                public void onVideoId(YouTubePlayer youTubePlayer, String s) {
                    Log.d("TAGV", "onVideoLoadedFraction");

                }

                @Override
                public void onApiChange(YouTubePlayer youTubePlayer) {
                    Log.d("TAGV", "onApiChange");

                }
            });
          //  holder.lytVideoUser.addView(youTubePlayerView);

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        FrameLayout lytVideoUser;
        YouTubePlayerView youTubePlayerView;
        ViewHolder(View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_specialist);
            lytVideoUser = itemView.findViewById(R.id.lyt_vid_user);
            youTubePlayerView = itemView.findViewById(R.id.yt);
        }
    }
}
