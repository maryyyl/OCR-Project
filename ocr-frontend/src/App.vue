<script setup>

</script>

<template>

  <router-view />
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-left">
        <h3>OCR Платформа</h3>
        <p>Претворај PDF во editable документ брзо и прецизно.</p>
      </div>


      <div class="footer-right">
        <p>Верзија 1.0.0</p>
        <p>© 2026 Марија Коцевска</p>
        <p><a target="_blank" href="https://www.finki.ukim.mk/">ФИНКИ</a></p>
        <p>Изградено со Spring Boot & Vue.js</p>
      </div>
    </div>
  </footer>

</template>

<style scoped>
.app-footer {
  width: 1250px;
  background: #2d6b62;
  opacity: 0.85;
  color: #e5e7eb;
  padding: 40px 60px;
  margin-top: 60px;
  font-family: 'Segoe UI', sans-serif;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
}

.footer-left h3 {
  margin-bottom: 10px;
  font-size: 20px;
}

.footer-left p {
  font-size: 14px;
  opacity: 0.8;
  max-width: 250px;
}

.footer-center {
  display: flex;
  gap: 40px;
}

.footer-right p {
  font-size: 13px;
  margin: 4px 0;
  opacity: 0.8;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
    align-items: center;
  }

}
.footer-right a{
  color: white;
}
.footer-right a:hover {
  color: #FAD0CF;
  transition: color 0.2s;
}
</style>
