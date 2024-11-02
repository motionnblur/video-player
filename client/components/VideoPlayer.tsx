import React from "react";

const VideoPlayer = ({ videoId }: { videoId: string }) => {
  return (
    <div>
      <video
        controls
        style={{ width: "100%", height: "auto" }}
        src={
          `${process.env.NEXT_PUBLIC_BACKEND_IP}/api/videos/stream/` + videoId
        }
      />
    </div>
  );
};

export default VideoPlayer;
