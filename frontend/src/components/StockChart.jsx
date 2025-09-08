import React from 'react';
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
  TimeScale,
  Filler,
} from 'chart.js';
import 'chartjs-adapter-date-fns';

ChartJS.register(
  CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, TimeScale, Filler
);

const StockChart = ({ chartData, chartOptions, isLoading }) => {
  if (isLoading) {
    return <p style={{ color: 'var(--text-dark)', textAlign: 'center' }}>Loading chart...</p>;
  }
  if (!chartData) {
    return <p style={{ color: 'var(--text-dark)', textAlign: 'center' }}>No data available.</p>;
  }
  
  // The only change is adding flex: '1' to this div's style
  return (
    <div style={{ position: 'relative', flex: '1', height: '100%', width: '100%' }}>
      <Line options={chartOptions} data={chartData} />
    </div>
  );
};

export default StockChart;