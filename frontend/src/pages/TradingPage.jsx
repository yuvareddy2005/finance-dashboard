import React, { useState, useEffect } from 'react';
import apiService from '../services/apiService';
import StockChart from '../components/StockChart';
import TimeframeSelector from '../components/TimeframeSelector';
import TradePanel from '../components/TradePanel';
import StockStatsPanel from '../components/StockStatsPanel';

const tradingPageStyle = { display: 'flex', gap: '1.5rem', height: 'calc(100vh - 120px)' };
const stockListStyle = { flex: '0 0 300px', backgroundColor: '#2D3748', borderRadius: '8px', padding: '1rem', overflowY: 'auto' };
const stockListItemStyle = (isSelected) => ({ padding: '0.75rem', borderRadius: '6px', cursor: 'pointer', marginBottom: '0.5rem', backgroundColor: isSelected ? 'var(--primary-teal)' : 'transparent', color: isSelected ? '#1A202C' : 'var(--text-light)', fontWeight: isSelected ? 'bold' : 'normal' });

const mainContentStyle = { flex: 1, display: 'flex', flexDirection: 'column', gap: '1.5rem', overflowY: 'auto' };

// v-- THIS IS THE ONLY STYLE OBJECT THAT HAS CHANGED --v
const chartContainerStyle = {
  height: 'calc(100vh - 230px)', // Fill viewport height minus header/padding
  minHeight: '500px',             // Set a minimum height
  backgroundColor: '#2D3748',
  borderRadius: '8px',
  padding: '2rem',
  display: 'flex',
  flexDirection: 'column'
};
const tradePanelContainerStyle = { backgroundColor: '#2D3748', borderRadius: '8px', padding: '2rem' };

const TradingPage = () => {
  const [allStocks, setAllStocks] = useState([]);
  const [selectedStock, setSelectedStock] = useState(null);
  const [portfolio, setPortfolio] = useState(null);
  const [account, setAccount] = useState(null);
  const [chartData, setChartData] = useState(null);
  const [stockStats, setStockStats] = useState(null);
  const [selectedRange, setSelectedRange] = useState('1Y');
  const [isLoading, setIsLoading] = useState({ stocks: true, chart: true, stats: true });
  const [error, setError] = useState(null);
  const [tradeCount, setTradeCount] = useState(0);

  useEffect(() => {
    const fetchInitialData = async () => {
      setIsLoading(prev => ({ ...prev, stocks: true }));
      try {
        const [stocksRes, portfolioRes, accountRes] = await Promise.all([
          apiService.get('/trading/stocks'),
          apiService.get('/trading/portfolio'),
          apiService.get('/accounts/my-account'),
        ]);
        setAllStocks(stocksRes.data);
        setPortfolio(portfolioRes.data);
        setAccount(accountRes.data);
        if (tradeCount === 0 && stocksRes.data.length > 0) {
          setSelectedStock(stocksRes.data[0]);
        }
      } catch (err) {
        setError('Failed to load trading data.');
      } finally {
        setIsLoading(prev => ({ ...prev, stocks: false }));
      }
    };
    fetchInitialData();
  }, [tradeCount]);

  useEffect(() => {
    if (!selectedStock) return;
    const fetchStockDetails = async () => {
      setIsLoading(prev => ({ ...prev, chart: true, stats: true }));
      try {
        const [historyRes, statsRes] = await Promise.all([
            apiService.get(`/trading/stocks/${selectedStock.tickerSymbol}/history`, { params: { range: selectedRange } }),
            apiService.get(`/trading/stocks/${selectedStock.tickerSymbol}/stats`)
        ]);
        setChartData({
          labels: historyRes.data.map(p => p.date),
          datasets: [{ data: historyRes.data.map(p => p.value), borderColor: '#14B8A6', backgroundColor: 'rgba(20, 184, 166, 0.1)', borderWidth: 2, pointRadius: 0, fill: true }],
        });
        setStockStats(statsRes.data);
      } catch (err) {
        console.error("Failed to load stock details", err);
        setChartData(null);
        setStockStats(null);
      } finally {
        setIsLoading(prev => ({ ...prev, chart: false, stats: false }));
      }
    };
    fetchStockDetails();
  }, [selectedStock, selectedRange]);

  const chartOptions = { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false }, tooltip: { mode: 'index', intersect: false, callbacks: { label: (ctx) => `${ctx.dataset.label || ''}: ${new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(ctx.parsed.y)}` } } }, scales: { x: { type: 'time', time: { unit: 'month' }, ticks: { color: '#A0AEC0' }, grid: { color: 'rgba(160, 174, 192, 0.1)' } }, y: { ticks: { color: '#A0AEC0' }, grid: { color: 'rgba(160, 174, 192, 0.1)' } } }, interaction: { mode: 'index', intersect: false } };

  if (isLoading.stocks) return <p>Loading trading terminal...</p>;
  if (error) return <p style={{ color: '#F56565' }}>{error}</p>;

  return (
    <div style={tradingPageStyle}>
      <div style={stockListStyle}>
        <h3 style={{ marginTop: 0 }}>Watchlist</h3>
        {allStocks.map(stock => (
          <div key={stock.tickerSymbol} style={stockListItemStyle(selectedStock?.tickerSymbol === stock.tickerSymbol)} onClick={() => setSelectedStock(stock)}>
            <strong>{stock.tickerSymbol}</strong>
            <p style={{ margin: '0.25rem 0 0', fontSize: '0.8rem', color: selectedStock?.tickerSymbol === stock.tickerSymbol ? '#2D3748' : 'var(--text-dark)' }}>{stock.companyName}</p>
          </div>
        ))}
      </div>
      <div style={mainContentStyle}>
        {selectedStock && (
          <>
            <div style={chartContainerStyle}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2>{selectedStock.tickerSymbol} - {selectedStock.companyName}</h2>
                <TimeframeSelector selectedRange={selectedRange} onSelectRange={setSelectedRange} />
              </div>
              <StockChart chartData={chartData} chartOptions={chartOptions} isLoading={isLoading.chart} />
            </div>
            <StockStatsPanel stats={stockStats} isLoading={isLoading.stats} />
            <div style={tradePanelContainerStyle}>
              <TradePanel selectedStock={selectedStock} portfolio={portfolio} account={account} onTradeSuccess={() => setTradeCount(c => c + 1)} />
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default TradingPage;