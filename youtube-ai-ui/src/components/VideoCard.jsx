import React from 'react';

const VideoCard = ({ video }) => {
  const thumbnailUrl = `https://i.ytimg.com/vi/${video.id}/hqdefault.jpg`;

  return (
    <a 
      href={`https://www.youtube.com/watch?v=${video.id}`}
      target="_blank" 
      rel="noopener noreferrer"
      className="flex-none w-64 md:w-80 group relative rounded-xl overflow-hidden bg-slate-800 transition-all duration-300 hover:scale-105 hover:ring-2 hover:ring-purple-500 hover:shadow-[0_0_20px_rgba(168,85,247,0.4)]"
    >
      <div className="aspect-video w-full relative overflow-hidden">
        <img 
          src={thumbnailUrl} 
          alt={video.title} 
          className="w-full h-full object-cover transition-opacity duration-300 group-hover:opacity-80"
        />
        
        {/* Glowing AI Score Badge (Rendered only on the Semantic row items) */}
        {video.vibeMatchScore && (
          <div className="absolute top-2 left-2 bg-slate-900/90 text-purple-400 text-xs font-bold px-2.5 py-1 rounded-md border border-purple-500/30 backdrop-blur-sm shadow-md">
            ✨ {video.vibeMatchScore}% Match
          </div>
        )}

        <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
          <div className="bg-purple-600/80 p-3 rounded-full backdrop-blur-sm">
            <svg className="w-8 h-8 text-white ml-1" fill="currentColor" viewBox="0 0 24 24">
              <path d="M8 5v14l11-7z" />
            </svg>
          </div>
        </div>
      </div>

      <div className="p-3">
        <h3 className="text-sm font-semibold text-slate-100 line-clamp-2 leading-tight">
          {video.title}
        </h3>
        <p className="text-xs text-slate-400 mt-1 font-medium tracking-wide">
          {video.channel}
        </p>
      </div>
    </a>
  );
};

export default VideoCard;