<template>
  <div class="stats-container">
    <h2>Статистики</h2>
    <div class="cards">
      <div class="card">
        <div class="number">{{ stats.totalDocuments }}</div>
        <div class="label">Вкупно обработени документи</div>
      </div>
      <div class="card">
        <div class="number">{{ (stats.averageProcessingTimeMs / 1000).toFixed(2) }} s</div>
        <div class="label">Просечно време на процесирање</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import axios from "axios";

const stats = ref({totalDocuments: 0,averageProcessingTimeMs :0});
let intervalId = null;

async function fetchStats() {
  try {
    const res = await axios.get("http://localhost:8080/api/documents/stats");
    stats.value = res.data;
  } catch (e) {
    console.error("Грешка при вчитување на статистики:", e);
  }
}

onMounted(() => {
  fetchStats();
  intervalId = setInterval(fetchStats, 5000);
});

onUnmounted(() => {
  clearInterval(intervalId);
});
</script>

<style scoped>
.stats-container {
  max-width: 600px;
  margin: 40px auto;
  padding: 20px;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #2c2c2c;
}

.cards {
  display: flex;
  justify-content: space-around;
  gap: 20px;
  flex-wrap: wrap;
}

.card {
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 25px 20px;
  text-align: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  flex: 1 1 250px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

.number {
  font-size: 2.5rem;
  font-weight: bold;
  color: #44A194;
  transition: all 0.5s ease-in-out;
}

.label {
  margin-top: 8px;
  font-size: 1rem;
  color: #555;
}
h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #44A194;
  font-size: 2rem;
  font-weight: 600;
  letter-spacing: 0.02em;
}
</style>