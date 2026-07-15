import { useState } from 'react';
import axios from 'axios';
import { Search, Sparkles } from 'lucide-react';
import VideoRow from './components/VideoRow';

function App() {
  const [query, setQuery] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [aiAnalysis, setAiAnalysis] = useState(null);
  
  // Normalized Data States
  const [liveResults, setLiveResults] = useState([]);
  const [semanticResults, setSemanticResults] = useState([]);

 const handleSearch = async (e) => {
  e.preventDefault();
  if (!query.trim()) return;

  setIsSearching(true);
  setAiAnalysis(null);
  
  // Clear out old results instantly so they don't sit on screen
  setLiveResults([]);
  setSemanticResults([]);

  try {
    // Step 1: Harvest and write new data to DB
    const discoverRes = await axios.get(`http://localhost:8080/api/v1/search/discover?query=${query}`);
    setAiAnalysis(discoverRes.data.searchAnalysis);

    const formattedLive = discoverRes.data.harvestedVideos.map(v => ({
      id: v.id.videoId,
      title: v.snippet.title,
      channel: v.snippet.channelTitle
    }));
    setLiveResults(formattedLive);

    // Step 2: Give MongoDB Atlas a 3-second window to finish building the vector graph
    await new Promise(resolve => setTimeout(resolve, 3000));

    // Step 3: Query the updated vector index
    const semanticRes = await axios.get(`http://localhost:8080/api/v1/search/semantic?query=${query}`);
    
    const formattedSemantic = semanticRes.data.results.map(v => ({
      id: v.videoId,
      title: v.content.split('\n')[0].replace('Title: ', ''),
      channel: v.channel,
      vibeMatchScore: v.vibeMatchScore
    }));
    setSemanticResults(formattedSemantic);

  } catch (error) {
    console.error("Pipeline Sync Error:", error);
  } finally {
    setIsSearching(false);
  }
};

  return (
    <div className="min-h-screen bg-[#0b0f19] text-white overflow-hidden relative">
      {/* Background AI Glow Blob (Zero-lag aesthetic) */}
      <div className="absolute top-[-20%] left-[-10%] w-[50%] h-[50%] bg-purple-900/20 blur-[120px] rounded-full pointer-events-none" />
      <div className="absolute bottom-[-20%] right-[-10%] w-[40%] h-[40%] bg-blue-900/20 blur-[120px] rounded-full pointer-events-none" />

      {/* Header & Search */}
      <header className="pt-12 pb-8 px-4 flex flex-col items-center relative z-10">
        <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight mb-8 flex items-center gap-3">
          <span className="bg-linear-to-r from-purple-400 to-blue-500 bg-clip-text text-transparent">
            Youtube AI
          </span>
          <Sparkles className="text-blue-400 h-8 w-8" />
        </h1>

        <form onSubmit={handleSearch} className="w-full max-w-2xl relative group">
          <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-slate-400 group-focus-within:text-purple-500 transition-colors" />
          </div>
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Describe a vibe, genre, or creator..."
            className="w-full bg-slate-900/80 border border-slate-700 text-white rounded-full py-4 pl-12 pr-6 shadow-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent backdrop-blur-md transition-all text-lg"
          />
          <button 
            type="submit"
            disabled={isSearching}
            className="absolute right-2 top-2 bottom-2 bg-linear-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 rounded-full px-6 font-semibold transition-all shadow-lg shadow-purple-500/20 disabled:opacity-50"
          >
            {isSearching ? 'Analyzing...' : 'Discover'}
          </button>
        </form>
      </header>

      {/* Main Content Area */}
      <main className="relative z-10 pb-20">
        
        {/* Loading State */}
        {isSearching && (
          <div className="flex flex-col items-center justify-center mt-20 animate-pulse text-purple-400">
            <Sparkles className="h-10 w-10 mb-4 animate-bounce" />
            <p className="text-lg font-medium">Extracting semantic meaning & harvesting videos...</p>
          </div>
        )}

        {/* AI Intent Display */}
        {!isSearching && aiAnalysis && (
          <div className="max-w-4xl mx-auto px-4 mb-10">
            <div className="bg-slate-800/50 border border-slate-700 rounded-2xl p-6 backdrop-blur-sm">
              <h3 className="text-sm uppercase tracking-widest text-slate-400 font-semibold mb-3 flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-purple-400" /> AI Intent Breakdown
              </h3>
              <div className="flex flex-wrap gap-3">
                <span className="px-4 py-1.5 bg-slate-900 rounded-full text-sm font-medium border border-slate-700">
                  Target: <span className="text-blue-400">{aiAnalysis.targetContent}</span>
                </span>
                <span className="px-4 py-1.5 bg-slate-900 rounded-full text-sm font-medium border border-slate-700">
                  Vibe: <span className="text-purple-400">{aiAnalysis.coreVibe}</span>
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Results Rows */}
        {!isSearching && (
          <>
            <VideoRow title="Live Harvest (YouTube API)" videos={liveResults} />
            <VideoRow title="Semantic Memory (MongoDB Vector Match)" videos={semanticResults} />
          </>
        )}
      </main>
    </div>
  );
}

export default App;