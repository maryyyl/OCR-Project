<template>
  <div class="chart-container">
    <h2>📊 Број на книги по автор</h2>
    <Bar v-if="chartData" :chart-data="chartData" :chart-options="chartOptions"/>
    <p v-else>Loading chart...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

// vue-chartjs imports
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from "chart.js";

import { Bar } from "vue-chartjs"; // <-- ова е вистинскиот компонент

// регистрирање Chart.js компоненти
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

const chartData = ref(null);
const chartOptions = {
  responsive: true,
  plugins: {
    legend: { display: false },
    title: { display: true, text: "Број на книги по автор" }
  }
};

async function fetchChartData() {
  try {
    const res = await axios.get("/api/documents/author-stats");
    const labels = res.data.map(item => item.author);
    const data = res.data.map(item => item.count);

    chartData.value = {
      labels: labels,
      datasets: [
        {
          label: "Број на книги",
          data: data,
          backgroundColor: "#4f46e5"
        }
      ]
    };
  } catch (err) {
    console.error("Грешка при fetch на chart data", err);
  }
}

onMounted(() => {
  fetchChartData();
});
</script>