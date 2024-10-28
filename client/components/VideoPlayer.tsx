import React, { useEffect, useState } from "react";
import axios from "axios";

const VideoPlayer = ({ videoId }: { videoId: string }) => {
  const [videoUrl, setVideoUrl] = useState("");

  useEffect(() => {
    const fetchVideo = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/api/videos/stream/${videoId}`
        );
        if (!response.ok) {
          throw new Error("Video not found");
        }
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        setVideoUrl(url);
      } catch (error) {
        console.error("Error fetching video:", error);
      }
    };

    fetchVideo();

    // Cleanup the URL.createObjectURL when the component unmounts
    return () => {
      if (videoUrl) {
        URL.revokeObjectURL(videoUrl);
      }
    };
  }, [videoId]);

  return (
    <div>
      {videoUrl ? (
        <video
          controls
          style={{ width: "100%", height: "auto" }}
          src={videoUrl}
        />
      ) : (
        <p>Loading video...</p>
      )}
    </div>
  );
};

export default VideoPlayer;
