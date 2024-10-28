import React from "react";

const VideoPlayer = ({ videoId }: { videoId: string }) => {
  return (
    <div>
      <video
        controls
        style={{ width: "100%", height: "auto" }}
        src={process.env.NEXT_PUBLIC_STREAM_IP + videoId}
      />
    </div>
  );
};

export default VideoPlayer;
