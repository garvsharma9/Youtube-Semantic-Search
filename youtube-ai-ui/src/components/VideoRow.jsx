import React from 'react';
import VideoCard from './VideoCard';

const VideoRow = ({ title, videos }) => {
  if (!videos || videos.length === 0) return null;

  return (
    <div className="my-8 w-full">
      <h2 className="text-xl md:text-2xl font-bold text-white mb-4 pl-4 md:pl-12 border-l-4 border-purple-500">
        {title}
      </h2>
      
      {/* The Scrollable Row */}
      <div className="flex overflow-x-auto gap-4 px-4 md:px-12 pb-6 scrollbar-hide snap-x">
        {videos.map((video, index) => (
          <div key={`${video.id}-${index}`} className="snap-start">
            <VideoCard video={video} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default VideoRow;