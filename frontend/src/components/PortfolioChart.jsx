import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import apiService from '../services/apiService';

// Register the components Chart.js needs
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const chartContainerStyle = {
  backgroundColor: '#2D3748',
  borderRadius: '8px',
  padding: '1.5rem',
  marginTop: '1rem',
};

const titleStyle = {
  marginTop: '0',
  marginBottom: '1.5rem',
  color: 'var(--text-light)',
  fontSize: '1.1rem',
  fontWeight: '500',
};

const PortfolioChart = () => {
  const [chartData, setChartData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await apiService.get('/trading/portfolio/history');
        const history = response.data;

        setChartData({
          labels: history.map(point => new Date(point.date).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' })),
          datasets: [
            {
              label: 'Portfolio Value',
              data: history.map(point => point.value),
              borderColor: '#14B8A6',
              backgroundColor: 'rgba(20, 184, 166, 0.1)',
              tension: 0.1,
              borderWidth: 2,
              pointRadius: 0,
              fill: true,
            },
          ],
        });
      } catch (err) {
        setError('Could not load chart data.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchHistory();
  }, []);

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
    },
    scales: {
      x: {
        ticks: { color: '#A0AEC0' },
        grid: { color: 'rgba(160, 174, 192, 0.1)' },
      },
      y: {
        ticks: { color: '#A0AEC0' },
        grid: { color: 'rgba(160, 174, 192, 0.1)' },
      },
    },
  };

  if (isLoading) {
    return <div style={chartContainerStyle}><p style={{ color: 'var(--text-dark)' }}>Loading Chart...</p></div>;
  }
  if (error) {
    return <div style={chartContainerStyle}><p style={{ color: '#F56565' }}>{error}</p></div>;
  }

  return (
    <div style={chartContainerStyle}>
      <h3 style={titleStyle}>30-Day Net Worth (₹)</h3>
      <div style={{ height: '350px' }}>
        {chartData && <Line options={options} data={chartData} />}
      </div>
    </div>
  );
};

export default PortfolioChart;